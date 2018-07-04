package parser.ast;

import visitors.Visitors;

import static java.util.Objects.requireNonNull;


public class Connect implements Stmt {
    private final String IpAdress;
    private final String Username;


    public Connect(String ip, String username){
        IpAdress = requireNonNull(ip);
        Username = requireNonNull(username);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + IpAdress + "," + Username + ")";
    }

    @Override
    public <T> T accept(Visitors<T> visitor) {
        return visitor.visitConnect(IpAdress, Username);
    }

}
