package parser.ast;

import visitors.Visitors;

import static java.util.Objects.requireNonNull;

public class AddStmt implements Stmt{
    private final Message messageExp;
    private final Ident TopicExp;

    public AddStmt(Message mExp, Ident tExp){ //TODO: i messaggi non sono di tipo ident, sono stringhe;
        messageExp = mExp;
        TopicExp = requireNonNull(tExp);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + messageExp + "," + TopicExp + ")";
    }

    @Override
    public <T> T accept(Visitors<T> visitor) {
        return visitor.visitAdd(messageExp, TopicExp);
    }
}
