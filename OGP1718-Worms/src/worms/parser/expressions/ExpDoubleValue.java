package worms.parser.expressions;

import be.kuleuven.cs.som.annotate.Value;
import worms.programs.SourceLocation;

@Value
public class ExpDoubleValue extends ExpAssignment{
	
	
	//TODO
	public ExpDoubleValue(SourceLocation sourceLoc, double value) {
		super(sourceLoc,value);
	}
}
