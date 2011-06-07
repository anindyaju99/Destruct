/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package anindyaju99.destruct.main;

import anindyaju99.destruct.common.FileDownload;
import java.io.PrintStream;
import anindyaju99.destruct.rules.RuleTreeExtractor;

/**
 *
 * @author anindya
 */
public class Main {

    private enum Format {

        JSON,
        TEXT,
        NONE
    }

    public static void exec(String url, String ruleFile,
            boolean useCache, boolean saveCache,
            int delay,
            Format format,
            PrintStream out)
            throws Exception {
        FileDownload fd = FileDownload.getInst();

        if (useCache) {
            fd.loadCache();
        }
        fd.setDelay(delay);
        RuleTreeExtractor rex = new RuleTreeExtractor(ruleFile);
        String file = fd.getDownloadedFile(url);
        ExtractedNode node = rex.collect(url, file);
        ExtractedNodePrinter printer = null;
        switch (format) {
            case JSON: {
                printer = new JSonPrinter();
                break;
            }
            case TEXT: {
                node.print(0);
                break;
            }
            default: {
                throw new Exception("Unknown output format");
            }
        }

        if (printer != null) {
            printer.print(out, 0, node);
        }

        if (saveCache) {
            fd.saveCache();
        }
    }

    public static void main(String[] args) {
        // TODO code application logic here

        String url = null;
        String ruleFile = null;
        boolean saveCache = true;
        boolean loadCache = true;
        int delay = 0;
        String log = null;
        Format format = Format.TEXT;
        String outFile = null;
        System.out.println("Help : -url url -rule ruleFile [-format json|text] [-o outfile] [-d delay] [-l log] [-nosavecache] [-noloadcache]");
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-url")) {
                i++;
                url = args[i];
            } else if (args[i].equals("-rule")) {
                i++;
                ruleFile = args[i];
            } else if (args[i].equals("-nosavecache")) {
                saveCache = false;
            } else if (args[i].equals("-noloadcache")) {
                loadCache = false;
            } else if (args[i].equals("-d")) {
                i++;
                Integer t = new Integer(args[i]);
                delay = t.intValue();
            } else if (args[i].equals("-l")) {
                i++;
                log = args[i];
            } else if (args[i].equals("-format")) {
                i++;
                if (args[i].equals("json")) {
                    format = Format.JSON;
                } else if (args[i].equals("text")) {
                    format = Format.TEXT;
                } else {
                    System.out.println("Format error. Invalid format " + args[i]);
                    System.exit(1);
                }
            } else if (args[i].equals("-o")) {
                i++;
                outFile = args[i];
            } else {
                System.out.println("Invalid command " + args[i]);
                System.exit(1);
            }
        }
        try {
            PrintStream out = null;
            if (log != null) {
                out = new PrintStream(log);
                System.setOut(out);
            }
            PrintStream output = null;
            if (outFile != null) {
                output = new PrintStream(outFile);
            }
            exec(url, ruleFile, loadCache, saveCache, delay, format,
                    (output != null) ? output : System.out);
            if (output != null) {
                output.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
