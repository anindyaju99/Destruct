/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package anindyaju99.destruct.rules;

import anindyaju99.destruct.common.FileDownload;
import anindyaju99.destruct.main.ExtractedNode;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 *
 * @author anindya
 */
public class RuleTreeExtractor {

    private RuleTree ruleTree = null;
    private List<ExtractedNode> nodeStack = null;
    private String thisURL = null;

    public RuleTreeExtractor(String ruleFile)
            throws Exception {

        RuleTreeParser parser = new RuleTreeParser();
        ruleTree = parser.parse(ruleFile);
        ruleTree.print();
    }

    private void pushExtractedNode(ExtractedNode node) {
        nodeStack.add(node);
    }

    private ExtractedNode popExtractedNode() {
        ExtractedNode node = nodeStack.remove(nodeStack.size() - 1);
        return node;
    }

    private ExtractedNode getCurrentScope() {
        ExtractedNode scope = popExtractedNode();
        pushExtractedNode(scope);
        return scope;
    }
    private ExtractedNode getTopScope() {
        ExtractedNode scope = nodeStack.get(0);
        return scope;
    }
    private ExtractedNode findScopeWithName(String name) {
        Iterator<ExtractedNode> iter = nodeStack.iterator();
        while (iter.hasNext()) {
            ExtractedNode node = iter.next();
            if (node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }

    private void copyChildrenToScope(ExtractedNode src, ExtractedNode target) {
        Iterator<String> iter = src.keyIterator();
            if (iter != null) {
                while (iter.hasNext()) {
                    String key = iter.next();
                    List<ExtractedNode> children = src.getChild(key);
                    if (children != null) {
                        Iterator<ExtractedNode> chIter = children.iterator();
                        while (chIter.hasNext()) {
                            ExtractedNode child = chIter.next();
                            target.addChild(child);
                        }
                    }
                }
            }
    }

    private ExtractedNode findSpecialVariableScope(String varName)
            throws Exception
    {
        ExtractedNode node = null;
        if (varName.equals("$TOP")) {
            node = getTopScope();
        } else if (varName.equals("$PARENT")) {
            ExtractedNode current = popExtractedNode();
            ExtractedNode parent = popExtractedNode();
            pushExtractedNode(parent);
            pushExtractedNode(current);
            node = parent;
        } else if (varName.equals("$SELF")) {
            node = getCurrentScope();
        } else {
            String scopeName = varName.substring(1);
            ExtractedNode scope = findScopeWithName(scopeName);
            node = scope;
            if (scope == null) {
                throw new Exception("Can't find named scope '" + scopeName + "' specified in FOLLOW");
            }
        }
        return node;
    }

    private void populateCorrectScope(ExtractedNode resultTop, CmdArg populateArg)
        throws Exception
    {
        String val = populateArg.toString();
        if (val.equals("$CHILDREN")) {
            ExtractedNode scope = getCurrentScope();
            copyChildrenToScope(resultTop, scope);
        } else {
            ExtractedNode scope = findSpecialVariableScope(val);
            copyChildrenToScope(resultTop, scope);
        }
    }
    private void populateEvalContext(EvalContext context, ExtractedNode node) {
        context.setValue("$VALUE", new StringArg(node.getValue()));
        context.setValue("$NAME", new StringArg(node.getName()));
    }
    private void buildExtractionTreeForFollow(ExtractedNode node, Cmd cmd)
            throws Exception
    {
        if (cmd.getType() != Cmd.CmdType.FOLLOW) {
            throw new Exception("Expected FOLLOW command");
        }
        CmdArg link = cmd.getArg("FOLLOW");
        CmdArg with = cmd.getArg("WITH");
        CmdArg populate = cmd.getArg("POPULATE");
        EvalContext evalContext = new EvalContext();
        populateEvalContext(evalContext, node);
        FileDownload fd = FileDownload.getInst();
        String nextURL = fd.createValidURL(thisURL, link.evaluate(evalContext));
        String file = fd.getDownloadedFile(nextURL);
        RuleTreeExtractor rex = new RuleTreeExtractor(with.evaluate(evalContext));
        ExtractedNode child = rex.collect(nextURL, file);

        //  now merge the new tree in the correct scope
        populateCorrectScope(child, populate);
    }

    private void processPrintAction(ExtractedNode node, Cmd cmd)
            throws Exception
    {
        if (cmd.getType() != Cmd.CmdType.PRINT) {
            throw new Exception("Expected PRINT command");
        }
        EvalContext context = new EvalContext();
        CmdArg prn = cmd.getArg("PRINT");
        populateEvalContext(context, node);
        String val = prn.evaluate(context);
        if (val.charAt(0) == '$') {
            if (val.equals("$CHILDREN")) {
                System.out.println("CHILDREN of " + node.getName());
                Iterator<List<ExtractedNode>> iter = node.iterator();
                if (iter != null) {
                    while (iter.hasNext()) {
                        List<ExtractedNode> list = iter.next();
                        Iterator<ExtractedNode> li = list.iterator();
                        while (li.hasNext()) {
                            li.next().print(2);
                        }
                    }
                }
                System.out.println("ENDCHILDREN of " + node.getName());
            } else {
                ExtractedNode scope = findSpecialVariableScope(val);
                scope.print(0);
            }
        } else {
            System.out.print(val);
        }
    }

    private void buildExtractionTreeForAction(ExtractedNode node, Action action)
            throws Exception
    {
        if (action == null) return;
        Iterator<Cmd> iter = action.iterator();
        while (iter.hasNext()) {
            Cmd cmd = iter.next();
            switch (cmd.getType()) {
                case FOLLOW: {
                    buildExtractionTreeForFollow(node, cmd);
                    break;
                }
                case PRINT: {
                    processPrintAction(node, cmd);
                    break;
                }
                default: {
                    throw new Exception("Unhandled command/bug");
                }
            }
        }
    }
    public void buildExtractionTreeWithRule(ExtractedNode node, Object domNode, RuleTree rule)
            throws Exception {
        pushExtractedNode(node);
        // pre action
        buildExtractionTreeForAction(node, rule.getPreAction());
        if (rule.iterator() == null) {
            // leaf
            node.setValue(domNode.toString());
        } else {
            Iterator<RuleTree> iter = rule.iterator();
            while (iter.hasNext()) {
                RuleTree childRule = iter.next();
                buildExtractionTreeInsideScope(node, (TagNode)domNode, childRule);
            }
        }
        // post action
        buildExtractionTreeForAction(node, rule.getPostAction());
        popExtractedNode();
    }

    public void buildExtractionTreeInsideScope(ExtractedNode parent, TagNode domNode, RuleTree rule)
            throws Exception {
        Object[] results = domNode.evaluateXPath(rule.getPattern());
        if (results == null || results.length == 0) {
            return;
        }
        for (int i = 0; i < results.length; i++) {
            Object res = results[i];
            ExtractedNode child = new ExtractedNode(rule.getName());
            parent.addChild(child);
            buildExtractionTreeWithRule(child, res, rule);
        }
    }

    public ExtractedNode collect(String url, String file)
            throws Exception
    {
        thisURL = url;
        nodeStack = new ArrayList<ExtractedNode>();
        InputStream in = new FileInputStream(file);
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode node = cleaner.clean(in);
        ExtractedNode top = new ExtractedNode(ExtractedNode.TOP_NAME);
        pushExtractedNode(top);
        buildExtractionTreeInsideScope(top, node, ruleTree);
        in.close();
        return top;
    }
}
