/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anindyaju99.destruct.filter;

/**
 *
 * @author anindya
 */
public interface Filter {
    public enum FilterAction {
        VISIT,
        DONT_VISIT,
        END_LEVEL
    }
    public void init(String args) throws Exception;
    public FilterAction doVisit(String url, int depth, int childNum) throws Exception;
    public void visit(String filePath) throws Exception;
    public void end() throws Exception;
}
