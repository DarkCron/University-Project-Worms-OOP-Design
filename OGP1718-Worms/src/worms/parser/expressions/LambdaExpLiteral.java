package worms.parser.expressions;

import worms.model.Program;

public class LambdaExpLiteral extends LambdaExpression{

	@FunctionalInterface
	public interface Assignment<V> {
		V set(Program p,V v);
	}
}
