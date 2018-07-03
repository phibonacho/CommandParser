package parser;

import parser.ast.*;
import sun.nio.cs.US_ASCII;

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
        }
    }

    private ListStmt parseListStmt() throws ParserException {
        consume(LIST);
        if(tokenizer.tokenType() == MESSAGE){
            ObjectLiteral obj = new ObjectLiteral(tokenizer.tokenString());
            consume(MESSAGE);
            return new ListStmt(parseIdent(), obj); //TODO: capire come fare
        }
        if(tokenizer.tokenType() == USER){
            ObjectLiteral obj = new ObjectLiteral(tokenizer.tokenString());
            return new ListStmt(parseIdent(), obj);
        }
        return null;
    }

    AddStmt parseAddStmt() throws ParserException{
        consume(ADD);
        switch (tokenizer.tokenType()){
            default:
                unexpectedTokenError();
            case MESSAGE:
                Message msg = parseMessage();
                consume(ON);
                Ident topic = parseIdent();
                return new AddStmt(msg, topic);
            case TOPIC:
                Ident t = parseIdent();
                return new AddStmt(null, t);
        }
    }

    private Exp parseAtom() throws ParserException{
        switch (tokenizer.tokenType()){
            default:
                unexpectedTokenError();
            case MESSAGE:
                return parseMessage();

        }
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
