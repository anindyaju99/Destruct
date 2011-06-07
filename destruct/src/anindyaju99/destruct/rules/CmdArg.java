/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anindyaju99.destruct.rules;

/**
 *
 * @author anindya
 */
public interface CmdArg {
    public String evaluate(EvalContext context)
            throws Exception;
    public String toString();
    public void print();
}
