/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package old;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 *
 * @author anindya
 */
public class RuleExtractor {
    private RuleParser rules = null;
    private HashMap<String, List<String>> results = null;
    public RuleExtractor(String ruleFile)
        throws Exception
    {
        rules = new RuleParser(ruleFile);
    }
    public void print(PrintStream out) {
        Iterator<RuleParser.OneRule> iter = this.rules.iterator();
        while (iter.hasNext()) {
            RuleParser.OneRule rule = iter.next();
            List<String> res = this.getResult(rule.name);
            out.println("RULE " + rule.name);
            Iterator<String> resI = res.iterator();
            while (resI.hasNext()) {
                out.println("ONE_RESULT");
                out.println(resI.next());
                out.println("ONE_RESULT_END");
            }
            out.println("RULE " + rule.name + " END");
        }
    }
    private void oneExtract(RuleParser.OneRule rule, TagNode node)
        throws Exception
    {
        List res = new ArrayList<String>();
        Object[] myNodes = node.evaluateXPath(rule.rule);
        for (int i = 0; i < myNodes.length; i++) {
            res.add(myNodes[i].toString());
            //debugPrint(myNodes[i].toString());
        }
        results.put(rule.name, res);
    }
    public void collect(String file) throws Exception
    {
        results = new HashMap<String, List<String>>();
        InputStream in = new FileInputStream(file);
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode node = cleaner.clean(in);
        Iterator<RuleParser.OneRule> iter = this.rules.iterator();
        while (iter.hasNext()) {
            oneExtract(iter.next(), node);
        }
        in.close();
    }
    public List<String> getResult(String rule) {
        return results.get(rule);
    }

}
