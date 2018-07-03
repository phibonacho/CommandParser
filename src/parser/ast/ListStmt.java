package parser.ast;

import parser.TokenType;
import visitors.Visitors;

import static java.util.Objects.requireNonNull;

public class ListStmt implements Stmt {
    protected final Ident Topic;
    protected final TokenType Type;

    public ListStmt(TokenType type, Ident topic){
        Topic = topic;
        Type = requireNonNull(type);
    }
    
    @Override
    public <T> T accept(Visitors<T> visitor) {
        return visitor.visitList(Type, Topic);
    }
}
