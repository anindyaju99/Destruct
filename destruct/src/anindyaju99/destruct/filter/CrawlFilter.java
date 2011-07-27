/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anindyaju99.destruct.filter;

import java.util.HashSet;
import java.util.regex.Pattern;

/**
 *
 * @author anindya
 */
public class CrawlFilter implements Filter {
    private int maxDepth = 0;
    private int maxChildNum = 0;
    private static HashSet<String> visited = null;
    public void init(String args) throws Exception {
        if (visited == null) {
            visited = new HashSet<String>();
        }
        ArgsParser p = new ArgsParser();
        String[] a = p.parseCommaSep(args);
        if (a.length != 2) {
            throw new Exception("Invalid argument " + args);
        }
        maxDepth = Integer.parseInt(a[0]);
        maxChildNum = Integer.parseInt(a[1]);
    }
    public FilterAction doVisit(String url, int depth, int childNum)
        throws Exception {
        if (visited.contains(url)) {
            return FilterAction.DONT_VISIT;
        }
        FilterAction ok = FilterAction.VISIT;
        Pattern pat = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
            + "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v|pdf"
            + "|rm|smil|wmv|swf|wma|zip|rar|gz|doc|docx|xls))$");
        if (pat.matcher(url).matches()) {
                ok = FilterAction.DONT_VISIT;
        } else if (url.indexOf("www.facebook.com") != -1) {
            // don't follow FB links
            ok = FilterAction.DONT_VISIT;
        } else if (maxDepth != -1 && depth > maxDepth) {
            ok = FilterAction.END_LEVEL;
        } else if (maxChildNum != -1 && childNum > maxChildNum) {
            ok = FilterAction.END_LEVEL;
        }
        if (ok == FilterAction.VISIT || ok == FilterAction.DONT_VISIT) {
            visited.add(url);
        }
        return ok;
    }
    public void visit(String filePath)
        throws Exception {
        return;
    }
    public void end()
        throws Exception {
        return;
    }
}
