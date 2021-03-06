package worms.parser.statements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import worms.model.Program;
import worms.programs.SourceLocation;

public class StateSequence extends BaseStatement {

	public StateSequence(SourceLocation sourceLoc, Collection<BaseStatement> sequence) {
		super(sourceLoc,null);
		this.sequence = new ArrayList<BaseStatement>(sequence);
	}
	
	public StateSequence(SourceLocation sourceLoc, BaseStatement single) {
		super(sourceLoc,null);
		this.sequence = new ArrayList<BaseStatement>();
		this.sequence.add(single);
	}
	
	private ArrayList<BaseStatement> sequence;
	
	public int getSequenceLength() {
		return sequence.size();
	}
	
	public List<BaseStatement> getSequence(){
		ArrayList<BaseStatement> sequenceCopy = new ArrayList<>(sequence);
		return sequenceCopy;
	}
	
	private int sequenceIndex = 0;
	
	private int getSequenceIndex() {
		return sequenceIndex;
	}
	
	private void setSequenceIndex(int index) {
		this.sequenceIndex = index;
	}

	@Override
	public void execute(Program parent, BaseStatement caller) throws IllegalArgumentException, IllegalStateException {
		super.execute(parent, caller);
		
		if(sequence == null) {
			throw new IllegalStateException();
		}
		
		while(!this.isInterrupted() && this.getSequenceIndex() < this.getSequenceLength()) {
			sequence.get(this.getSequenceIndex()).execute(parent, this);
			setSequenceIndex(getSequenceIndex()+1);
		}
		
		if(!this.isInterrupted) {
			this.setSequenceIndex(0); //Make sure this block is runnable next time.
		}else {
			this.clearInterrupt(); //This block is interrupted, clear the interruption so that next time it can continue.
			setSequenceIndex(getSequenceIndex()-1);
			if(this.getSequenceIndex() < 0) {
				setSequenceIndex(0);
			}
			//this.setSequenceIndex(0);
		}
		
	}

	private boolean isInterrupted = false;
	
	private boolean isInterrupted() {
		return this.isInterrupted;
	}
	
	@Override
	public void interrupt() {
		isInterrupted = true;
		
		for (BaseStatement baseStatement : sequence) {
			baseStatement.interrupt();
		}
	}
	
	private void clearInterrupt() {
		isInterrupted = false;
	}

	@Override
	public void invokeBreak() {
		interrupt();
		
	}
	
	@Override
	public StateSequence clone() throws CloneNotSupportedException {
		StateSequence clone = (StateSequence)super.clone();
		clone.sequence = new ArrayList<BaseStatement>();
		for (BaseStatement baseStatement : sequence) {
			clone.sequence.add(baseStatement.clone());
		}
		clone.setSequenceIndex(0);
		return clone;
	}
}
