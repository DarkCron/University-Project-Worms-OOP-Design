package worms.parser.expressions;

import be.kuleuven.cs.som.annotate.*;
import worms.model.Program;
import worms.model.Worm;

@Value
public class LambdaExpression {

	public LambdaExpression(Expression<?> exp) {
		myExpression = exp;
	}
	
	/**
	 * The functional interface representing the Lambda Expression of this anonymous class.
	 * This interface should point to the actual lambda expression to be executed.
	 * 
	 * @author bernd
	 *
	 * @param <V>
	 */
	@FunctionalInterface
	public interface Expression<V> {
		V getExpressionResult(Program p);
	}
	
	@FunctionalInterface
	public interface Unary<V,E> {
		V set(Program p, E e);
	}
	
	/**
	 * Can actually be replaced by the Unary expression above as 
	 * Assignment<Double> == Unary<Double,Double>
	 * 
	 * @author bernd
	 *
	 * @param <V>
	 */
	@FunctionalInterface
	public interface Assignment<V> {
		V set(Program p,V v);
	}
	
	@FunctionalInterface
	public interface Binary<V,L,R> {
		V set(Program p,L l,R r);
	}

	private final Expression<?> myExpression;

	@Raw @Basic @Immutable
	public Expression<?> getExpression() {
		return this.myExpression;
	}

	public final static Binary<Double, LambdaExpression, LambdaExpression> ADDER = (p, a, b) -> {
		Object resultLeft = a.getExpression().getExpressionResult(p);
		Object resultRight = b.getExpression().getExpressionResult(p);
		if (resultLeft instanceof Double && resultRight instanceof Double) {
			return (Double) a.getExpression().getExpressionResult(p)
					+ (Double) b.getExpression().getExpressionResult(p);
		} else {
			throw new IllegalArgumentException("Tried to add non Double assignments");
		}
	};
	public final static Binary<String, LambdaExpression, LambdaExpression> CONCAT = (p, a, b) -> {
		try {
			return (String) a.getExpression().getExpressionResult(p)
					+ (String) b.getExpression().getExpressionResult(p);
		} catch (Exception e) {
			throw new IllegalArgumentException("Tried to add non String assignments");
		}
	};

	public final static Unary<Boolean, LambdaExpression> LOGIC_NOT = (p, exp) -> {
		Object result = exp.getExpression().getExpressionResult(p);
		if (result instanceof Boolean) {
			return !(Boolean) result;
		} else {
			throw new IllegalArgumentException("Logic not argument wasn't boolean expression");
		}
	};
	public final static Binary<Boolean, LambdaExpression, LambdaExpression> LOGIC_AND = (p, a, b) -> {
		Object resultLeft = a.getExpression().getExpressionResult(p);
		Object resultRight = b.getExpression().getExpressionResult(p);
		if (resultLeft instanceof Boolean && resultRight instanceof Boolean) {
			return (Boolean) a.getExpression().getExpressionResult(p)
					&& (Boolean) b.getExpression().getExpressionResult(p);
		} else {
			throw new IllegalArgumentException("Tried to compare non booleans");
		}
	};
	public final static Binary<Boolean, LambdaExpression, LambdaExpression> LOGIC_EQUALITY = (p, a,b) ->
	{
		Object resultLeft = a.getExpression().getExpressionResult(p);
		Object resultRight = b.getExpression().getExpressionResult(p);
		if ((resultLeft instanceof Boolean && resultRight instanceof Boolean)
				|| (resultLeft instanceof Double && resultRight instanceof Double)) {
			return a.getExpression().getExpressionResult(p) == b.getExpression().getExpressionResult(p);
		} else if(resultLeft!= null && resultRight != null){
			return resultLeft.equals(resultRight);
		}else if(resultLeft== null && resultRight == null){
			return true;
		}else {
			throw new IllegalArgumentException();
		}
	};
	public final static Binary<Boolean, LambdaExpression, LambdaExpression> LOGIC_LESS_THAN = (p, a, b) -> 
	{
		Object resultLeft = a.getExpression().getExpressionResult(p);
		Object resultRight = b.getExpression().getExpressionResult(p);
		if (resultLeft instanceof Double && resultRight instanceof Double) {
			return (Double) a.getExpression().getExpressionResult(p) < (Double) b.getExpression()
					.getExpressionResult(p);
		} else {
			throw new IllegalArgumentException("Tried to compare non Doubles");
		}
	};

	public final static Assignment<Double> LITERAL_DOUBLE = (p, a) -> a;
	public final static Assignment<String> LITERAL_STRING = (p, a) -> a;
	public final static Assignment<Boolean> LITERAL_BOOLEAN = (p, a) -> a;
	public final static Assignment<Void> LITERAL_NULL = (p, a) -> null;

	public final static Unary<Void, LambdaExpression> PRINTER = (p, a) -> {
		System.out.println(a.getExpression().getExpressionResult(p));
		return null;
	};

	public final static Binary<Object, String, LambdaExpression> VARIABLE_ASSIGN = (p, name, exp) -> {
		p.getGlobals().put(name, exp.getExpression().getExpressionResult(p));
		return null;
	};
	public final static Unary<Object, String> VARIABLE_READ = (p, name) -> p.getGlobals().get(name);
	public final static Assignment<Worm> GET_SELF = (p, v) -> p.getProgramHolder();
}
