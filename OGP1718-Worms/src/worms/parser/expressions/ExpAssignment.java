package worms.parser.expressions;

import be.kuleuven.cs.som.annotate.*;
import worms.programs.SourceLocation;

@Value
public abstract class ExpAssignment extends BaseExpression {

	public ExpAssignment(SourceLocation sourceLoc, Object value) {
		super(sourceLoc);
		this.value = value;
	}

	private final Object value;

	@Raw @Basic @Immutable
	public Object getValue() {
		return this.value;
	}
}
