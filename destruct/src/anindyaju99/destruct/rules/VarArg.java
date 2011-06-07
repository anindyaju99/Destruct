/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anindyaju99.destruct.rules;

/**
 *
 * @author anindya
 */
public class VarArg  implements CmdArg {
    private String varName = null;
    public VarArg(String nm) {
        varName = nm;
    }
    public String evaluate(EvalContext context)
            throws Exception {
        CmdArg val = context.getValue(varName);
        if (val == null) {
            return toString();
        }
        return val.evaluate(context);
    }
    public String toString() {
        return varName;
    }

    public void print() {
        System.out.println(toString());
    }
}
