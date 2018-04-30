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
			if(executing==null) {
				clone = (BaseProcedure)(parent.getProcedures().get(procName).clone());	
				executing = clone;//TODO
			}else {
				clone = executing;
			} 
		} catch (Exception e) {
			throw new IllegalStateException();
		}
		parent.setLastCalledProc(clone);
		clone.getBody().execute(parent, caller);
//		if(o instanceof BaseStatement) {
//			((BaseStatement) o).execute(parent, caller);
//		}
		if(!interrupted) {
			executing = null;	
		}
		parent.popLastCalledProc();
		interrupted = false;
	}
	
	private boolean interrupted = false;
	private BaseProcedure executing = null;
	private final String procName;
	
	public String getProcName() {
		return procName;
	}
	
	@Override
	public void interrupt() {
		interrupted = true;
		if(executing!=null && executing.getBody()!=null) {
			executing.getBody().interrupt();
		}
	}

	@Override
	public void invokeBreak() {
		// TODO Auto-generated method stub
		
	}


}
