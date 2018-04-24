package worms.parser.statements;

import be.kuleuven.cs.som.annotate.*;
import worms.model.Program;
import worms.parser.expressions.LambdaExpression;
import worms.programs.SourceLocation;

public class StateWhile extends BaseStatement {

	public StateWhile(SourceLocation sourceLoc, LambdaExpression expression, BaseStatement body) {
		super(sourceLoc, expression);
		this.body = body;
	}
	
	private final BaseStatement body;
	
	@Raw @Immutable @Basic
	public BaseStatement getWhileBody() {
		return body;
	}

	@Override
	public void execute(Program parent, BaseStatement caller) throws IllegalArgumentException, IllegalStateException {
		super.execute(parent, caller);
		
		if(parent==null||this.getExpression() == null) {
			throw new IllegalArgumentException();
		}
		
		Object condition = this.getExpression().getExpression().getExpressionResult(parent);
		if(condition instanceof Boolean) {
			while ((boolean) condition && !this.isInterrupted()) {
				this.getWhileBody().execute(parent, this);
				condition = this.getExpression().getExpression().getExpressionResult(parent);
			}
		}
		
		this.clearInterrupt();
	}

	private boolean isInterrupted = false;
	
	private boolean isInterrupted() {
		return this.isInterrupted;
	}
	
	public void interrupt() {
		isInterrupted = true;
	}
	
	private void clearInterrupt() {
		isInterrupted = false;
	}
}
