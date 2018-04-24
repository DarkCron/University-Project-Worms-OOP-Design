package worms.parser.statements;

import worms.model.Program;
import worms.parser.expressions.LambdaExpression;
import worms.programs.SourceLocation;

public class StateProcedure extends BaseStatement {

	public StateProcedure(SourceLocation sourceLoc, LambdaExpression expression) {
		super(sourceLoc, expression);
	}

	@Override
	public void execute(Program parent, BaseStatement caller) throws IllegalArgumentException, IllegalStateException {
		super.execute(parent, caller);
		Object o = this.getExpression().getExpression().getExpressionResult(parent);
		if(o instanceof BaseStatement) {
			((BaseStatement) o).execute(parent, caller);
		}
	}
}
