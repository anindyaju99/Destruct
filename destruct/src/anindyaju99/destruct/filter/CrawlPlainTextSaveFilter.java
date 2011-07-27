/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anindyaju99.destruct.filter;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 *
 * @author anindya
 */
public class CrawlPlainTextSaveFilter extends CrawlFilter {
    private static FileWriter out = null;
    @Override
    public void init(String args) throws Exception {
        if (out == null) {
            String file = "plain_txt_out.txt";
            out = new FileWriter(file);
        }
        super.init(args);
    }
    private boolean isValidChar(char c) {
        if (c == '&' || c == '<' || c == '>'
                || c == '`' || c == '\t') {
            return false;
        }
        if ((c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c >= '0' && c <= '9')) {
            return true;
        }
        if ((c == '[') || (c == ']') || (c == '{') || (c == '}')
                || (c == ',') || (c == '.') || (c == '?') || (c == '_')
                || (c == '/') || (c == ' ') || (c == '\n')) {
            return true;
        }
        return false;
    }
    private String sanitizeText(String str) {
        int len = str.length();
        //debugPrint("text len : " + String.valueOf(len));
        char[] strCharArr = str.toCharArray();
        //debugPrint("done toarr");
        final int maxNewLineGap = 100;
        int newLineGap = 0;
        for (int i = 0; i < len; i++) {
            char c = strCharArr[i];
            if (!isValidChar(c)) {
                c = ' ';
            }
            if (newLineGap >= maxNewLineGap) {
                if (c == ' ') {
                    c = '\n';
                    newLineGap = 0;
                }
            }
            strCharArr[i] = c;
            if (c != '\n') {
                newLineGap++;
            }
        }
        //debugPrint("done sanitize");
        String newStr = String.valueOf(strCharArr);
        //debugPrint("done toStr");
        return newStr;
    }
    @Override
    public void visit(String filePath)
        throws Exception {
        InputStream in = new FileInputStream(filePath);
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode node = cleaner.clean(in);
        String text = node.getText().toString();
        out.write("========START OF " + filePath + " ========\n");
        text = sanitizeText(text);
        out.write(text);
        out.write("\n========END OF " + filePath + " ========\n");
        in.close();
        return;
    }
    @Override
    public void end()
            throws Exception {
        if (out != null) {
            out.close();
            out = null;
        }
    }
}
