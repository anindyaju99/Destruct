/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anindyaju99.destruct.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
//import anindyaju99.destruct.rules.Action;
import org.htmlcleaner.TagNode;

/**
 *
 * @author anindya
 */
public class ExtractedNode {
    public static String TOP_NAME = "__INT_TOP";
    private String name = null;
    private String value = null;
    private TagNode domNode = null;
    private HashMap<String, List<ExtractedNode>> children = null;
    
    public ExtractedNode(String name) {
        this.name = name;
    }

    private void print(int indent, String msg) {
        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }
        System.out.println(msg);
    }
    public void print(int indent)
    {
        print(indent, "{");
        indent += 2;
        print(indent, name);
        if (value != null) {
            print(indent, "value : '" + value + "'");
        }
        if (children != null) {
            Iterator<List<ExtractedNode>> iter = children.values().iterator();
            while (iter.hasNext()) {
                List<ExtractedNode> list = iter.next();
                Iterator<ExtractedNode> listIter = list.iterator();
                while (listIter.hasNext()) {
                    ExtractedNode node = listIter.next();
                    node.print(indent);
                }
            }
        }
        print(indent, "}");
    }
    public void setDomNode(TagNode n) {
        domNode = n;
    }
    public TagNode getDomNode() {
        return domNode;
    }
    public void setValue(String v) {
        value = v;
    }
    public void addChild(ExtractedNode child) {
        if (children == null) {
            children = new HashMap<String, List<ExtractedNode>>();
        }
        List<ExtractedNode> list = children.get(child.getName());
        if (list == null) {
            list = new ArrayList<ExtractedNode>();
            children.put(child.getName(), list);
        }
        list.add(child);
    }
    public String getName() {
        return name;
    }
    public String getValue() {
        return value;
    }
    public List<ExtractedNode> getChild(String name) {
        if (children == null) {
            return null;
        }
        return children.get(name);
    }
    public Iterator<List<ExtractedNode>> iterator() {
        if (children == null) return null;
        return children.values().iterator();
    }
    public Iterator<String> keyIterator() {
        if (children == null) return null;
        return children.keySet().iterator();
    }
}
