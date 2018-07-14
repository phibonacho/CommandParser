package parser.ast;

import visitors.Visitors;

public class SwitchStmt implements Stmt {
    @Override
    public <T> T accept(Visitors<T> visitor) {
        return visitor.visitSwitch();
    }
}
