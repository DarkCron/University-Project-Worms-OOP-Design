package worms.parser.expressions;

import worms.programs.SourceLocation;

public class ExpAddition extends ExpLogical {

	public ExpAddition(SourceLocation sourceLoc, ExpAssignment left, ExpAssignment right) {
		super(sourceLoc,left,right);
	}

	@Override
	public Object evaluate(ExpAssignment left, ExpAssignment right) throws IllegalArgumentException {
		if(left.getValue() == null || right.getValue() == null) {
			throw new IllegalArgumentException("Expression arguments are invalid for logic evalution");
		}
		if(left instanceof ExpDoubleValue && right instanceof ExpDoubleValue) {
			return simpleAddition((Double)left.getValue(),(Double)right.getValue());
		}
		
		throw new IllegalArgumentException("Logical expression unknown for argument type: "+left.getValue().getClass());
	}

	private Object simpleAddition(Double value, Double value2) {
		return value + value2;
	}


}
