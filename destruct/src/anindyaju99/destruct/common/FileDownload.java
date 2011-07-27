/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package anindyaju99.destruct.common;

//import anindyaju99.destruct.common.Config;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author anindya
 */
public class FileDownload {

    private static FileDownload inst = null;
    private HashMap<String, String> results = null; // url -> local file
    private String baseDir = null;
    private int delay = 0;

    private FileDownload(String dir) {
        results = new HashMap<String, String>();
        baseDir = dir;
    }

    public void setDelay(int d) {
        delay = d;
    }

    private void execDelay()
        throws Exception
    {
        if (delay == 0)
            return;
        Thread.sleep(delay);
    }

    public static FileDownload getInst() {
        if (inst == null) {
            inst = new FileDownload(Config.DUMP_PATH);
        }
        return inst;
    }

    private void fetch(URL url, PrintStream out)
            throws Exception {
        execDelay();
        HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
        httpcon.addRequestProperty("User-Agent", "Mozilla/3.76");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                httpcon.getInputStream()));
        //url.openStream()));

        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            out.println(inputLine);
        }

        in.close();
    }

    private void downloadFile(String url, String file)
            throws Exception {
        URL u = new URL(url);
        PrintStream out = new PrintStream(file);
        fetch(u, out);
        out.close();
    }

    public String getDownloadedFile(String url)
            throws Exception {
        /*if (url.indexOf("://") == -1) {
            url = "http://" + url;
        }*/
        String file = results.get(url);
        if (file == null) {
            System.out.println("Downloading URL " + url);
            Integer sz = new Integer(results.size() + 1);
            file = baseDir + "cache_" + sz + ".txt";
            downloadFile(url, file);
            results.put(url, file);
        } else {
            System.out.println("Found in cache URL " + url);
        }
        return file;
    }


    public void loadCache()
        throws Exception
    {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(Config.DUMP_CACHE));
        }
        catch (FileNotFoundException e) {
            return;
        }
        String line = null;
        boolean isUrl = true;
        String url = null;
        String file = null;
        while ((line = in.readLine()) != null) {
            if (isUrl) {
                url = line;
                isUrl = false;
            } else {
                file = line;
                isUrl = true;
                results.put(url, file);
            }
        }
        in.close();
    }

    public void saveCache()
        throws Exception
    {
        Iterator<String> iter = results.keySet().iterator();
        PrintStream out = new PrintStream(Config.DUMP_CACHE);
        while (iter.hasNext()) {
            String line = iter.next();
            out.println(line);
            line = results.get(line);
            out.println(line);
        }
        out.close();
    }

    public String createValidURL(String parent, String next)
            throws Exception
    {
        if (next.startsWith("http") ||
                next.startsWith("ftp")) {
            // absolute URL
            return next;
        } else if (next.length() == 0) {
            return parent;
        } else if (next.charAt(0) == '/') {
            // right after the domain
            int domainStart = parent.indexOf("://") + 3;
            if (domainStart == 2) {
                // indexof returned -1
                throw new Exception("Bug- Invalid parent url '" + parent + "'");
            }
            int domainEnd = parent.indexOf('/', domainStart);
            String homeURL = null;
            if (domainEnd == -1) {
                homeURL = parent;
            } else {
                homeURL = parent.substring(0, domainEnd);
                if (homeURL.charAt(homeURL.length() - 1) == '/') {
                    throw new Exception("Bug");
                }
            }
            return homeURL + next;
        } else {
            // relative url
            int dirEnd = parent.lastIndexOf('/');
            int protoEnd = parent.indexOf("://");
            String dirURL = null;
            if (dirEnd == -1 || protoEnd + 2 == dirEnd) {
                //throw new Exception("Bug - Invalid parent url '" + parent + "'");
                dirURL = parent + "/";
            } else {
                dirURL = parent.substring(0, dirEnd+1);
            }
            return dirURL + next;
        }
    }

    public String normalizeURL(String url)
        throws Exception
    {
        String retUrl = url;
        if (retUrl.indexOf("#") != -1) {
            int hash = retUrl.indexOf("#");
            retUrl = retUrl.substring(0, hash);
        }
        return retUrl;
    }
}
