package parser.ast;

import visitors.Visitors;

public class Disconnect implements Stmt{



    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + ")";
    }

    @Override
    public <T> T accept(Visitors<T> visitor) {
        return visitor.visitDisconnect();
    }
}
