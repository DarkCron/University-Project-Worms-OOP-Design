package worms.parser.statements;

import java.util.Map;

import worms.model.Program;
import worms.parser.KeyValuePair;
import worms.parser.expressions.LambdaExpAssignment;
import worms.parser.expressions.LambdaExpression;
import worms.programs.SourceLocation;

public class StateAssignment extends BaseStatement {

	public StateAssignment(SourceLocation sourceLoc, LambdaExpAssignment expression) {
		super(sourceLoc,expression);
	}

	@Override
	public void execute(Program parent) throws IllegalArgumentException, IllegalStateException {
		this.getExpression().getExpression().getExpressionResult(parent);
	}
	

	public void varSetter(Map<String,LambdaExpression> vars,Program parent) throws IllegalStateException{
		if(this.getExpression().getExpression().getExpressionResult(parent) instanceof KeyValuePair<?, ?>) {
			Object key = ((KeyValuePair<?, ?>)this.getExpression().getExpression().getExpressionResult(parent)).getKey();
			Object value = ((KeyValuePair<?, ?>)this.getExpression().getExpression().getExpressionResult(parent)).getValue();
			if(key instanceof String && value instanceof LambdaExpression) {
				vars.put((String)key,(LambdaExpression)value);
			}else {
				throw new IllegalStateException("Lambda assignment was no assignment");
			}
		}else {
			throw new IllegalStateException("Lambda assignment was no assignment");
		}	
	}
	
}
