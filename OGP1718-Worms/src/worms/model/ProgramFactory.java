package worms.model;

import java.util.List;

import worms.exceptions.NotEnoughAPException;
import worms.parser.expressions.LambdaExpression;
import worms.parser.procedures.BaseProcedure;
import worms.parser.statements.BaseStatement;
import worms.parser.statements.StateAction;
import worms.parser.statements.StateAssignment;
import worms.parser.statements.StateBreak;
import worms.parser.statements.StateIf;
import worms.parser.statements.StatePrint;
import worms.parser.statements.StateProcedure;
import worms.parser.statements.StateSequence;
import worms.parser.statements.StateWhile;
import worms.programs.IProgramFactory;
import worms.programs.SourceLocation;
import worms.util.ModelException;
import worms.util.MustNotImplementException;

public class ProgramFactory implements IProgramFactory<LambdaExpression, BaseStatement, BaseProcedure, Program> {


	@Override
	public Program createProgram(List<BaseProcedure> procs, BaseStatement main) throws ModelException {
		return new Program(procs, main);
	}
	
	/**
	 * PROCEDURES
	 */

	@Override
	public BaseProcedure createProcedureDefinition(String procedureName, BaseStatement body,
			SourceLocation sourceLocation) {
		return new BaseProcedure(sourceLocation, procedureName, new StateSequence(sourceLocation, body));
	}
	
	/**
	 * STATEMENTS
	 */
	
	@Override
	public BaseStatement createInvokeStatement(String procedureName, SourceLocation sourceLocation)
			throws ModelException, MustNotImplementException {
		LambdaExpression.Unary<BaseStatement, String> invokeProcedure = (p,name) -> {
			return p.getProcedures().get(name).getBody();
		};
		return new StateProcedure(sourceLocation,procedureName, new LambdaExpression((p)->invokeProcedure.set(p, procedureName)));
	}

	@Override
	public BaseStatement createAssignmentStatement(String variableName, LambdaExpression value,
			SourceLocation sourceLocation) throws ModelException {
		LambdaExpression assignment = new LambdaExpression((p) -> LambdaExpression.VARIABLE_ASSIGN.set(p,variableName, value));
		StateAssignment assigner = new StateAssignment(sourceLocation, assignment);
		return assigner;
	}

	@Override
	public BaseStatement createPrintStatement(LambdaExpression value, SourceLocation sourceLocation)
			throws ModelException {
		LambdaExpression printer = new LambdaExpression((p) -> LambdaExpression.PRINTER.set(p,value));
		return new StatePrint(sourceLocation, printer);
	}

	@Override
	public BaseStatement createTurnStatement(LambdaExpression angle, SourceLocation location) throws ModelException {
		LambdaExpression.Unary<Void, LambdaExpression> turnExpression = (p,a) -> {
			if(p.getProgramHolder() == null) {
				throw new IllegalStateException();
			}
			if(a == null || a.getExpression() == null) {
				throw new IllegalStateException();
			}
			Object delta = a.getExpression().getExpressionResult(p);
			if(delta instanceof Double) {
				try {
					if(p.getProgramHolder().getTurnCost((Double)delta)>p.getProgramHolder().getCurrentActionPoints()) {
						throw new NotEnoughAPException("");
					}
					p.getActionHandler().turn(p.getProgramHolder(),(Double)delta);
				} catch (NotEnoughAPException e) {
					p.interruptProgram();
				}catch (Exception e) {
					p.interruptProgram();
					throw e;
				}
				
			}else {
				throw new IllegalArgumentException("Type mismatch to double in turn action");
			}
			
//			if(p.getProgramHolder().getCurrentActionPoints() == 0) {
//				p.interruptProgram();
//			}
			return null;
		};
		return new StateAction(location, new LambdaExpression((p) -> turnExpression.set(p, angle)));
	}

	@Override
	public BaseStatement createMoveStatement(SourceLocation location) throws ModelException {
		LambdaExpression.Unary<Void, Void> moveExpression = (p,a) -> {
			if(p.getProgramHolder() == null) {
				throw new IllegalStateException();
			}
			try {
				if(!p.getActionHandler().move(p.getProgramHolder())) {
					throw new RuntimeException();
				}
			} catch (Exception e) {
				p.interruptProgram();
			}

//			if(p.getProgramHolder().getCurrentActionPoints() == 0) {
//				p.interruptProgram();
//			}
			return null;
		};
		return new StateAction(location, new LambdaExpression((p) -> moveExpression.set(p, null)));
	}

