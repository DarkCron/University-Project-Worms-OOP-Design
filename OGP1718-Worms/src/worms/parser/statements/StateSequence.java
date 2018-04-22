package worms.parser.statements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import worms.model.Program;
import worms.programs.SourceLocation;

public class StateSequence extends BaseStatement {

	public StateSequence(SourceLocation sourceLoc, Collection<BaseStatement> sequence) {
		super(sourceLoc,null);
		this.sequence = sequence;
	}
	
	public StateSequence(SourceLocation sourceLoc, BaseStatement single) {
		super(sourceLoc,null);
		this.sequence = new ArrayList<BaseStatement>();
		this.sequence.add(single);
	}
	
	private final Collection<BaseStatement> sequence;
	
	public int getSequenceLength() {
		return sequence.size();
	}
	
	public List<BaseStatement> getSequence(){
		ArrayList<BaseStatement> sequenceCopy = new ArrayList<>(sequence);
		return sequenceCopy;
	}

	@Override
	public void execute(Program parent) throws IllegalArgumentException, IllegalStateException {
		throw new IllegalStateException("Should not execute state sequence");
	}
}
