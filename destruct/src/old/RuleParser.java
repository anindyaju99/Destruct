/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package old;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author anindya
 */
public class RuleParser {

    public class OneRule {

        public String name = null;
        public String rule = null;
    }
    private List<RuleParser.OneRule> ruleList = null;

    public RuleParser(String file)
            throws Exception {
        ruleList = new ArrayList<OneRule>();
        BufferedReader in = new BufferedReader(new FileReader(file));
        String inputLine;
        OneRule rule = null;
        while ((inputLine = in.readLine()) != null) {
            if (rule == null) {
                rule = new OneRule();
                rule.name = inputLine;
            } else {
                rule.rule = inputLine;
                ruleList.add(rule);
                rule = null;
            }
        }
        in.close();
    }
    public Iterator<OneRule> iterator() {
        return ruleList.iterator();
    }
}
