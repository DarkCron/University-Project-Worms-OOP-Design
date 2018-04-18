package worms.parser.statements;

import java.util.Map;

import be.kuleuven.cs.som.annotate.Value;
import worms.programs.SourceLocation;

@Value
public abstract class BaseStatement {
	public BaseStatement(SourceLocation sourceLoc) {
		this.sourceLoc = sourceLoc;
	}
	
	private final SourceLocation sourceLoc;
	
	public final SourceLocation getSource() {
		return sourceLoc;
	}
	
	public void execute(Map<String, Object> globalVariables) throws IllegalArgumentException{};
}
