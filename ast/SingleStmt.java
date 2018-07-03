package parser.ast;

import visitors.Visitors;

public class SingleStmt extends Single<Stmt> implements StmtSeq {

	public SingleStmt(Stmt single) {
		super(single);
	}

	@Override
	public <T> T accept(Visitors<T> visitor) {
		return visitor.visitStmt(single);
	}
}
