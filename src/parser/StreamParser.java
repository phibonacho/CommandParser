package parser;

import parser.ast.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

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
        consume(found);
        return new ListStmt(found, parseOn());
    }

    private AddStmt parseAddStmt() throws ParserException{
        Message msg = null;
        consume(ADD);
        switch (tokenizer.tokenType()){
            default:
                unexpectedTokenError();
            case MESSAGE:
                tryNext();
                msg = parseMessage();
                System.err.println(msg.toString());
                consume(ON);
            case TOPIC:
                return new AddStmt(msg, parseIdent());
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
        consume(MESSAGELIT);
        return new SimpleMessage(message);
    }

    private Ident parseIdent() throws ParserException{
        String name = tokenizer.tokenString();
        consume(IDENT);
        return new SimpleIdent(name);
    }

    public static void main(String args[]){
        try (Tokenizer tokenizer = new StreamTokenizer(
                args.length > 0 ? new FileReader(args[0]) : new InputStreamReader(System.in))) {
            Parser parser = new StreamParser(tokenizer);
            System.out.println("sono qui");
            Prog prog = parser.parseProg();
        }
        catch(ParserException pe){
            System.err.println("Syntax error: "+ pe.getMessage());
        } catch (TokenizerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
