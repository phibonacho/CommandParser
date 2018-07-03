package parser.ast;

import visitors.Visitors;

import static java.util.Objects.requireNonNull;

public class AddStmt implements Stmt{
    private final Message messageExp;
    private final Ident TopicExp;

    public AddStmt(Message mExp, Ident tExp){ //TODO: i messaggi non sono di tipo ident, sono stringhe;
        messageExp = mExp;
        TopicExp = requireNonNull(tExp);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + (messageExp == null?  "(" + TopicExp + ")" : "(" + messageExp + "," + TopicExp + ")");
    }

    @Override
    public <T> T accept(Visitors<T> visitor) {
        return messageExp == null ? visitor.visitAddTopic(TopicExp) : visitor.visitAddMessage(messageExp, TopicExp);
    }
}
