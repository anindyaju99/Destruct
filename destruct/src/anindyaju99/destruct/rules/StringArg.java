/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anindyaju99.destruct.rules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author anindya
 */
public class StringArg implements CmdArg {
    private String value = null;
    public StringArg(String v) {
        value = v;
    }
    private List<CmdArg> tokenize()
        throws Exception
    {
        char[] str = value.toCharArray();
        int p = 0;
        List<CmdArg> tokens = new ArrayList<CmdArg>();
        String tok = "";
        boolean invar = false;
        boolean instr = true;
        while (p < str.length) {
            if (invar) {
                if (RuleTreeParser.isAlNumUS(str[p])) {
                    tok += str[p];
                    p++;
                    continue;
                } else {
                    invar = false;
                    instr = true;
                    CmdArg arg = new VarArg(tok);
                    tokens.add(arg);
                    tok = null;
                    continue;
                }
            } else if (instr) {
                if (str[p] != '$') {
                    tok += str[p];
                    p++;
                    continue;
                } else {
                    instr = false;
                    CmdArg arg = new StringArg(tok);
                    tokens.add(arg);
                    tok = null;
                    continue;
                }
            } else if(p == '$') {
                invar = true;
                tok = "$";
                p++;
            }
        }

        if (tok != null) {
            CmdArg arg = null;
            if (instr) {
                arg = new StringArg(tok);
            } else if (invar) {
                arg = new VarArg(tok);
            } else {
                throw new Exception("Unexpected condition/bug");
            }
            tokens.add(arg);
        }
        return tokens;
    }
    public String evaluate(EvalContext context)
            throws Exception {
        System.out.println("evaluating " + value);
        String evalVal = "";
        Iterator<CmdArg> iter = tokenize().iterator();
        while (iter.hasNext()) {
            CmdArg arg = iter.next();
            if (arg instanceof StringArg) {
                evalVal += arg.toString();
            } else {
                evalVal += arg.evaluate(context);
            }
        }
        return evalVal;
    }
    public String toString() {
        return value;
    }
    public void print() {
        System.out.println("\"" + toString() + "\"");
    }
}
