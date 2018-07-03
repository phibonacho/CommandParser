package parser.ast;

import visitors.Visitors;

public class ObjectLiteral extends PrimLiteral<String> {
    public ObjectLiteral(String n) { super(n); }


    @Override
    public <T> T accept(Visitors<T> visitor) {
        return visitor.visitObjectLiteral(value);
    }
}
