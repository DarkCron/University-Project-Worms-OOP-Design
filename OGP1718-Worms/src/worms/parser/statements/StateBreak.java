package worms.parser.statements;

import worms.model.Program;
import worms.parser.expressions.LambdaExpression;
import worms.programs.SourceLocation;

public class StateBreak extends BaseStatement {

	public StateBreak(SourceLocation sourceLoc, LambdaExpression expression) {
		super(sourceLoc, expression);
	}

	@Override
	public void execute(Program parent, BaseStatement caller) throws IllegalArgumentException, IllegalStateException {
		super.execute(parent, caller);
		
		if(caller != null) {
			BaseStatement block = caller;
			//TODO procedure body detection + handling
			while(block != null && !(block instanceof StateWhile)) {  //Search for a while statement
				block = block.getParentBlock();
			}
			if(block != null) { //Meaning that we haven't found the program's main loop yet.
				if(block instanceof StateWhile) { //If this statement is part of a while block
					BaseStatement stopAt = block;
					((StateWhile) block).interrupt();
					block = caller;
					while(block != stopAt) {  //Search for a while statement
						if(block instanceof StateSequence) {
						//	((StateSequence) block).interrupt(); //Ensure that all sequences are interrupted up until the while loop of this block.
						}
						block = block.getParentBlock();
					}
				}
			}
		}
	}

}
