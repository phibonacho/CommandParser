package parser.ast;

import visitors.Visitors;

import static java.util.Objects.requireNonNull;

public class Unsubscribe implements Stmt {
    private final Ident Topic;

    public Unsubscribe(Ident topic){
        Topic = requireNonNull(topic);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + Topic + ")";
    }

    @Override
    public <T> T accept(Visitors<T> visitor) {
        return visitor.visitUnsubscribe(Topic);
    }
}
