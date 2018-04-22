package worms.parser.expressions;

import worms.model.Program;

public class LambdaExpRead extends LambdaExpression{


	@FunctionalInterface
	public interface Read {
		LambdaExpression set(String key,Program p);
	}
}
