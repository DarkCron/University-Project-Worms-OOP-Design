package worms.parser.statements;

import be.kuleuven.cs.som.annotate.*;
import worms.model.Program;
import worms.parser.expressions.LambdaExpression;
import worms.programs.SourceLocation;

public class StateIf extends BaseStatement {

	public StateIf(SourceLocation sourceLoc, LambdaExpression expression, BaseStatement ifBody, BaseStatement elseBody) {
		super(sourceLoc, expression);
		this.ifBody = ifBody;
		this.elseBody = elseBody;
	}
	
	private final BaseStatement ifBody;
	private final BaseStatement elseBody;
	
	@Basic @Raw @Immutable
	public BaseStatement getIfBody() {
		return this.ifBody;
	}
	
	@Basic @Raw @Immutable
	public BaseStatement getElseBody() {
		return this.elseBody;
	}

	@Override
	public void execute(Program parent, BaseStatement caller) throws IllegalArgumentException, IllegalStateException {
		super.execute(parent, caller);
		
		if(parent==null||this.getExpression() == null) {
			throw new IllegalArgumentException();
		}
		
		Object condition = this.getExpression().getExpression().getExpressionResult(parent);
		if(condition instanceof Boolean) {
			if((boolean) condition) {
				this.getIfBody().execute(parent, this);
			}else {
				if(this.getElseBody() != null) {
					this.getElseBody().execute(parent, this);
				}
			}
		}
	}

}
