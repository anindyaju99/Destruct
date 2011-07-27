/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package anindyaju99.destruct.rules;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

/**
 *
 * @author anindya
 */
public class RuleTreeParser {
    private enum TokenType {
        NODE,
        PATTERN,
        ID,
        CHILDREN,
        ENDCHILDREN,
        ENDNODE,
        EQ,
        NL,
        EOF,
        PREACTION,
        POSTACTION,
        ENDPREACTION,
        ENDPOSTACTION,
        PRINT,
        FOLLOW,
        WITH,
        POPULATE,
        VARIABLE,
        STRING,
        FILTER,
        FILTER_ARGS,
        PROCESS_VALUE,
        TO_STR,
        TO_DOM,
        NONE
    }

    private String getTokenTypeString(TokenType t) {
        switch (t) {
            case NODE: return "NODE";
            case PATTERN: return "PATTERN";
            case ID: return "ID";
            case CHILDREN: return "CHILDREN";
            case ENDCHILDREN: return "ENDCHILDREN";
            case ENDNODE: return "ENDNODE";
            case EQ: return "=";
            case NL: return "new line";
            case EOF: return "End of file";
            case PREACTION: return "PREACTION";
            case POSTACTION: return "POSTACTION";
            case ENDPREACTION: return "ENDPREACTION";
            case ENDPOSTACTION: return "ENDPOSTACTION";
            case PRINT: return "PRINT";
            case FOLLOW: return "FOLLOW";
            case WITH: return "WITH";
            case POPULATE: return "POPULATE";
            case VARIABLE: return "VARIABLE";
            case STRING: return "STRING";
            case FILTER: return "FILTER";
            case FILTER_ARGS: return "FILTER_ARGS";
            case PROCESS_VALUE: return "PROCESS_VALUE";
            case TO_STR: return "TO_STR";
            case TO_DOM: return "TO_DOM";
            default: error("Unexpected token requested for printing");
        }
        return null;
    }

    private class Token {
        public TokenType type = TokenType.NONE;
        public String val = null;

        public Token(TokenType type, String val) {
            this.type = type;
            this.val = val;
        }

        public Token(TokenType type) {
            this.type = type;
            this.val = null;
        }
    }

    public RuleTreeParser() {
    }

    private static HashMap<String, RuleTree> ruleTreeCache = null;

    public RuleTree parse(String ruleFile)
            throws Exception {
        if (ruleTreeCache != null) {
            RuleTree t = ruleTreeCache.get(ruleFile);
            if (t != null) {
                return t;
            }
        }
        BufferedReader in = new BufferedReader(new FileReader(ruleFile));
        loadBuffer(in);
        in.close();
        RuleTree root = parseNode();
        consumeEOF();
        lexBuffer = null;
        bufferPos = 0;
        if (ruleTreeCache == null) {
            ruleTreeCache = new HashMap<String, RuleTree>();
        }
        ruleTreeCache.put(ruleFile, root);
        return root;
    }
    private char[] lexBuffer = null;
    private int bufferPos = 0;
    private int lineNo = 1;

    private void loadBuffer(BufferedReader in)
            throws Exception {
        String line = in.readLine();
        String text = line;
        while ((line = in.readLine()) != null) {
            text += "\n" + line;
        }
        lexBuffer = text.toCharArray();
    }

    private boolean isEOF() {
        if (bufferPos >= lexBuffer.length) {
            return true;
        }
        return false;
    }

    private char currentChar() {
        error(!isEOF(), "End of FILE reached.");
        return lexBuffer[bufferPos];
    }

    private void moveNext() {
        bufferPos++;
    }

    private void movePrev() {
        bufferPos--;
    }

    private void putBack(String val) {
        bufferPos -= val.length();
    }

