package parser.ast;

import visitors.Visitors;

public interface AST {
    <T> T accept(Visitors<T> visitor);
}
