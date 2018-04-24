package worms.parser.procedures;

import worms.parser.statements.BaseStatement;
import worms.programs.SourceLocation;

public class BaseProcedure {
	public BaseProcedure(SourceLocation sourceLocation,String procedureName, BaseStatement body) {
		this.sourceLocation = sourceLocation;
		this.procedureName = procedureName;
		this.body = body;
	}
	
	private final SourceLocation sourceLocation;
	private final String procedureName;
	private final BaseStatement body;
	
	public SourceLocation getSourceLocation() {
		return sourceLocation;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public BaseStatement getBody() {
		return body;
	}

}
