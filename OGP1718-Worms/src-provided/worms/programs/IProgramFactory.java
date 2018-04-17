package worms.programs;

import java.util.ArrayList;
import java.util.List;

import worms.util.ModelException;
import worms.util.MustNotImplementException;

/**
 * A program factory is used by the parser ({@link ProgramParser}) to construct
 * an in-memory representation of your program. For example, when reading the
 * program source code
 * 
 * <pre>
 * print 2.0
 * </pre>
 * 
 * the parser will create a Program object by (conceptually) executing the
 * following code:
 * 
 * <pre>
 * factory.createProgram(Collections.emptyList(),
 * 		factory.createPrintStatement(factory.createDoubleLiteral(2.0)));
 * </pre>
 * 
 * on the returned factory object.
 * 
 * <p>
 * For testing, you may use the methods from {@link ProgramParser} yourself.
 * </p>
 *  
 * <p>
 * You should declare your class as follows:<code><pre>
 * public class ProgramFactory implements IProgramFactory&lt;MyExpression, MyStatement, MyProc, Program&gt;
 * </pre></code> where MyExpression, MyStatement, MyProc, and Program are your classes
 * for representing expressions, statements, procedure definitions, and programs,
 * respectively.
 * 
 * <p>
 * The SourceLocation object in the methods defined by this factory refers to
 * the location (line and column) in the text file where the statement or
 * expression begins.
 * 
 * @param <E>
 *            Your class for representing an expression.
 * @param <S>
 *            Your class for representing a statement.
 * @param <P>
 *            Your class for representing a procedure.
 * @param <Program>
 *            Your class for representing a program (should be Program).
 * 
 * 
 */
public interface IProgramFactory<E, S, P, Program> {

	/* PROGRAM */

	/**
	 * Create a program from the given arguments.
	 * 
	 * @param procs
	 *            The procedure definitions for the program.
	 * @param main
	 *            The main statement of the program. Most likely this is a
	 *            sequence statement.
	 * @return A new program.
	 */
	public Program createProgram(List<P> procs, S main) throws ModelException;

	/**
	 * Create a program from the given argument.
	 * 
	 * @param main
	 *            The main statement of the program. Most likely this is a
	 *            sequence statement.
	 * @return A new program without procedure definitions.
	 */
	public default Program createProgram(S main) throws ModelException {
		return createProgram(new ArrayList<P>(), main);
	}

	/* PROCEDURE DEFINITIONS */

	/**
	 * Create a procedure definition with the given name and body.
	 * 
	 * @param procedureName
	 *            The name of the procedure
	 * @param body
	 *            The body of the procedure.
	 */
	public P createProcedureDefinition(String procedureName, S body, SourceLocation sourceLocation);

	/* STATEMENTS */

	/**
	 * Create a statement that assigns the value obtained by evaluating the
	 * given expression to a variable with the given name.
	 */
	public S createAssignmentStatement(String variableName, E value, SourceLocation sourceLocation)
			throws ModelException;

	/**
	 * Create a print statement that prints the value obtained by evaluating the
	 * given expression.
	 */
	public S createPrintStatement(E value, SourceLocation sourceLocation) throws ModelException;

	/**
	 * Create a turn statement that makes the worm that is executing the program
	 * turn by the amount obtained by evaluating the given expression.
	 */
	public S createTurnStatement(E angle, SourceLocation location) throws ModelException;

	/**
	 * Create a statement that makes the worm that is executing the program move
	 * one step.
	 */
	public S createMoveStatement(SourceLocation location) throws ModelException;

	/**
	 * Returns a statement that makes the worm that is executing the program jump.
	 */
	public S createJumpStatement(SourceLocation location) throws ModelException;

	/**
	 * Returns a statement that makes the worm that is executing the program eat.
	 */
	public S createEatStatement(SourceLocation location);

	/**
	 * Returns a statement that makes the worm that is executing the program fire
	 * a weapon.
	 */
	public S createFireStatement(SourceLocation location) throws ModelException;

