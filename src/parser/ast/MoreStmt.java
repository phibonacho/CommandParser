package parser.ast;

import visitors.Visitors;

public class MoreStmt extends More<Stmt, StmtSeq> implements StmtSeq {

	public MoreStmt(Stmt first, StmtSeq rest) {
		super(first, rest);
	}

	@Override
	public <T> T accept(Visitors<T> visitor) {
		return visitor.visitMoreStmt(first, rest);
	}
}