    private void putBack(Token tok) {
        switch (tok.type) {
            case ID: {
                putBack(tok.val);
                break;
            }
            case NL: {
                error("Unhandled putBack request for token" + getTokenTypeString(tok.type));
                break;
            }
            case EOF: {
                error("Unhandled putBack request for token" + getTokenTypeString(tok.type));
                break;
            }
            default: {
                //error("Unhandled putBack request for token" + getTokenTypeString(tok.type));
                putBack(getTokenTypeString(tok.type));
            }
        }
    }

    public static boolean isWS(char c) {
        if (c == ' ' || c == '\t') {
            return true;
        }
        if (isNL(c)) {
            return true;
        }
        return false;
    }

    public static boolean isNL(char c) {
        if (c == '\n' || c == '\r') {
            return true;
        }
        return false;
    }

    private void skipWS() {
        while (isWS(currentChar())) {
            moveNext();
        }
    }

    public static boolean isAlNumUS(char c) {
        if (c >= 'a' && c <= 'z') {
            return true;
        }
        if (c >= 'A' && c <= 'Z') {
            return true;
        }
        if (c >= '0' && c <= '9') {
            return true;
        }
        if (c == '_') {
            return true;
        }
        return false;
    }

    private String getTextTillNL() {
        String tok = "";
        char c = currentChar();
        while (!isNL(c)) {
            moveNext();
            if (isEOF()) {
                return tok;
            }
            tok += c;
            c = currentChar();
        }
        return tok;
    }

    private String nextVariable() {
        String v = "$";
        char c = currentChar();
        error((c == '$'), "Expecting '$'");
        moveNext();
        c = currentChar();
        while (isAlNumUS(c)) {
            v += c;
            moveNext();
            if (isEOF()) {
                break;
            }
            c = currentChar();
        }
        return v;
    }

    private String nextString() {
        String s = "";
        char c = currentChar();
        error(c == '"', "Expecting string");

        while (true) {
            moveNext();
            if (isEOF()) {
                error(false, "Unexpected EOF");
                break;
            }
            c = currentChar();
            if (c == '"') {
                moveNext();
                break;
            }
            s += c;
        }
        return s;
    }

    private Token nextToken() {
        skipWS();
        if (isEOF()) {
            return new Token(TokenType.EOF);
        }
        char c = currentChar();
        String tok = "";

        boolean isID = false;
        if (c == '$') {
            String var = nextVariable();
            return new Token(TokenType.VARIABLE, var);
        } else if (c == '"') {
            String s = nextString();
            return new Token(TokenType.STRING, s);
        } else if (c == '#') {
            getTextTillNL();
        }
        while (isAlNumUS(c)) {
            isID = true;
            tok += c;
            moveNext();
            if (isEOF()) {
                break;
            }
            c = currentChar();
        }
        if (!isID) {
            if (c == '=') {
                moveNext();
                return new Token(TokenType.EQ);
            } else {
                error("Unexpected input.");
            }
        }
        if (tok.equals("NODE")) {
            return new Token(TokenType.NODE);
        } else if (tok.equals("PAT")) {
            return new Token(TokenType.PATTERN);
        } else if (tok.equals("CHILDREN")) {
            return new Token(TokenType.CHILDREN);
        } else if (tok.equals("ENDCHILDREN")) {
            return new Token(TokenType.ENDCHILDREN);
        } else if (tok.equals("ENDNODE")) {
            return new Token(TokenType.ENDNODE);
        } else if (tok.equals("PREACTION")) {
            return new Token(TokenType.PREACTION);
        } else if (tok.equals("POSTACTION")) {
            return new Token(TokenType.POSTACTION);
        } else if (tok.equals("ENDPREACTION")) {
            return new Token(TokenType.ENDPREACTION);
        } else if (tok.equals("ENDPOSTACTION")) {
            return new Token(TokenType.ENDPOSTACTION);
        } else if (tok.equals("WITH")) {
            return new Token(TokenType.WITH);
        } else if (tok.equals("POPULATE")) {
            return new Token(TokenType.POPULATE);
        } else if (tok.equals("FOLLOW")) {
            return new Token(TokenType.FOLLOW);
        } else if (tok.equals("PRINT")) {
            return new Token(TokenType.PRINT);
        } else if (tok.equals("FILTER")) {
            return new Token(TokenType.FILTER);
        } else if (tok.equals("FILTER_ARGS")) {
            return new Token(TokenType.FILTER_ARGS);
        } else if (tok.equals("PROCESS_VALUE")) {
            return new Token(TokenType.PROCESS_VALUE);
        } else if (tok.equals("TO_STR")) {
            return new Token(TokenType.TO_STR);
        } else if (tok.equals("TO_DOM")) {
            return new Token(TokenType.TO_DOM);
        }

        return new Token(TokenType.ID, tok);
    }

