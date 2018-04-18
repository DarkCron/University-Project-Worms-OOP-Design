package worms.parser.expressions;

import worms.programs.SourceLocation;

public abstract class ExpLogical extends BaseExpression {

	public ExpLogical(SourceLocation sourceLoc, ExpAssignment left, ExpAssignment right) {
		super(sourceLoc);		
		this.left = left;
		this.right = right;
		this.expressionValue = evaluate(left,right);
	}

	private final ExpAssignment left;
	private final ExpAssignment right;
	private final Object expressionValue;
	
	public abstract Object evaluate(ExpAssignment left,ExpAssignment right) throws IllegalArgumentException;
	
	public ExpAssignment asExpAssignment() {
		if(left instanceof ExpDoubleValue && right instanceof ExpDoubleValue) {
			return new ExpDoubleValue(this.getSource(),(Double)this.getExpressionLogicValue());
		}
		
		throw new IllegalArgumentException("Assignment expression unknown for argument type: "+left.getValue().getClass());
	}
	
	public Object getExpressionLogicValue(){
		return this.expressionValue;
	}
}
