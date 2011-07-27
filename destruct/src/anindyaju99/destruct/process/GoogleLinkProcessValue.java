/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anindyaju99.destruct.process;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

/**
 *
 * @author anindya
 */
public class GoogleLinkProcessValue implements ProcessValue {
    public String processToStr(TagNode domNode, String text) {
        int i = text.indexOf(" ");
        if (i == -1) {
            return null;
        }
        String url = text.substring(0, i);
        if (url.indexOf("http://") == -1) {
            url = "http://" + url;
        }
        return url;
    }
    public TagNode processToDom(TagNode domNode) {
        return null;
    }
}