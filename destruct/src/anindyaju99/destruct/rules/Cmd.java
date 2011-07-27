/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anindyaju99.destruct.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author anindya
 */
public class Cmd {
    public enum CmdType {
        PRINT,
        FOLLOW,
        PROCESS_VALUE,
        NONE
    }
    private CmdType type = CmdType.NONE;
    private HashMap<String, CmdArg> args = null;

    public Cmd(CmdType t) {
        type = t;
        args = new HashMap<String, CmdArg>();
    }
    public void addArg(String name, CmdArg arg) {
        if (args.get(name) != null) {
            Exception e = new Exception("Duplicate argument " + name);
            e.printStackTrace();
            System.exit(1);
        }
        args.put(name, arg);
    }
    public CmdArg getArg(String name) {
        return args.get(name);
    }
    public CmdType getType() {
        return type;
    }
    private void print(String msg) {
        System.out.println(msg);
    }
    public void print() {
        switch (type) {
            case PRINT: {
                print("PRINT");
                break;
            }
            case FOLLOW: {
                print("FOLLOW");
                break;
            }
            case PROCESS_VALUE: {
                print("PROCESS_VALUE");
                break;
            }
            default: {
                Exception e = new Exception("Invalid command");
                e.printStackTrace();
                System.exit(1);
            }
        }
        Iterator<String> iter = args.keySet().iterator();
        while (iter.hasNext()) {
            String cmdName = iter.next();
            print(cmdName + "=");
            CmdArg arg = getArg(cmdName);
            arg.print();
        }
    }
}
