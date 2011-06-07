/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anindyaju99.destruct.main;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author anindya
 */
public class JSonPrinter implements ExtractedNodePrinter {
    private PrintStream out = null;
    private void print(int indent, String msg) {
        if (indent != 0) {
            for (int i = 0; i < indent; i++) {
                out.print(" ");
            }
        }
        out.println(msg);
    }
    private void printChildren(ExtractedNode node, int indent)
        throws Exception
    {
        Iterator<List<ExtractedNode>> iter = node.iterator();
        if (iter == null) {
            return;
        }
        print(indent, "children : {");
        while (iter.hasNext()) {
            List<ExtractedNode> list = iter.next();
            String name = list.get(0).getName();
            print(indent + 2, name + ": [");
            Iterator<ExtractedNode> listi = list.iterator();
            while (listi.hasNext()) {
                ExtractedNode child = listi.next();
                if (!name.equals(child.getName())) {
                    throw new Exception("Bug - Child name does not match other children in the category");
                }
                print(indent+2, child);
            }
            print(indent+2, "],");
        }
        print(indent, "},");
    }
    private void print(int indent, ExtractedNode root)
            throws Exception
    {
        print(indent, "{");
        String str = "name : \"" + root.getName() + "\",";
        print(indent + 2, str);
        str = "value : " + ((root.getValue() != null)? ("\"" + root.getValue() + "\""): "null") + ",";
        print(indent+2, str);
        printChildren(root, indent+2);
        print(indent, "}");
    }
    public void print(PrintStream out, int indent, ExtractedNode root)
            throws Exception
    {
        this.out = out;
        print(indent, root);
    }
}
