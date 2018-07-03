package parser.ast;

import visitors.Visitors;

public class ListStmt implements Stmt {
    protected final Ident Topic;
    protected final ObjectLiteral obj;

    public ListStmt(Ident t, ObjectLiteral o){
        Topic = t;
        obj = o;
    }
    
    @Override
    public <T> T accept(Visitors<T> visitor) {
        return Topic == null? visitor.visitListTopics() : visitor.visitListObj(Topic, obj);
    }
}
