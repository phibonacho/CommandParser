package parser.ast;

import static java.util.Objects.requireNonNull;

public abstract class BinaryOp implements Exp{
    protected final Exp left;
    protected final Exp right;

    protected BinaryOp(Exp l, Exp r){
        left = requireNonNull(l);
        right = requireNonNull(r);
    }

    @Override
    public String toString(){
        return getClass().getSimpleName() +"(" + left + "," + right +")";
    }

}
