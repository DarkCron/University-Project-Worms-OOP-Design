package worms.parser.statements;

import be.kuleuven.cs.som.annotate.*;
import worms.model.Program;
import worms.parser.expressions.LambdaExpression;
import worms.programs.SourceLocation;

public abstract class BaseStatement {

	public BaseStatement(SourceLocation sourceLoc, LambdaExpression expression) {
		this.sourceLoc = sourceLoc;
		this.expression = expression;
	}

	private final LambdaExpression expression;
	private final SourceLocation sourceLoc;
	
	@Basic @Raw @Immutable
	public final SourceLocation getSource() {
		return sourceLoc;
	}
	
	@Basic @Raw @Immutable
	public final LambdaExpression getExpression() {
		return expression;
	}
	
	public abstract void execute(Program parent) throws IllegalArgumentException,IllegalStateException;
}
