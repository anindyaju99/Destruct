/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anindyaju99.destruct.rules;

import java.util.HashMap;

/**
 *
 * @author anindya
 */
public class EvalContext {
    private HashMap<String, CmdArg> values = null;
    public EvalContext() {
        values = new HashMap<String, CmdArg>();
    }
    public void setValue(String var, CmdArg val) {
        values.put(var, val);
    }
    public CmdArg getValue(String var) {
        return values.get(var);
    }
}
