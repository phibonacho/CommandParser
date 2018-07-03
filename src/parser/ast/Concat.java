package parser.ast;

import visitors.Visitors;

public class Concat extends BinaryOp {
    public Concat(Exp left, Exp right){ super(left, right);}

    @Override
    public <T> T accept(Visitors<T> visitor) {
        return visitor.visitConcat(left, right);
    }
}