	@Override
	public BaseStatement createJumpStatement(SourceLocation location) throws ModelException {
		LambdaExpression.Unary<Void, Void> jumpExpression = (p,a) -> {
			if(p.getProgramHolder() == null) {
				throw new IllegalStateException();
			}
			try {
				p.getActionHandler().jump(p.getProgramHolder());
			} catch (NotEnoughAPException e) {
				p.interruptProgram();
			} catch (Exception e) {
				p.interruptProgram();
				throw e;
			}
//			
//			if(p.getProgramHolder().getCurrentActionPoints() == 0) {
//				p.interruptProgram();
//			}
			return null;
		};
		return new StateAction(location, new LambdaExpression((p) -> jumpExpression.set(p, null)));
	}

	@Override
	public BaseStatement createEatStatement(SourceLocation location) {
		LambdaExpression.Unary<Void, Void> eatExpression = (p,a) -> {
			if(p.getProgramHolder() == null) {
				throw new IllegalStateException();
			}
			try {
				if(p.getProgramHolder().getCurrentActionPoints() < 8) { //TODO constants
					throw new NotEnoughAPException("");
				}
				p.getActionHandler().eat(p.getProgramHolder());
			} catch (NotEnoughAPException e) {
				p.interruptProgram();
			} catch (Exception e) {
				throw e;
			}

			return null;
		};
		return new StateAction(location, new LambdaExpression((p) -> eatExpression.set(p, null)));
	}

	@Override
	public BaseStatement createFireStatement(SourceLocation location) throws ModelException {
		LambdaExpression.Unary<Void, Void> fireExpression = (p,a) -> {
			if(p.getProgramHolder() == null) {
				throw new IllegalStateException();
			}
			try {
				if(!p.getActionHandler().fire(p.getProgramHolder())) {
					throw new RuntimeException();
				}
			} catch (Exception e) {
				p.interruptProgram();
			}
	
			if(p.getProgramHolder().getCurrentActionPoints() == 0) {
				p.interruptProgram();
			}
			return null;
		};
		return new StateAction(location, new LambdaExpression((p) -> fireExpression.set(p, null)));
	}
	
	@Override
	public BaseStatement createBreakStatement(SourceLocation sourceLocation)
			throws ModelException, MustNotImplementException {
		return new StateBreak(sourceLocation, null);
	}

	@Override
	public BaseStatement createSequenceStatement(List<BaseStatement> statements, SourceLocation sourceLocation)
			throws ModelException {
		return new StateSequence(sourceLocation, statements);
	}

	@Override
	public BaseStatement createIfStatement(LambdaExpression condition, BaseStatement ifBody, BaseStatement elseBody,
			SourceLocation sourceLocation) throws ModelException {
		return new StateIf(sourceLocation, condition, ifBody, elseBody);
	}

	@Override
	public BaseStatement createWhileStatement(LambdaExpression condition, BaseStatement body,
			SourceLocation sourceLocation) throws ModelException {
		return new StateWhile(sourceLocation, condition, body);
	}

	/**
	 * EXPRESSIONS 
	 */
	
	@Override
	public LambdaExpression createReadVariableExpression(String variableName, SourceLocation sourceLocation)
			throws ModelException {
		return new LambdaExpression((p)->LambdaExpression.VARIABLE_READ.set(p,variableName));
	}

	@Override
	public LambdaExpression createDoubleLiteralExpression(double value, SourceLocation location) throws ModelException {
		return new LambdaExpression((p) -> LambdaExpression.LITERAL_DOUBLE.set(p,value));
	}

	@Override
	public LambdaExpression createBooleanLiteralExpression(boolean value, SourceLocation location)
			throws ModelException {
		return new LambdaExpression((p) -> LambdaExpression.LITERAL_BOOLEAN.set(p,value));
	}

	@Override
	public LambdaExpression createNullExpression(SourceLocation location) throws ModelException {
		return new LambdaExpression((p) -> LambdaExpression.LITERAL_NULL.set(p,null));
	}

	@Override
	public LambdaExpression createGetDirectionExpression(LambdaExpression entity, SourceLocation location)
			throws ModelException, MustNotImplementException {
		return new LambdaExpression((p) -> LambdaExpression.GET_SELF_DIRECTION.set(p,null));
	}
	
