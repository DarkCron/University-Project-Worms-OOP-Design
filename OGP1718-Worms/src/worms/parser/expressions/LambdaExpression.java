package worms.parser.expressions;

import worms.model.Program;
import worms.model.Worm;

public abstract class LambdaExpression {

	@FunctionalInterface
	public interface Expression<V> {
		V getExpressionResult(Program p);
	}

	private Expression<?> myExpression;

	public void setExpression(Expression<?> exp) {
		this.myExpression = exp;
	}

	public Expression<?> getExpression() {
		return this.myExpression;
	}

	public final static LambdaExpBinary.Binary<Double, LambdaExpression, LambdaExpression> ADDER = (p, a, b) -> {
		Object resultLeft = a.getExpression().getExpressionResult(p);
		Object resultRight = b.getExpression().getExpressionResult(p);
		if (resultLeft instanceof Double && resultRight instanceof Double) {
			return (Double) a.getExpression().getExpressionResult(p)
					+ (Double) b.getExpression().getExpressionResult(p);
		} else {
			throw new IllegalArgumentException("Tried to add non Double assignments");
		}
	};
	public final static LambdaExpBinary.Binary<String, LambdaExpression, LambdaExpression> CONCAT = (p, a, b) -> {
		try {
			return (String) a.getExpression().getExpressionResult(p)
					+ (String) b.getExpression().getExpressionResult(p);
		} catch (Exception e) {
			throw new IllegalArgumentException("Tried to add non String assignments");
		}
	};

	public final static LambdaExpUnary.Unary<Boolean, LambdaExpression> LOGIC_NOT = (p, exp) -> {
		Object result = exp.getExpression().getExpressionResult(p);
		if (result instanceof Boolean) {
			return !(Boolean) result;
		} else {
			throw new IllegalArgumentException("Logic not argument wasn't boolean expression");
		}
	};
	public final static LambdaExpBinary.Binary<Boolean, LambdaExpression, LambdaExpression> LOGIC_AND = (p, a, b) -> {
		Object resultLeft = a.getExpression().getExpressionResult(p);
		Object resultRight = b.getExpression().getExpressionResult(p);
		if (resultLeft instanceof Boolean && resultRight instanceof Boolean) {
			return (Boolean) a.getExpression().getExpressionResult(p)
					&& (Boolean) b.getExpression().getExpressionResult(p);
		} else {
			throw new IllegalArgumentException("Tried to compare non booleans");
		}
	};
	public final static LambdaExpBinary.Binary<Boolean, LambdaExpression, LambdaExpression> LOGIC_EQUALITY = (p, a,b) ->
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
	public final static LambdaExpBinary.Binary<Boolean, LambdaExpression, LambdaExpression> LOGIC_LESS_THAN = (p, a,
			b) -> {
		Object resultLeft = a.getExpression().getExpressionResult(p);
		Object resultRight = b.getExpression().getExpressionResult(p);
		if (resultLeft instanceof Double && resultRight instanceof Double) {
			return (Double) a.getExpression().getExpressionResult(p) < (Double) b.getExpression()
					.getExpressionResult(p);
		} else {
			throw new IllegalArgumentException("Tried to compare non Doubles");
		}
	};

	public final static LambdaExpLiteral.Assignment<Double> LITERAL_DOUBLE = (p, a) -> a;
	public final static LambdaExpLiteral.Assignment<String> LITERAL_STRING = (p, a) -> a;
	public final static LambdaExpLiteral.Assignment<Boolean> LITERAL_BOOLEAN = (p, a) -> a;
	public final static LambdaExpLiteral.Assignment<Void> LITERAL_NULL = (p, a) -> null;

	public final static LambdaExpUnary.Unary<Void, LambdaExpression> PRINTER = (p, a) -> {
		System.out.println(a.getExpression().getExpressionResult(p));
		return null;
	};

	public final static LambdaExpAssignment.Binary<Object, LambdaExpression> VARIABLE_ASSIGN = (p, name, exp) -> {
		p.getGlobals().put(name, exp.getExpression().getExpressionResult(p));
		return null;
	};
	public final static LambdaExpUnary.Unary<Object, String> VARIABLE_READ = (p, name) -> p.getGlobals().get(name);
	public final static LambdaExpLiteral.Assignment<Worm> GET_SELF = (p, v) -> p.getProgramHolder();
}