    private Token nextToken(TokenType type) {
        Token tok = nextToken();
        error(tok.type == type, getTokenTypeString(type));
        return tok;
    }

    private void consumeEOF() {

        while (true) {
            if (isEOF()) {
                break;
            }
            skipWS();
            if (isEOF()) {
                break;
            }
            getTextTillNL();
            if (isEOF()) {
                break;
            }
        }

        error(isEOF(), "EOF expected");
    }

    private String parsePattern() {
        Token tok = nextToken();
        error(tok.type == TokenType.PATTERN, "PAT expected");
        tok = nextToken();
        error(tok.type == TokenType.EQ, "'=' expected");
        String pat = getTextTillNL();
        return pat;
    }

    private void parseChildren(RuleTree parent) {
        Token tok = nextToken();
        error(tok.type == TokenType.CHILDREN, "CHILDREN expected");

        while (true) {
            tok = nextToken();
            if (tok.type != TokenType.NODE) {
                error(tok.type == TokenType.ENDCHILDREN, "Expecting ENDCHILDREN");
                putBack("ENDCHILDREN");
                break;
            }
            putBack("NODE");
            RuleTree child = parseNode();
            parent.addChild(child);
        }

        tok = nextToken();
        error(tok.type == TokenType.ENDCHILDREN, "ENDCHILDREN expected");
    }

    private Cmd parseFollow() {
        Cmd cmd = new Cmd(Cmd.CmdType.FOLLOW);
        Token tok = nextToken();
        CmdArg arg = null;
        if (tok.type == TokenType.VARIABLE) {
            arg = new VarArg(tok.val);
        } else if (tok.type == TokenType.STRING) {
            arg = new StringArg(tok.val);
        } else {
            error(false, "Expecting a $variable or a string");
        }
        cmd.addArg("FOLLOW", arg);
        arg = null;

        tok = nextToken();
        error(tok.type == TokenType.WITH, "Expectig WITH");
        tok = nextToken();
        error(tok.type == TokenType.EQ, "Expectig '='");
        tok = nextToken();
        error(tok.type == TokenType.STRING, "Expectig string value");
        arg = new StringArg(tok.val);
        cmd.addArg("WITH", arg);
        arg = null;

        tok = nextToken();
        error(tok.type == TokenType.POPULATE, "Expectig POPULATE");
        tok = nextToken();
        error(tok.type == TokenType.EQ, "Expectig '='");
        tok = nextToken();
        error(tok.type == TokenType.VARIABLE, "Expectig $variable");
        arg = new VarArg(tok.val);
        cmd.addArg("POPULATE", arg);

        tok = nextToken();
        if (tok.type != TokenType.FILTER) {
            putBack(tok);
        } else {
            tok = nextToken(TokenType.EQ);
            tok = nextToken(TokenType.STRING);
            arg = new StringArg(tok.val);
            cmd.addArg("FILTER", arg);
            tok = nextToken(TokenType.FILTER_ARGS);
            tok = nextToken(TokenType.EQ);
            tok = nextToken(TokenType.STRING);
            arg = new StringArg(tok.val);
            cmd.addArg("FILTER_ARGS", arg);
        }
        return cmd;
    }

