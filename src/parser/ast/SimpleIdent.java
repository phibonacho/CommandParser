package parser.ast;

import visitors.Visitors;

public class SimpleIdent implements Ident {
    private final String id;

    public SimpleIdent(String id){
        this.id = id;
    }

    @Override
    public String getName() {
        return id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + id + ")";
    }


    @Override
    public <T> T accept(Visitors<T> visitor) {
        return visitor.visitIdent(id);
    }
}
