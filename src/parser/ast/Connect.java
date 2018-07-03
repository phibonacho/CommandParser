package parser.ast;

import visitors.Visitors;

import static java.util.Objects.requireNonNull;


public class Connect implements Stmt {
    private final String IpAdress;


    public Connect(String ip){
        IpAdress = requireNonNull(ip);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + IpAdress + ")";
    }

    @Override
    public <T> T accept(Visitors<T> visitor) {
        return visitor.visitConnect(IpAdress);
    }

}
