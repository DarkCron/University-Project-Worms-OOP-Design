package worms.parser.statements;

import java.util.Map;

import worms.parser.expressions.ExpReadVar;
import worms.programs.SourceLocation;

public class StatePrint extends BaseStatement {

	public StatePrint(SourceLocation sourceLoc, ExpReadVar expressionReadToPrint) {
		super(sourceLoc);
		this.expressionReadToPrint = expressionReadToPrint;
	}

	private final ExpReadVar expressionReadToPrint;

	@Override
	public void execute(Map<String, Object> globalVariables) throws IllegalArgumentException{
		try {
			System.out.println(globalVariables.get(expressionReadToPrint.getVariableName()).toString());
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
}
