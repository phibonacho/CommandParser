package visitors;

import parser.TokenType;
import parser.ast.*;

public interface Visitors<T> {

    T visitObjectLiteral(String obj);

    T visitAdd(Exp m, Exp t);

    T visitList(TokenType t, Ident o);

    T visitUnsubscribe(Ident Topic);

    T visitIdent();
    T visitIPLiteral();
    T visitSingleExp(); //TODO: collassare su visitExp

    T visitConnect(String ip);
    T visitDisconnect();

    T visitProg(Stmt stmt);
    T visitStmt(Stmt single);
    T visitAddMessage(String message);
    T visitSubscribe(Ident Topic);

    T visitHelp();
}
