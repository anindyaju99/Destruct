/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anindyaju99.destruct.process;

import org.htmlcleaner.TagNode;

/**
 *
 * @author anindya
 */
public interface ProcessValue {
    public String processToStr(TagNode domNode, String text);
    public TagNode processToDom(TagNode domNode);
}
