package parser.ast;

import visitors.Visitors;

public class SimpleIP implements IP {
    private final String ip;

    public SimpleIP(String ip){
        this.ip = ip;
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public int hashCode() {
        return ip.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + ip + ")";
    }


    @Override
    public <T> T accept(Visitors<T> visitor) {
        return visitor.visitIdent(ip);
    }
}