    private Cmd parsePrint() {
        Cmd cmd = new Cmd(Cmd.CmdType.PRINT);
        Token tok = nextToken();
        CmdArg arg = null;
        if (tok.type == TokenType.VARIABLE) {
            arg = new VarArg(tok.val);
        } else if (tok.type == TokenType.STRING) {
            arg = new StringArg(tok.val);
        } else {
            error(false, "Expecting a $variable or a string");
        }
        cmd.addArg("PRINT", arg);
        return cmd;
    }

    private Cmd parseProcessValue() {
        Cmd cmd = new Cmd(Cmd.CmdType.PROCESS_VALUE);
        Token tok = nextToken();
        CmdArg arg = null;
        if (tok.type == TokenType.TO_STR || tok.type == TokenType.TO_DOM) {
            arg = new StringArg(getTokenTypeString(tok.type));
        } else {
            error(false, "Expecting a $variable or a string");
        }
        cmd.addArg("TYPE", arg);
        tok = nextToken(TokenType.STRING);
        arg = new StringArg(tok.val);
        cmd.addArg("CLASS", arg);
        return cmd;
    }

    private void parseCmds(RuleTree rule, Action act) {
        while (true) {
            Token tok = nextToken();
            Cmd cmd = null;
            switch (tok.type) {
                case ENDPREACTION: {
                    putBack("ENDPREACTION");
                    return;
                }
                case ENDPOSTACTION: {
                    putBack("ENDPOSTACTION");
                    return;
                }
                case FOLLOW: {
                    cmd = parseFollow();
                    break;
                }
                case PRINT: {
                    cmd = parsePrint();
                    break;
                }
                case PROCESS_VALUE: {
                    cmd = parseProcessValue();
                    break;
                }
                default: {
                    error(false, "Unexpected token. Expecting end of action, follow or print");
                }
            }
            act.addCmd(cmd);
        }
    }

    private void parseAction(RuleTree rule) {
        Token tok = nextToken();
        error((tok.type == TokenType.PREACTION || tok.type == TokenType.POSTACTION),
                "Expecting ACTION");
        boolean preAction = (tok.type == TokenType.PREACTION);
        Action act = new Action(preAction);
        parseCmds(rule, act);
        tok = nextToken();
        error((preAction && tok.type == TokenType.ENDPREACTION) || (!preAction && tok.type == TokenType.ENDPOSTACTION),
                "Expecting END of ACTION");
        if (preAction) {
            rule.setPreAction(act);
        } else {
            rule.setPostAction(act);
        }
    }

    private RuleTree parseNode() {
        Token tok = nextToken();
        if (tok.type != TokenType.NODE) {
            error("Expecting NODE");
        }
        tok = nextToken();
        error(tok.type == TokenType.ID, "Expecting identifier");

        RuleTree node = new RuleTree(tok.val);

        node.setPattern(parsePattern());

        boolean cont = true;
        boolean seenChildre = false;
        boolean seenPreAction = false;
        boolean seenPostAction = false;

        while (cont) {
            tok = nextToken();
            switch (tok.type) {
                case CHILDREN: {
                    error(!seenChildre, "Multiple CHILDREN in a node");
                    putBack("CHILDREN");
                    parseChildren(node);
                    seenChildre = true;
                    break;
                }
                case PREACTION: {
                    error(!seenPreAction, "Multiple PREACTION in a node");
                    putBack("PREACTION");
                    parseAction(node);
                    seenPreAction = true;
                    break;
                }
                case POSTACTION: {
                    error(!seenPostAction, "Multiple POSTACTION in a node");
                    putBack("POSTACTION");
                    parseAction(node);
                    seenPostAction = true;
                    break;
                }
                case ENDNODE: {
                    cont = false;
                }
            }
        }
        return node;
    }

    private void error(String msg) {
        System.out.println("Error: " + msg + ". Line : " + lineNo);
        System.exit(1);
    }

    private void error(boolean cond, String msg) {
        if (!cond) {
            error(msg);
        }
    }
}
