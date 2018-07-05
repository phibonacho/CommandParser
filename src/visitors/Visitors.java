package visitors;

import parser.TokenType;
import parser.ast.*;

public interface Visitors<T> {


    T visitAdd(String m, String t);
    T visitRemove(TokenType e, String l,  Ident t);

    T visitList(TokenType t, Ident o);

    T visitUnsubscribe(Ident Topic);

    T visitIdent(String id);

    T visitConnect(String ip, String username);
    T visitDisconnect();

    T visitProg(StmtSeq stmt);
    T visitStmt(Stmt single);
    T visitAddMessage(String message);
    T visitSubscribe(Ident Topic);

    T visitHelp();

    T visitMoreStmt(Stmt first, StmtSeq rest);

    T visitExit();

    T visitStart(String ip);
}
