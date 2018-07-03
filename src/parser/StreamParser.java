package parser;

import parser.ast.*;

import static parser.TokenType.*;

public class StreamParser implements Parser {
    private final Tokenizer tokenizer;

    /**
     * Constructor
     * @param tokenizer
     */
    public StreamParser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    /**
     * Aux Functions
     * @throws ParserException
     */
    private void tryNext() throws ParserException {
        try {
            tokenizer.next();
        } catch (TokenizerException e) {
            throw new ParserException(e);
        }
    }

    private void match(TokenType expected) throws ParserException {
        final TokenType found = tokenizer.tokenType();
        if (found != expected)
            throw new ParserException(
                    "Expecting " + expected + ", found " + found + "('" + tokenizer.tokenString() + "')");
    }

    private void consume(TokenType expected) throws ParserException {
        match(expected);
        tryNext();
    }

    private void unexpectedTokenError() throws ParserException {
        throw new ParserException("Unexpected token " + tokenizer.tokenType() + "('" + tokenizer.tokenString() + "')");
    }

    @Override
    public Prog parseProg() throws ParserException {
        tryNext(); // one look-ahead symbol
        Prog prog = new ProgClass(parseStmt());
        match(EOF);
        return prog;
    }

    private Stmt parseStmt() throws ParserException {
        switch(tokenizer.tokenType()){
            default:
                unexpectedTokenError();
            case ADD:
                return parseAddStmt();
            case LIST:
                return parseListStmt();
            case CONNECT:
                return parseConnectStmt();
            case DISCONNECT:
                return parseDisconnectStmt();
        }
    }

    // list user: restituisce gli utenti connessi su lato server
    // list user on <Ident>: restituisce gli utenti registrati al topic
    // list message on <Ident>: restituisce i messaggi postati su un topic
    // list topic: restituisce i topic presenti su un server
    private ListStmt parseListStmt() throws ParserException {
        consume(LIST);
        TokenType found = tokenizer.tokenType();
        consume(OBJ);
        return new ListStmt(found, parseOn());
    }

    AddStmt parseAddStmt() throws ParserException{
        consume(ADD);
        Message msg = null;
        TokenType found = tokenizer.tokenType();
        consume(OBJ);
        switch (found){
            default:
                unexpectedTokenError();
            case MESSAGE:
                msg = parseMessage();
                consume(ON);
            case TOPIC:
                Ident t = parseIdent();
                return new AddStmt(msg, t);
        }
    }

    private Ident parseOn() throws ParserException {
        if (tokenizer.tokenType()!= ON) return null;
        consume(ON);
        return parseIdent();
    }

    private Connect parseConnectStmt() throws ParserException {
        consume(CONNECT);
        String ip = tokenizer.IPValue();
        return new Connect(ip);
    }

    private Disconnect parseDisconnectStmt() throws ParserException {
        consume(DISCONNECT);
        return new Disconnect();
    }

    private Message parseMessage() throws ParserException {
        String message = tokenizer.MessageValue();
        consume(MESSAGE);
        //TODO: capire come spostarsi oltre la stringa che contiene il messaggio....
        return new SimpleMessage(message);
    }

    private Ident parseIdent() throws ParserException{
        return new SimpleIdent(tokenizer.tokenString());
    }
}
