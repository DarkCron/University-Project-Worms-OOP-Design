package worms.parser.statements;

import worms.model.Program;
import worms.parser.expressions.LambdaExpression;
import worms.programs.SourceLocation;

public class StateAction extends BaseStatement {

	public StateAction(SourceLocation sourceLoc, LambdaExpression expression) {
		super(sourceLoc, expression);
	}

	@Override
	public void execute(Program parent, BaseStatement caller) throws IllegalArgumentException, IllegalStateException {
		super.execute(parent, caller);
		this.getExpression().getExpression().getExpressionResult(parent);
	}

	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invokeBreak() {
		// TODO Auto-generated method stub
		
	}
}
