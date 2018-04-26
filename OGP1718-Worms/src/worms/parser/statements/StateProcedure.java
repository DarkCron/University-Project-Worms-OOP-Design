package worms.parser.statements;

import worms.model.Program;
import worms.parser.expressions.LambdaExpression;
import worms.parser.procedures.BaseProcedure;
import worms.programs.SourceLocation;

public class StateProcedure extends BaseStatement {

	public StateProcedure(SourceLocation sourceLoc,String procName, LambdaExpression expression) {
		super(sourceLoc, expression);
		this.procName = procName;
	}

	@Override
	public void execute(Program parent, BaseStatement caller) throws IllegalArgumentException, IllegalStateException {
		super.execute(parent, caller);
		//Object o = this.getExpression().getExpression().getExpressionResult(parent);
		BaseProcedure clone = null;
		try {
			clone = (BaseProcedure)(parent.getProcedures().get(procName).clone());
		} catch (Exception e) {
			throw new IllegalStateException();
		}
		parent.setLastCalledProc(clone);
		clone.getBody().execute(parent, caller);
//		if(o instanceof BaseStatement) {
//			((BaseStatement) o).execute(parent, caller);
//		}
		parent.popLastCalledProc();
	}
	
	private final String procName;
	
	public String getProcName() {
		return procName;
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
