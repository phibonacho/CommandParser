package parser.ast;

import visitors.Visitors;

import static java.util.Objects.requireNonNull;

public class StartStmt implements Stmt {
    private final IP Ip;

    public StartStmt(IP ip){
        Ip = requireNonNull(ip);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + Ip + ")";
    }
    @Override
    public <T> T accept(Visitors<T> visitor) {
        return visitor.visitStart(Ip);
    }
}
