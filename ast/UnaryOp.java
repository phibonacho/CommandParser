package parser.ast;

import static java.util.Objects.requireNonNull;

public abstract class UnaryOp implements Exp {
    protected final Exp exp;

    protected UnaryOp(Exp e){
        exp = requireNonNull(e);
    }

    @Override
    public String toString(){ return getClass().getSimpleName() + "(" + exp + ")"; }
}
