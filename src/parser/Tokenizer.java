package parser;

public interface Tokenizer extends AutoCloseable{
    TokenType next() throws TokenizerException;

    String tokenString();

    TokenType tokenType();

    boolean hasNext();

    public void close() throws TokenizerException;

    String IPValue();

    String MessageValue();
}
