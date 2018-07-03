package visitors;

import parser.TokenType;
import parser.ast.*;

public interface Visitors<T> {

    T visitObjectLiteral(String obj);

    T visitAddMessage(Exp m, Exp t);
    T visitAddTopic(Exp t);

    T visitListTopics();
    T visitListObj(TokenType t, Ident o);

    T visitSub();
    T visitConnect();
    T visitDiscconnect();
    T visitIntLiteral();
    T visitIdent();
    T visitIPLiteral();
    T visitSingleExp(); //TODO: collassare su visitExp
    T visitConnect(String ip);
    T visitDisconnect();
    T visitProg(Stmt stmt);
    T visitStmt(Stmt single);
    T visitHelp();
    T visitAddMessage(String message);
}
