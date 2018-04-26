package worms.parser.expressions;

import be.kuleuven.cs.som.annotate.*;
import worms.model.Food;
import worms.model.GameObject;
import worms.model.Program;
import worms.model.Projectile;
import worms.model.Worm;
import worms.model.values.Location;

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
		if(resultLeft instanceof Boolean) {
			if((Boolean)resultLeft == false) {
				return false;
			}
		}
		Object resultRight = b.getExpression().getExpressionResult(p);
		if (resultLeft instanceof Boolean && resultRight instanceof Boolean) {
			return (Boolean) a.getExpression().getExpressionResult(p)
					&& (Boolean) b.getExpression().getExpressionResult(p);
		} else {
			throw new IllegalArgumentException("Tried to compare non booleans");
		}
	};
	public final static Binary<Boolean, LambdaExpression, LambdaExpression> LOGIC_OR = (p, a, b) -> {
		Object resultLeft = a.getExpression().getExpressionResult(p);
		if(resultLeft instanceof Boolean) {
			if((Boolean)resultLeft == false) {
				return false;
			}
		}
		
		Object resultRight = b.getExpression().getExpressionResult(p);
		if (resultLeft instanceof Boolean && resultRight instanceof Boolean) {
			return (Boolean) a.getExpression().getExpressionResult(p)
					|| (Boolean) b.getExpression().getExpressionResult(p);
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
			return resultLeft.equals(resultRight);
		} else if(resultLeft!= null && resultRight != null){
			return resultLeft.equals(resultRight);
		}else if(resultLeft== null && resultRight == null){
			return true;
		}else if(resultLeft != null || resultRight != null){
			return resultLeft == resultRight;
		}else {
			throw new IllegalArgumentException();
		}
	};
	public final static Binary<Boolean, LambdaExpression, LambdaExpression> LOGIC_INEQUALITY = (p, a,b) ->
	{
		Object resultLeft = a.getExpression().getExpressionResult(p);
		Object resultRight = b.getExpression().getExpressionResult(p);
		if ((resultLeft instanceof Boolean && resultRight instanceof Boolean)
				|| (resultLeft instanceof Double && resultRight instanceof Double)) {
			return !resultLeft.equals(resultRight);
		} else if(resultLeft!= null && resultRight != null){
			return !resultLeft.equals(resultRight);
		}else if(resultLeft == null && resultRight == null){
			return false;
		}else if(resultLeft != null || resultRight != null){
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
	
	public final static Unary<Boolean, LambdaExpression> IS_FOOD = (p, a) -> 
	{
		Object resultLeft = a.getExpression().getExpressionResult(p);
		if (resultLeft instanceof Food) {
			return true;
		} else {
			return false;
		}
	};
	public final static Unary<Boolean, LambdaExpression> IS_WORM = (p, a) -> 
	{
		Object resultLeft = a.getExpression().getExpressionResult(p);
		if (resultLeft instanceof Worm) {
			return true;
		} else {
			return false;
		}
	};
	public final static Unary<Boolean, LambdaExpression> IS_PROJECTILE = (p, a) -> 
	{
		Object resultLeft = a.getExpression().getExpressionResult(p);
		if (resultLeft instanceof Projectile) {
			return true;
		} else {
			return false;
		}
	};

	public final static Assignment<Double> LITERAL_DOUBLE = (p, a) -> a;
	public final static Assignment<String> LITERAL_STRING = (p, a) -> a;
	public final static Assignment<Boolean> LITERAL_BOOLEAN = (p, a) -> a;
	public final static Assignment<Void> LITERAL_NULL = (p, a) -> null;

	public final static Unary<Void, LambdaExpression> PRINTER = (p, a) -> {
		//String print = a.getExpression().getExpressionResult(p) != null ? a.getExpression().getExpressionResult(p).toString() : "null";
		System.out.println(a.getExpression().getExpressionResult(p));
		p.addToPrintLog(a.getExpression().getExpressionResult(p));
		return null;
	};

	public final static Binary<Object, String, LambdaExpression> VARIABLE_ASSIGN = (p, name, exp) -> {
		if(p.getProcedures().containsKey(name)) {
			throw new IllegalArgumentException();
		}
		p.getGlobals().put(name, exp.getExpression().getExpressionResult(p));
		return null;
	};
	public final static Unary<Object, String> VARIABLE_READ = (p, name) -> {
		if(!p.getGlobals().containsKey(name)) {
			throw new IllegalArgumentException();
		}
		return p.getGlobals().get(name);
	};
	public final static Assignment<Worm> GET_SELF = (p, v) -> p.getProgramHolder();
	
	public final static Unary<Double, LambdaExpression> DISTANCE_FROM = (p, other) -> {
		Object resultLeft = other.getExpression().getExpressionResult(p);
		if(resultLeft instanceof GameObject) {
			double distanceCenters = ((GameObject)resultLeft).getLocation().getDistanceFrom(p.getProgramHolder().getLocation());
			distanceCenters-= (((GameObject)resultLeft).getRadius().getRadius() + p.getProgramHolder().getRadius().getRadius());
			return distanceCenters;
		}else {
			//return Double.POSITIVE_INFINITY;//TODO What to do here check or from program
			
			throw new IllegalArgumentException("Tried to compare non GameObjects");		
		}
	};
	public final static Unary<Boolean, LambdaExpression> SAME_TEAM = (p, other) -> {
		Object resultLeft = other.getExpression().getExpressionResult(p);
		if(resultLeft == null) {
			return false;
		}
		if(resultLeft instanceof Worm) {
			if(((Worm)resultLeft).getTeam() == null) {
				return false; //TODO if both or either teams are null they aren't on the same team I assume.
			}
			return ((Worm)resultLeft).getTeam().equals(p.getProgramHolder().getTeam());
		}else {
			throw new IllegalArgumentException("Tried to compare non Worms");		
		}
	};
	public final static Unary<Object, LambdaExpression> SEARCH_OBJECT = (p, other) -> {
		
		if(p.getProgramHolder()==null || p.getProgramHolder().getWorld() == null) {
			throw new IllegalStateException();
		}
		
		Object resultLeft = other.getExpression().getExpressionResult(p);
		if(resultLeft instanceof Double) {
			double angle = ((Double)resultLeft) + p.getProgramHolder().getDirection().getAngle();
			double distanceDelta = 0.1f;
			double distance = 0.1f;
			boolean bContinue = true;
			while(bContinue) {
				double x = p.getProgramHolder().getLocation().getX() + Math.cos(angle) * distance;
				double y = p.getProgramHolder().getLocation().getY() + Math.sin(angle) * distance;
				Location loc = new Location(x,y);
				for (Object gameObject : p.getProgramHolder().getWorld().getAllGameObjects()) {
					if(gameObject instanceof GameObject) {
						if(!gameObject.equals(p.getProgramHolder())) {
							if(((GameObject) gameObject).getSurface().contains(loc)) {
								return gameObject;
							}
						}
					}
				}
				distance += distanceDelta;
				if(x < 0 || y < 0 || x > p.getProgramHolder().getWorld().getWorldWidth() || y > p.getProgramHolder().getWorld().getWorldHeight()) {
					bContinue = false;
				}
			}
			return null;
		}else {
			throw new IllegalArgumentException("Tried to compare non Worms");		
		}
	};
}
