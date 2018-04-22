package worms.parser.expressions;

import worms.model.Program;
import worms.parser.KeyValuePair;

public class LambdaExpAssignment extends LambdaExpression{

	@FunctionalInterface
	public interface Binary<R,V> {
		KeyValuePair<String, R> set(Program p,String key,V v);
	}
}
