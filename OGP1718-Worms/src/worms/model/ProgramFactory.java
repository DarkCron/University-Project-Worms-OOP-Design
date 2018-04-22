package worms.model;

import java.util.List;

import worms.parser.expressions.LambdaExpAssignment;
import worms.parser.expressions.LambdaExpBinary;
import worms.parser.expressions.LambdaExpLiteral;
import worms.parser.expressions.LambdaExpUnary;
import worms.parser.expressions.LambdaExpression;
import worms.parser.procedures.BaseProcedure;
import worms.parser.statements.BaseStatement;
import worms.parser.statements.StateAssignment;
import worms.parser.statements.StatePrint;
import worms.parser.statements.StateSequence;
import worms.programs.IProgramFactory;
import worms.programs.SourceLocation;
import worms.util.ModelException;

public class ProgramFactory implements IProgramFactory<LambdaExpression, BaseStatement, BaseProcedure, Program> {


	@Override
	public Program createProgram(List<BaseProcedure> procs, BaseStatement main) throws ModelException {
		// TODO Auto-generated method stub
		return new Program(procs, main);
	}

	@Override
	public BaseProcedure createProcedureDefinition(String procedureName, BaseStatement body,
			SourceLocation sourceLocation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseStatement createAssignmentStatement(String variableName, LambdaExpression value,
			SourceLocation sourceLocation) throws ModelException {
		LambdaExpAssignment assignment = new LambdaExpAssignment();
		assignment.setExpression((p) -> LambdaExpression.VARIABLE_ASSIGN.set(p,variableName, value));
		StateAssignment assigner = new StateAssignment(sourceLocation, assignment);
		return assigner;
	}

	@Override
	public BaseStatement createPrintStatement(LambdaExpression value, SourceLocation sourceLocation)
			throws ModelException {
		LambdaExpUnary printer = new LambdaExpUnary();
		printer.setExpression((p) -> LambdaExpression.PRINTER.set(p,value));
		return new StatePrint(sourceLocation, printer);
	}

	@Override
	public BaseStatement createTurnStatement(LambdaExpression angle, SourceLocation location) throws ModelException {
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
		return new StateSequence(sourceLocation, statements);
	}

	@Override
	public BaseStatement createIfStatement(LambdaExpression condition, BaseStatement ifBody, BaseStatement elseBody,
			SourceLocation sourceLocation) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseStatement createWhileStatement(LambdaExpression condition, BaseStatement body,
			SourceLocation sourceLocation) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LambdaExpression createReadVariableExpression(String variableName, SourceLocation sourceLocation)
			throws ModelException {
		LambdaExpUnary reader = new LambdaExpUnary();
		reader.setExpression((p)->LambdaExpression.VARIABLE_READ.set(p,variableName));
		return reader;
	}

	@Override
	public LambdaExpression createDoubleLiteralExpression(double value, SourceLocation location) throws ModelException {
		LambdaExpLiteral literal = new LambdaExpLiteral();
		literal.setExpression((p) -> LambdaExpression.LITERAL_DOUBLE.set(p,value));
		return literal;
	}

	@Override
	public LambdaExpression createBooleanLiteralExpression(boolean value, SourceLocation location)
			throws ModelException {
		LambdaExpLiteral literal = new LambdaExpLiteral();
		literal.setExpression((p) -> LambdaExpression.LITERAL_BOOLEAN.set(p,value));
		return literal;
	}

	@Override
	public LambdaExpression createNullExpression(SourceLocation location) throws ModelException {
		LambdaExpLiteral literal = new LambdaExpLiteral();
		literal.setExpression((p) -> LambdaExpression.LITERAL_NULL.set(p,null));
		return literal;
	}

	@Override
	public LambdaExpression createSelfExpression(SourceLocation location) throws ModelException {
		LambdaExpLiteral literal = new LambdaExpLiteral();
		literal.setExpression((p) -> LambdaExpression.GET_SELF.set(p,null));
		return literal;
	}

	@Override
	public LambdaExpression createAdditionExpression(LambdaExpression left, LambdaExpression right,
			SourceLocation location) throws ModelException {
		LambdaExpBinary addition = new LambdaExpBinary();
		addition.setExpression((p) -> LambdaExpression.ADDER.set(p,left,right));
		return addition;
	}

	@Override
	public LambdaExpression createAndExpression(LambdaExpression left, LambdaExpression right,
			SourceLocation sourceLocation) throws ModelException {
		LambdaExpBinary logicOperation = new LambdaExpBinary();
		logicOperation.setExpression((p)-> LambdaExpression.LOGIC_AND.set(p, left,right));
		return logicOperation;
	}

	@Override
	public LambdaExpression createNotExpression(LambdaExpression expression, SourceLocation sourceLocation)
			throws ModelException {
		LambdaExpUnary logicOperation = new LambdaExpUnary();
		logicOperation.setExpression((p)-> LambdaExpression.LOGIC_NOT.set(p, expression));
		return logicOperation;
	}

	@Override
	public LambdaExpression createEqualityExpression(LambdaExpression left, LambdaExpression right,
			SourceLocation location) throws ModelException {
		LambdaExpBinary logicOperation = new LambdaExpBinary();
		logicOperation.setExpression((p)-> LambdaExpression.LOGIC_EQUALITY.set(p, left,right));
		return logicOperation;
	}

	@Override
	public LambdaExpression createLessThanExpression(LambdaExpression left, LambdaExpression right,
			SourceLocation location) {
		LambdaExpBinary logicOperation = new LambdaExpBinary();
		logicOperation.setExpression((p)-> LambdaExpression.LOGIC_LESS_THAN.set(p, left,right));
		return logicOperation;
	}

	@Override
	public LambdaExpression createSearchObjectExpression(LambdaExpression angleDelta, SourceLocation sourceLocation)
			throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LambdaExpression createDistanceExpression(LambdaExpression entity, SourceLocation sourceLocation)
			throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LambdaExpression createIsWormExpression(LambdaExpression entity, SourceLocation sourceLocation)
			throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

}