	@Override
	public LambdaExpression createSelfExpression(SourceLocation location) throws ModelException {
		return new LambdaExpression((p) -> LambdaExpression.GET_SELF.set(p,null));
	}

	@Override
	public LambdaExpression createAdditionExpression(LambdaExpression left, LambdaExpression right,
			SourceLocation location) throws ModelException {
		return new LambdaExpression((p) -> LambdaExpression.ADDER.set(p,left,right));
	}

	@Override
	public LambdaExpression createAndExpression(LambdaExpression left, LambdaExpression right,
			SourceLocation sourceLocation) throws ModelException {
		return new LambdaExpression((p)-> LambdaExpression.LOGIC_AND.set(p, left,right));
	}
	
	@Override
	public LambdaExpression createOrExpression(LambdaExpression left, LambdaExpression right,
			SourceLocation sourceLocation) throws ModelException {
		return new LambdaExpression((p)-> LambdaExpression.LOGIC_OR.set(p, left,right));
	}

	@Override
	public LambdaExpression createNotExpression(LambdaExpression expression, SourceLocation sourceLocation)
			throws ModelException {
		return new LambdaExpression((p)-> LambdaExpression.LOGIC_NOT.set(p, expression));
	}

	@Override
	public LambdaExpression createEqualityExpression(LambdaExpression left, LambdaExpression right,
			SourceLocation location) throws ModelException {
		return new LambdaExpression((p)-> LambdaExpression.LOGIC_EQUALITY.set(p, left,right));
	}
	
	@Override
	public LambdaExpression createInequalityExpression(LambdaExpression left, LambdaExpression right,
			SourceLocation location) throws ModelException {
		return new LambdaExpression((p)-> LambdaExpression.LOGIC_INEQUALITY.set(p, left,right));
	}

	@Override
	public LambdaExpression createLessThanExpression(LambdaExpression left, LambdaExpression right,
			SourceLocation location) {
		return new LambdaExpression((p)-> LambdaExpression.LOGIC_LESS_THAN.set(p, left,right));
	}
	
	@Override
	public LambdaExpression createGreaterThanExpression(LambdaExpression left, LambdaExpression right,
			SourceLocation location) {
		return new LambdaExpression((p)-> LambdaExpression.LOGIC_GREATER_THAN.set(p, left,right));
	}

	/**
	 * The expression searchobj e returns the closest game
	 * object, the center of which is on a direct line from the center of the executing
	 * worm in the direction of theta+e. Null is returned if no such game object exists.
	 * The line may partially or complete go through impassable terrain. The expression
	 * distance e returns the distance between the executing worm and
	 * the entity e. The expressions isworm e, isfood e and isprojectile e can
	 * be used to determine the type of an entity expression e.
	 */
	@Override
	public LambdaExpression createSearchObjectExpression(LambdaExpression angleDelta, SourceLocation sourceLocation)
			throws ModelException {
		return new LambdaExpression((p) -> LambdaExpression.SEARCH_OBJECT.set(p, angleDelta));
	}

	@Override
	public LambdaExpression createDistanceExpression(LambdaExpression entity, SourceLocation sourceLocation)
			throws ModelException {
		return new LambdaExpression((p) -> LambdaExpression.DISTANCE_FROM.set(p, entity));
	}

	@Override
	public LambdaExpression createIsWormExpression(LambdaExpression entity, SourceLocation sourceLocation)
			throws ModelException {
		return new LambdaExpression((p) -> LambdaExpression.IS_WORM.set(p, entity));
	}

	@Override
	public LambdaExpression createIsFoodExpression(LambdaExpression entity, SourceLocation sourceLocation)
			throws ModelException, MustNotImplementException {
		return new LambdaExpression((p) -> LambdaExpression.IS_FOOD.set(p, entity));
	}
	
	@Override
	public LambdaExpression createIsProjectileExpression(LambdaExpression entity, SourceLocation sourceLocation)
			throws ModelException, MustNotImplementException {
		return new LambdaExpression((p) -> LambdaExpression.IS_PROJECTILE.set(p, entity));
	}
	
	@Override
	public LambdaExpression createSameTeamExpression(LambdaExpression entity, SourceLocation sourceLocation)
			throws ModelException, MustNotImplementException {
		return new LambdaExpression((p) -> LambdaExpression.SAME_TEAM.set(p,entity));
	}
}
