package visitors;

import parser.ast.*;

public interface Visitors<T> {

    T visitObjectLiteral(String obj);

    T visitAddMessage(Exp m, Exp t);
    T visitAddTopic(Exp t);

    T visitListTopics();
    T visitListObj(Ident t, ObjectLiteral o);

    T visitSub();
    T visitConnect();
    T visitDiscconnect();
    T visitIntLiteral();
    T visitIdent();
    T visitIPLiteral();
    T visitSingleExp(); //TODO: collassare su visitExp

    T visitProg(Stmt stmt);
    T visitStmt(Stmt single);

    T visitAddMessage(String message);
}
