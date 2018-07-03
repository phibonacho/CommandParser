package parser;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import static parser.TokenType.*;

public class StreamTokenizer implements Tokenizer {
    private static final String regEx;
    private static final Map<String, TokenType> keywords = new HashMap<>();
    private static final Map<String, TokenType> symbols = new HashMap<>();

    private boolean hasNext = true; // any stream contains at least the EOF
    // token
    private TokenType tokenType;
    private String tokenString;
    private int intValue;
    private String IPValue;
    private String MessageValue;
    private final Scanner scanner;

    static {
        // remark: groups must correspond to the ordinal of the corresponding
        // token type
        final String stringRegEx = "(\"[^\"\\n]*\")";
        final String identRegEx = "([a-zA-Z][a-zA-Z0-9,\\./]*)"; // group 1
        final String numRegEx = "(0[bB][01]+|[1-9][0-9]*|0)"; // group 2
        final String skipRegEx = "(\\s+|\\.*)"; // group 3
        final String IPRegex = "(([0-9]+.){3}[0-9])"; // group 4
        final String symbolRegEx = "\"|\\+|\\*|!|==|=|&&|\\(|\\)|;|,|\\{|\\}|-|::|:|\\[|\\]";
        regEx = stringRegEx + "|" + identRegEx + "|" + numRegEx  + "|" + skipRegEx + "|" + IPRegex + "|" + symbolRegEx;
    }

    static {
        keywords.put("add", ADD);
        keywords.put("list", LIST);
        keywords.put("message", MESSAGE);
        keywords.put("topic", TOPIC);
        keywords.put("user", USER);
        keywords.put("on", ON);
        keywords.put("connect", CONNECT);
        keywords.put("disconnect", DISCONNECT);
        keywords.put("help", HELP);
    }

    static {
        symbols.put("\"", APEX);
    }

    /**
     * Constructor
     * */
    public StreamTokenizer(Reader reader) {
        scanner = new StreamScanner(regEx, reader);
    }


    /**
     * CheckTyper
     * @return
     * @throws TokenizerException
     */
    private void checkType() {
        tokenString = scanner.group();

        if(scanner.group(MESSAGELIT.ordinal()) != null){
            System.err.println("Found a string...");
            tokenType = MESSAGELIT;
            MessageValue = tokenString.substring(1, tokenString.length()-1);
            return;
        }

        if (scanner.group(IDENT.ordinal()) != null) { // IDENT or a keyword
            tokenType = keywords.get(tokenString);
            if (tokenType == null)
                tokenType = IDENT;
            if(tokenType == IP)
                IPValue = tokenString;
            return;
        }
        if (scanner.group(NUM.ordinal()) != null) { // NUM
            tokenType = NUM;

            intValue = Integer.parseInt(tokenString);
            return;
        }

        if (scanner.group(SKIP.ordinal()) != null) { // SKIP
            tokenType = SKIP;
            return;
        }

        throw new AssertionError("Fatal error");
    }


    @Override
    public TokenType next() throws TokenizerException {
        do {
            tokenType = null;
            tokenString = "";
            try {
                if (hasNext && !scanner.hasNext()) {
                    hasNext = false;
                    return tokenType = EOF;
                }
                scanner.next();
            } catch (ScannerException e) {
                throw new TokenizerException(e);
            }
            checkType();
        } while (tokenType == SKIP);
        return tokenType;
    }

    private void checkValidToken() {
        if (tokenType == null)
            throw new IllegalStateException();
    }

    private void checkValidToken(TokenType ttype) {
        if (tokenType != ttype)
            throw new IllegalStateException();
    }

    @Override
    public String tokenString() {
        checkValidToken();
        return tokenString;
    }

    @Override
    public int intValue() {
        checkValidToken(NUM);
        return intValue;
    }

    @Override
    public TokenType tokenType() {
        checkValidToken();
        return tokenType;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public void close() throws TokenizerException {
        try {
            scanner.close();
        } catch (ScannerException e) {
            throw new TokenizerException(e);
        }
    }

    @Override
    public String IPValue() {
        checkValidToken(IP);
        return IPValue;
    }

    @Override
    public String MessageValue(){
        checkType();
        return MessageValue; // rimuove le virgolette...
    }

}