/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package anindyaju99.destruct.rules;

import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author anindya
 */
public class RuleTree {

    private String pattern = null;
    private String name = null;
    private HashMap<String, RuleTree> children = null;
    private Action preAction = null;
    private Action postAction = null;

    public RuleTree(String name) {
        this.name = name;
    }

    public void setPreAction(Action a) {
        preAction = a;
    }
    public void setPostAction(Action a) {
        postAction = a;
    }

    public void addChild(RuleTree node) {
        if (children == null) {
            children = new HashMap<String, RuleTree>();
        }
        children.put(node.getName(), node);
    }

    public void setPattern(String pat) {
        pattern = pat;
    }

    public String getName() {
        return name;
    }

    public String getPattern() {
        return pattern;
    }

    public RuleTree getChild(String childName) {
        return children.get(childName);
    }

    public Iterator<RuleTree> iterator() {
        if (children == null) {
            return null;
        }
        return children.values().iterator();
    }

    public Action getPreAction() {
        return preAction;
    }
    public Action getPostAction() {
        return postAction;
    }

    private void print(String msg) {
        System.out.println(msg);
    }

    public void print() {
        print("NODE " + name);
        print("PAT=" + getPattern());

        Iterator<RuleTree> iter = iterator();
        if (iter != null) {
            print("CHILDREN");
            while (iter.hasNext()) {
                RuleTree child = iter.next();
                child.print();
            }
            print("ENDCHILDREN");
        }
        if (preAction != null) {
            preAction.print();
        }
        if (postAction != null) {
            postAction.print();
        }

        print("ENDNODE");
    }
}
