package worms.model;

import java.util.List;

import worms.parser.expressions.BaseExpression;
import worms.parser.expressions.ExpAddition;
import worms.parser.expressions.ExpAssignment;
import worms.parser.expressions.ExpDoubleValue;
import worms.parser.expressions.ExpLogical;
import worms.parser.expressions.ExpReadVar;
import worms.parser.procedures.BaseProcedure;
import worms.parser.statements.BaseStatement;
import worms.parser.statements.StateAssignment;
import worms.parser.statements.StatePrint;
import worms.parser.statements.StateSequence;
import worms.programs.IProgramFactory;
import worms.programs.SourceLocation;
import worms.util.ModelException;

public class ProgramFactory implements IProgramFactory<BaseExpression, BaseStatement, BaseProcedure, Program> {

	@Override
	public Program createProgram(List<BaseProcedure> procs, BaseStatement main) throws ModelException {
		// TODO Auto-generated method stub
		return new Program(procs, main);
	}

	@Override
	public BaseProcedure createProcedureDefinition(String procedureName, BaseStatement body, SourceLocation sourceLocation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseStatement createAssignmentStatement(String variableName, BaseExpression value, SourceLocation sourceLocation)
			throws ModelException {
		// TODO Auto-generated method stub
		if(value instanceof ExpAssignment) {
			return new StateAssignment(sourceLocation,variableName,(ExpAssignment) value);
		}else if(value instanceof ExpLogical) {
			return new StateAssignment(sourceLocation,variableName,((ExpLogical)value).asExpAssignment());
		}else {
			throw new ModelException("Invalid assign expression to assign in statement. @CreateAssignmentStatement-ProgramFactory");
		}
		
	}

	@Override
	public BaseStatement createPrintStatement(BaseExpression value, SourceLocation sourceLocation) throws ModelException {
		// TODO Auto-generated method stub
		if(value instanceof ExpReadVar) {
			return new StatePrint(sourceLocation, (ExpReadVar) value);
		}else {
			throw new ModelException("Invalid expression to read variable to print."
					+ "@CreatePrintStatement - ProgramFactory");
		}
		
	}

	@Override
	public BaseStatement createTurnStatement(BaseExpression angle, SourceLocation location) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseStatement createMoveStatement(SourceLocation location) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseStatement createJumpStatement(SourceLocation location) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseStatement createEatStatement(SourceLocation location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseStatement createFireStatement(SourceLocation location) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseStatement createSequenceStatement(List<BaseStatement> statements, SourceLocation sourceLocation)
			throws ModelException {
		// TODO Auto-generated method stub
		return new StateSequence(sourceLocation, statements);
	}

	@Override
	public BaseStatement createIfStatement(BaseExpression condition, BaseStatement ifBody, BaseStatement elseBody,
			SourceLocation sourceLocation) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseStatement createWhileStatement(BaseExpression condition, BaseStatement body, SourceLocation sourceLocation)
			throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseExpression createReadVariableExpression(String variableName, SourceLocation sourceLocation)
			throws ModelException {
		// TODO Auto-generated method stub
		return new ExpReadVar(sourceLocation, variableName);
	}

	@Override
	public BaseExpression createDoubleLiteralExpression(double value, SourceLocation location) throws ModelException {
		// TODO Auto-generated method stub
		return new ExpDoubleValue(location,value);
	}

	@Override
	public BaseExpression createBooleanLiteralExpression(boolean value, SourceLocation location) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseExpression createNullExpression(SourceLocation location) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseExpression createSelfExpression(SourceLocation location) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseExpression createAdditionExpression(BaseExpression left, BaseExpression right, SourceLocation location)
			throws ModelException {
		// TODO Auto-generated method stub
		if(left instanceof ExpAssignment && right instanceof ExpAssignment) {
			return new ExpAddition(location, (ExpAssignment)left, (ExpAssignment)right);
		}
		throw new ModelException("Illegal expressions left and right for logical expressions.");
	}

	@Override
	public BaseExpression createAndExpression(BaseExpression left, BaseExpression right, SourceLocation sourceLocation)
			throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseExpression createNotExpression(BaseExpression expression, SourceLocation sourceLocation)
			throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseExpression createEqualityExpression(BaseExpression left, BaseExpression right, SourceLocation location)
			throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseExpression createLessThanExpression(BaseExpression left, BaseExpression right, SourceLocation location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseExpression createSearchObjectExpression(BaseExpression angleDelta, SourceLocation sourceLocation)
			throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseExpression createDistanceExpression(BaseExpression entity, SourceLocation sourceLocation)
			throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseExpression createIsWormExpression(BaseExpression entity, SourceLocation sourceLocation)
			throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

}
