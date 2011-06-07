/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anindyaju99.destruct.main;

import java.io.PrintStream;

/**
 *
 * @author anindya
 */
public interface ExtractedNodePrinter {
    public void print(PrintStream out, int indent, ExtractedNode root)
            throws Exception;
}
