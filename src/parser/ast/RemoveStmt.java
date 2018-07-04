package parser.ast;

import parser.TokenType;
import visitors.Visitors;

import static java.util.Objects.requireNonNull;

public class RemoveStmt implements Stmt {
    private final TokenType toRemove; // message(Message), user(Ident), topic(Ident);
    private final String Line;
    private final Ident Topic;

    public RemoveStmt(TokenType e, String line, Ident Id){
        toRemove = requireNonNull(e);
        Line = line;
        Topic = Id;
    }

    @Override
    public <T> T accept(Visitors<T> visitor) {
        return visitor.visitRemove(toRemove, Line, Topic);
    }
}
