package worms.parser.statements;

import worms.parser.KeyValuePair;
import worms.parser.expressions.ExpAssignment;
import worms.programs.SourceLocation;

public class StateAssignment extends BaseStatement {

	public StateAssignment(SourceLocation sourceLoc, String variableName, ExpAssignment assignmentExpression) {
		super(sourceLoc);
		
		this.variableName = variableName;
		this.assignmentExp = assignmentExpression;
		this.keyValue = new KeyValuePair<String, Object>(variableName, assignmentExpression.getValue());
	}

	private final String variableName;
	private final ExpAssignment assignmentExp;
	
	private final KeyValuePair<String, Object> keyValue;
	
	public KeyValuePair<String, Object> getKeyValue(){
		return keyValue;
	}
}
