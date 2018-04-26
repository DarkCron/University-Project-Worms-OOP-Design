package worms.parser.procedures;

import worms.parser.statements.BaseStatement;
import worms.programs.SourceLocation;

public class BaseProcedure implements Cloneable{
	public BaseProcedure(SourceLocation sourceLocation,String procedureName, BaseStatement body) {
		this.sourceLocation = sourceLocation;
		this.procedureName = procedureName;
		this.body = body;
	}
	
	private final SourceLocation sourceLocation;
	private final String procedureName;
	private BaseStatement body;
	
	public SourceLocation getSourceLocation() {
		return sourceLocation;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public BaseStatement getBody() {
		return body;
	}

	@Override
	public BaseProcedure clone() throws CloneNotSupportedException {
		BaseProcedure clone = (BaseProcedure)super.clone();
		clone.body = body.clone();
		return clone;
	}
}
