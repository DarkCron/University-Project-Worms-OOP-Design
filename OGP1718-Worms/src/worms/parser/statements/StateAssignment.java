package worms.parser.statements;

import worms.model.Program;
import worms.parser.expressions.LambdaExpression;
import worms.programs.SourceLocation;

public class StateAssignment extends BaseStatement {

	public StateAssignment(SourceLocation sourceLoc, LambdaExpression expression) {
		super(sourceLoc,expression);
	}

	@Override
	public void execute(Program parent) throws IllegalArgumentException, IllegalStateException {
		this.getExpression().getExpression().getExpressionResult(parent);
	}
}
