package parser.ast;

import visitors.Visitors;

import static java.util.Objects.requireNonNull;

public class Subscribe implements Stmt { // todo: valutare se rendere tutti statement o tutti operatori...
    protected final Ident Topic;

    public Subscribe(Ident topic){
        Topic = requireNonNull(topic);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + Topic + ")";
    }

    @Override
    public <T> T accept(Visitors<T> visitor) {
        return visitor.visitSubscribe(Topic);
    }
}
