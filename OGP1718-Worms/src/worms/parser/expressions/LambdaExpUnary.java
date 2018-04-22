package worms.parser.expressions;

import worms.model.Program;

public class LambdaExpUnary extends LambdaExpression{

	@FunctionalInterface
	public interface Unary<V,E> {
		V set(Program p, E e);
	}

}
