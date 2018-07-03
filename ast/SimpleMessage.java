package parser.ast;

import visitors.Visitors;

import static java.util.Objects.requireNonNull;

public class SimpleMessage implements Message {
	private final String message;

	public SimpleMessage(String name) {
		this.message = requireNonNull(name);
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Ident))
			return false;
		return message.equals(((Ident) obj).getName());
	}

	@Override
	public int hashCode() {
		return message.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + message + ")";
	}

	@Override
	public <T> T accept(Visitors<T> visitor) {
		return visitor.visitAddMessage(message); // left for uniformity, instead of return visitor.visitIdent(this);
	}
}
