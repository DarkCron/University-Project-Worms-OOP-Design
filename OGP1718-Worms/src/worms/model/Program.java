package worms.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.kuleuven.cs.som.annotate.*;
import worms.parser.procedures.BaseProcedure;
import worms.parser.statements.BaseStatement;
import worms.parser.statements.StateSequence;

public class Program {
	
	public Program(List<BaseProcedure> procs, BaseStatement main) {
		this.procedures = procs;
		this.globalVariables = new HashMap<String, Object>();
		
		if(main instanceof StateSequence) {
			mainSequence = (StateSequence)main;
		}else {
			mainSequence = new StateSequence(main.getSource(), main);
		}
	}
	
	private final List<BaseProcedure> procedures;
	
	private final StateSequence mainSequence;
	
	private final Map<String, Object> globalVariables;
	
	@Basic @Raw @Immutable
	public Map<String, Object> getGlobals(){
		return globalVariables;
	}
	
	private int mainSequenceIndex = 0;
	
	public void doStartExecution() throws IllegalStateException{
		mainSequenceIndex = 0;
		
		if(mainSequence == null) {
			throw new IllegalStateException("mainSequence does not exist in program.");
		}

		execute();
	}

	private void execute() throws IllegalStateException,IllegalArgumentException{
		while(this.canContinueExecution()) {
			BaseStatement currentStatement = mainSequence.getSequence().get(mainSequenceIndex);
			if(currentStatement == null) {
				throw new IllegalStateException("mainSequence contains illegal statements in program.");
			}
			handleExecution(currentStatement);
			this.mainSequenceIndex++;
		}
	}

	private void handleExecution(BaseStatement currentStatement) throws IllegalStateException, IllegalArgumentException{
		currentStatement.execute(this);
	}

	private boolean canContinueExecution() {
		if(this.mainSequenceIndex >= mainSequence.getSequenceLength()) {
			return false;
		}
		return true;
	}
	
	private Worm programHolder = null;
	
	public void assignProgram(Worm w) {
		this.setProgramHolder(w);
		if(w != null) {
			w.assignProgram(this);
		}
	}
	
	public void setProgramHolder(Worm w) {
		this.programHolder = w;
	}
	
	public Worm getProgramHolder() {
		return this.programHolder;
	}
}
