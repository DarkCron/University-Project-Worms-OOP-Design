package worms.parser.expressions;

import worms.programs.SourceLocation;

public class ExpReadVar extends BaseExpression {

	public ExpReadVar(SourceLocation sourceLoc, String variableName) {
		super(sourceLoc);
		this.variableName = variableName;
	}

	private final String variableName;
	
	public String getVariableName() {
		return variableName;
	}
}
