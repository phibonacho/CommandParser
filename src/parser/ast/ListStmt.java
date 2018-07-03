package parser.ast;

import parser.TokenType;
import visitors.Visitors;

public class ListStmt implements Stmt {
    protected final Ident Topic;
    protected final TokenType Type;

    public ListStmt(TokenType type, Ident topic){
        Topic = topic;
        Type = type;
    }
    
    @Override
    public <T> T accept(Visitors<T> visitor) {
        return Topic == null? visitor.visitListTopics() : visitor.visitListObj(Type, Topic);
    }
}
