/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package old;

import anindyaju99.destruct.common.FileDownload;
import java.io.PrintStream;

/**
 *
 * @author anindya
 */
public class MainOld {

    /**
     * @param args the command line arguments
     */
    private static FileDownload fd = null;

    public static void main(String[] args) {
        // TODO code application logic here
        try {
            PrintStream out = new PrintStream("log.txt");
            fd = FileDownload.getInst();
            fd.loadCache();
            for (int i = 2000; i < 2003; i++) {
                String baseUrl = "http://www.imdb.com/year/";
                String url = baseUrl + i;

                String file = fd.getDownloadedFile(url);
                RuleExtractor re = new RuleExtractor("yr_page_rule.txt");
                re.collect(file);
                re.print(out);
                Thread.sleep(4000);
            }
            fd.saveCache();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