	/**
	 * Create a sequence of statements involving the given list of statements.
	 */
	public S createSequenceStatement(List<S> statements, SourceLocation sourceLocation) throws ModelException;

	/**
	 * Create an if-then-else statement involving the given condition and the
	 * given then-part and else-part.
	 *    The else-part may be null.
	 */
	public S createIfStatement(E condition, S ifBody, S elseBody, SourceLocation sourceLocation) throws ModelException;

	/**
	 * Create a while statement involving the given condition and given body.
	 */
	public S createWhileStatement(E condition, S body, SourceLocation sourceLocation) throws ModelException;

	/**
	 * Create a break statement.
	 */
	public default S createBreakStatement(SourceLocation sourceLocation)
			throws ModelException, MustNotImplementException {
		// Individual students must not work out this method.
		throw new MustNotImplementException();
	}

	/**
	 * Create an invoke statement that invokes the procedure with the given name.
	 */
	public default S createInvokeStatement(String procedureName, SourceLocation sourceLocation)
			throws ModelException, MustNotImplementException {
		// Individual students must not work out this method.
		throw new MustNotImplementException();
	}

	/* EXPRESSIONS */

	/**
	 * Create an expression that evaluates to the current value of the given
	 * variable.
	 */
	public E createReadVariableExpression(String variableName, SourceLocation sourceLocation) throws ModelException;

	/**
	 * Creates an expression that represents a literal double value.
	 */
	public E createDoubleLiteralExpression(double value, SourceLocation location) throws ModelException;

	/**
	 * Creates an expression that represents a literal boolean value.
	 */
	public E createBooleanLiteralExpression(boolean value, SourceLocation location) throws ModelException;

	/**
	 * Creates an expression that represents the null value.
	 */
	public E createNullExpression(SourceLocation location) throws ModelException;

	/**
	 * Creates an expression that represents the self value, evaluating to the
	 * worm that executes the program.
	 */
	public E createSelfExpression(SourceLocation location) throws ModelException;

	/**
	 * Returns an expression that evaluates to the addition of the values
	 * obtained by evaluating the given left and second expressions.
	 */
	public E createAdditionExpression(E left, E right, SourceLocation location) throws ModelException;

	/**
	 * Returns an expression that evaluates to the subtraction of the values
	 * obtained by evaluating the first and second given expressions.
	 */
	public default E createSubtractionExpression(E left, E right, SourceLocation location)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Returns an expression that evaluates to the multiplication of the values
	 * obtained by evaluating the first and second given expressions.
	 */
	public default E createMultiplicationExpression(E left, E right, SourceLocation location)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Returns an expression that evaluates to the division of the values
	 * obtained by evaluating the first and second given expressions.
	 */
	public default E createDivisionExpression(E left, E right, SourceLocation location)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Returns an expression that evaluates to the square root of the value
	 * obtained by evaluating the given expression.
	 */
	public default E createSqrtExpression(E e, SourceLocation location)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Returns an expression that evaluates to the sine of the value
	 * obtained by evaluating the given expression.
	 */
	public default E createSinExpression(E e, SourceLocation location)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Returns an expression that evaluates to the cosine of the value
	 * obtained by evaluating the given expression.
	 */
	public default E createCosExpression(E e, SourceLocation location)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Returns an expression that evaluates to the conjunction of the values
	 * obtained by evaluating the given left and right expressions.
	 */
	public E createAndExpression(E left, E right, SourceLocation sourceLocation) throws ModelException;

	/**
	 * Returns an expression that evaluates to the disjunction of the values
	 * obtained by evaluating the given left and right expressions.
	 */
	public default E createOrExpression(E left, E right, SourceLocation sourceLocation)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Returns an expression that evaluates to true when the given expression
	 * evaluates to false, and vice versa.
	 */
	public E createNotExpression(E expression, SourceLocation sourceLocation) throws ModelException;

	/**
	 * Returns an expression that evaluates to true if the evaluation of the
	 * left expression yields a value that is equal to the value obtained by
	 * evaluating the right expression.
	 */
	public E createEqualityExpression(E left, E right, SourceLocation location) throws ModelException;

