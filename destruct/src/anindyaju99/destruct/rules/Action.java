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
public class Action {
    private boolean preAction = false;
    private List<Cmd> sequence = null;

    public Action(boolean pre) {
        preAction = pre;
        sequence = new ArrayList<Cmd>();
    }
    public void addCmd(Cmd cmd) {
        sequence.add(cmd);
    }
    public Iterator<Cmd> iterator() {
        return sequence.iterator();
    }
    public boolean isPreAction() {
        return preAction;
    }
    private void print(String msg) {
        System.out.println(msg);
    }
    public void print() {
        if (preAction) {
            print("PREACTION");
        } else {
            print("POSTACTION");
        }

        Iterator<Cmd> iter = iterator();
        if (iter != null) {
        while (iter.hasNext()) {
            Cmd cmd = iter.next();
            cmd.print();
        }
            }
        if (preAction) {
            print("ENDPREACTION");
        } else {
            print("ENDPOSTACTION");
        }
    }
}
