package worms.parser.statements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import worms.programs.SourceLocation;

public class StateSequence extends BaseStatement {

	public StateSequence(SourceLocation sourceLoc, Collection<BaseStatement> sequence) {
		super(sourceLoc);
		this.sequence = sequence;
	}
	
	public StateSequence(SourceLocation sourceLoc, BaseStatement single) {
		super(sourceLoc);
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
}