	/**
	 * Returns an expression that evaluates to true if the evaluation of the
	 * left expression yields a value that is different from the value obtained by
	 * evaluating the right expression.
	 */
	public default E createInequalityExpression(E left, E right, SourceLocation sourceLocation)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Returns an expression that evaluates to true if the evaluation of the
	 * left expression yields a value that is less than the value obtained by
	 * evaluating the right expression.
	 */
	public E createLessThanExpression(E left, E right, SourceLocation location);

	/**
	 * Returns an expression that evaluates to true if the evaluation of the
	 * left expression yields a value that is less than or equal to the value obtained by
	 * evaluating the right expression.
	 */
	public default E createLessThanOrEqualExpression(E left, E right, SourceLocation sourceLocation)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Returns an expression that evaluates to true if the evaluation of the
	 * left expression yields a value that is greater than the value obtained by
	 * evaluating the right expression.
	 */
	public default E createGreaterThanExpression(E left, E right, SourceLocation sourceLocation)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Returns an expression that evaluates to true if the evaluation of the
	 * left expression yields a value that is greater than or equal to the value obtained by
	 * evaluating the right expression.
	 */
	public default E createGreaterThanOrEqualExpression(E left, E right, SourceLocation sourceLocation)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Returns an expression that evaluates to the position along the x-axis of
	 * the game object to which the given expression evaluates.
	 */
	public default E createGetXExpression(E e, SourceLocation location)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Returns an expression that evaluates to the position along the y-axis of
	 * the game object to which the given expression evaluates.
	 */
	public default E createGetYExpression(E e, SourceLocation location)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Returns an expression that evaluates to the radius of the game object to which
	 * the given expression evaluates.
	 */
	public default E createGetRadiusExpression(E e, SourceLocation location)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Returns an expression that evaluates to the direction of the game object to which
	 * the given expression evaluates.
	 */
	public default E createGetDirectionExpression(E entity, SourceLocation location)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Returns an expression that evaluates to the number of action points of the worm to which
	 * the given expression evaluates.
	 */
	public default E createGetActionPointsExpression(E entity, SourceLocation sourceLocation)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Returns an expression that evaluates to the maximum number of action points of the worm to which
	 * the given expression evaluates.
	 */
	public default E createGetMaxActionPointsExpression(E entity, SourceLocation sourceLocation)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Returns an expression that evaluates to the number of hit points of the game object to which
	 * the given expression evaluates.
	 */
	public default E createHitPointsExpression(E entity, SourceLocation sourceLocation)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	public E createSearchObjectExpression(E angleDelta, SourceLocation sourceLocation) throws ModelException;

	/**
	 * Returns an expression that evaluates to a boolean reflecting whether or not
	 * the executing worm belongs to the same team as the worm to which the given
	 * expression evaluates.
	 */
	public default E createSameTeamExpression(E entity, SourceLocation sourceLocation)
			throws ModelException, MustNotImplementException {
		// Individual students must not work out this method.
		throw new MustNotImplementException();
	}

	/**
	 * Returns an expression that evaluates to the distance between the executing worm
	 * and the game object to which the given expression evaluates.
	 */
	public E createDistanceExpression(E entity, SourceLocation sourceLocation) throws ModelException;

	/**
	 * Returns a boolean indicating whether or not the object to which the given
	 * expression evaluates is a worm.
	 */
	public E createIsWormExpression(E entity, SourceLocation sourceLocation) throws ModelException;

	/**
	 * Returns a boolean indicating whether or not the object to which the given
	 * expression evaluates is a portion of food.
	 */
	public default E createIsFoodExpression(E entity, SourceLocation sourceLocation)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Returns a boolean indicating whether or not the object to which the given
	 * expression evaluates is a projectile.
	 */
	public default E createIsProjectileExpression(E entity, SourceLocation sourceLocation)
			throws ModelException, MustNotImplementException {
		throw new MustNotImplementException();
	}

}
