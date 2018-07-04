package parser.ast;

import visitors.Visitors;

public class ExitStmt implements Stmt {
    @Override
    public <T> T accept(Visitors<T> visitor) {
        return visitor.visitExit();
    }
}
