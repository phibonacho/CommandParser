package parser;

import parser.ast.*;
import visitors.evaluation.Eval;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

import static parser.TokenType.*;

public class StreamParser implements Parser {

    private final Tokenizer tokenizer;
    private Eval eval = new Eval();

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
    public void tryNext() throws ParserException {
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
        Prog prog = new ProgClass(parseStmtSeq());
        // match(EXIT);
        return prog;
    }

    private StmtSeq parseStmtSeq() throws ParserException {
        Stmt stmt = parseStmt();
        if ( !(stmt instanceof ExitStmt) && tokenizer.tokenType() == NEWLINE) {
            tryNext();
            return new MoreStmt(stmt, parseStmtSeq());
        }
        return new SingleStmt(stmt);
    }

    public void plays() throws ParserException{
        Stmt stmt;
        do{

            stmt = parseStmt();
            stmt.accept(eval);
        } while(tokenizer.tokenType() != EXIT);
    }

    public Stmt parseStmt() throws ParserException {
        tryNext();
        switch(tokenizer.tokenType()){
            default:
                unexpectedTokenError();
            case SWITCH:
                return parseSwitch();
            case NEWLINE:
                return parseStmt();
            case ADD:
                return parseAddStmt();
            case REMOVE:
                return parseRemoveStmt();
            case LIST:
                return parseListStmt();
            case CONNECT:
                return parseConnectStmt();
            case DISCONNECT:
                return parseDisconnectStmt();
            case SUBSCRIBE:
                return parseSubscribe();
            case UNSUBSCRIBE:
                return parseUnsubscribe();
            case START:
                return parseStartStmt();
            case HELP:
                return parseHelpStmt();
            case EXIT:
                return parseExitStmt();
        }
    }

    private Stmt parseSwitch() throws ParserException {
        consume(SWITCH);
        return new SwitchStmt();
    }

    private StartStmt parseStartStmt() throws ParserException {
        consume(START);
        IP ip = parseIP(); // creare tipo IP... altrimenti restituisce invalid state exception....
        return new StartStmt(ip);
    }

    private Stmt parseRemoveStmt() throws ParserException {
        consume(REMOVE);
        String toRemove = null;
        TokenType found = tokenizer.tokenType();
        tryNext();
        switch (found){
            default:
                unexpectedTokenError();
            case USER:
                toRemove = parseIdent().getName();
            case MESSAGE:
                if(toRemove == null) toRemove = parseMessage().getMessage();
            case TOPIC:
                Ident Topic = parseIn();
                return new RemoveStmt(found, toRemove, Topic);
        }
    }

    private Stmt parseExitStmt() throws ParserException {
        consume(EXIT);
        return new ExitStmt();
    }

    // list user: restituisce gli utenti connessi su lato server
    // list user on <Ident>: restituisce gli utenti registrati al topic
    // list message on <Ident>: restituisce i messaggi postati su un topic
    // list topic: restituisce i topic presenti su un server
    private ListStmt parseListStmt() throws ParserException {
        consume(LIST);
        TokenType found = tokenizer.tokenType();
        tryNext();
        switch (found){
            default:
                return new ListStmt(found, parseIn());
            case TOPIC:
                return new ListStmt(found, null);
        }
    }

    private AddStmt parseAddStmt() throws ParserException{
        Message msg = null;
        consume(ADD);
        TokenType found = tokenizer.tokenType();
        tryNext();
        switch (found){
            default:
                unexpectedTokenError();
            case MESSAGE:
                msg = parseMessage();
                consume(IN);
            case TOPIC:
                return new AddStmt(msg, parseIdent());
        }
    }

    private Subscribe parseSubscribe() throws ParserException {
        consume(SUBSCRIBE);
        return new Subscribe(parseIdent());
    }

    private Unsubscribe parseUnsubscribe() throws ParserException {
        consume(UNSUBSCRIBE);
        return new Unsubscribe(parseIdent());
    }

    private Connect parseConnectStmt() throws ParserException {
        consume(CONNECT);
        String ip = tokenizer.IPValue();
        consume(IP);
        consume(AS);
        Ident username = parseIdent();
        return new Connect(ip, username.getName());
    }

    private Disconnect parseDisconnectStmt() throws ParserException {
        consume(DISCONNECT);
        return new Disconnect();
    }

    private Help parseHelpStmt() throws ParserException {
        consume(HELP);
        return  new Help();
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

    private parser.ast.IP parseIP() throws ParserException {
        String ip = tokenizer.tokenString();
        consume(IP);
        return new SimpleIP(ip);
    }

    private Ident parseIn() throws ParserException { // opzionale, gli statement che non specificano il topic, sono quelli che operano su server...
        if(tokenizer.tokenType()!=IN) return null;
        consume(IN);
        return parseIdent();
    }

    public static void main(String args[]){
        try (Tokenizer tokenizer = new StreamTokenizer(
                args.length > 0 ? new FileReader(args[0]) : new InputStreamReader(System.in))) {
            Parser parser = new StreamParser(tokenizer);
            ((StreamParser) parser).plays();
            Prog prog = parser.parseProg();
            prog.accept(new Eval());
        }
        catch(ParserException pe){
            System.err.println("Syntax error: "+ pe.getMessage());
        } catch (TokenizerException e) {
            System.err.println("unexpected token: "+e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
