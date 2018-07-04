package parser.ast;

import visitors.Visitors;

import static java.util.Objects.requireNonNull;

public class ProgClass implements Prog {
	private final StmtSeq stmt;

	public ProgClass(StmtSeq stmt) {
		this.stmt = requireNonNull(stmt);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + stmt + ")";
	}

	@Override
	public <T> T accept(Visitors<T> visitor) {
		return visitor.visitProg(stmt);
	}
}
