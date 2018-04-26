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
	
	private BaseStatement ifBody;
	private BaseStatement elseBody;
	
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
		if(interruptedDoingElse) {
			this.getElseBody().execute(parent, this);
			interruptedDoingElse = false;
		}else if(interruptedDoingIf) {
			this.getIfBody().execute(parent, this);
			interruptedDoingIf = false;
		}else {
			if(condition instanceof Boolean) {
				if((boolean) condition) {
					doingIf = true;
					this.getIfBody().execute(parent, this);
				}else {
					if(this.getElseBody() != null) {
						doingElse = true;
						this.getElseBody().execute(parent, this);
					}
				}
			}else {
				throw new IllegalStateException();
			}
		}
		

		
		doingIf = false;
		doingElse = false;
	}

	@Override
	public void interrupt() {
		if(this.ifBody != null) {
			this.ifBody.interrupt();
			
			if(doingIf) {
				interruptedDoingIf = true;
			}
		}
		if(this.elseBody != null) {
			this.elseBody.interrupt();
			
			if(doingElse) {
				interruptedDoingElse = true;
			}
		}
	}
	
	@Override
	public void invokeBreak() {
		this.interrupt();
	}

	
	private boolean doingIf = false;
	
	private boolean doingElse = false;
	
	private boolean interruptedDoingIf = false;

	private boolean interruptedDoingElse = false;

	@Override
	public StateIf clone() throws CloneNotSupportedException {
		StateIf clone = (StateIf)super.clone();
		clone.ifBody = ifBody.clone();
		if(elseBody!=null) {
			clone.elseBody = elseBody.clone();
		}
		
		return clone;
	}
}
