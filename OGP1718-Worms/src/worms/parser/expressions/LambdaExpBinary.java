package worms.parser.expressions;

import worms.model.Program;

public class LambdaExpBinary extends LambdaExpression{

	@FunctionalInterface
	public interface Binary<V,L,R> {
		V set(Program p,L l,R r);
	}


}
