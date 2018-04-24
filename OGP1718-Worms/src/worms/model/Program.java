package worms.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.kuleuven.cs.som.annotate.*;
import worms.internal.gui.game.IActionHandler;
import worms.parser.procedures.BaseProcedure;
import worms.parser.statements.BaseStatement;
import worms.parser.statements.StateSequence;

public class Program {
	
	public Program(List<BaseProcedure> procs, BaseStatement main) {
		this.procedures = new HashMap<String, BaseProcedure>();
		this.globalVariables = new HashMap<String, Object>();
		
		for (BaseProcedure baseProcedure : procs) {
			this.procedures.put(baseProcedure.getProcedureName(), baseProcedure);
		}
		
		if(main instanceof StateSequence) {
			mainSequence = (StateSequence)main;
		}else {
			mainSequence = new StateSequence(main.getSource(), main);
		}
	}
	
	private IActionHandler actionHandler;
	
	public void setActionHandler(IActionHandler actionHandler) {
		this.actionHandler = actionHandler;
	}
	
	public IActionHandler getActionHandler() {
		return this.actionHandler;
	}
	
	private final Map<String, BaseProcedure> procedures;
	
	public Map<String, BaseProcedure> getProcedures() {
		return procedures;
	}
	
	private final StateSequence mainSequence;
	
	private final Map<String, Object> globalVariables;
	
	@Basic @Raw @Immutable
	public Map<String, Object> getGlobals(){
		return globalVariables;
	}
	
	private final List<Object> printLog = new ArrayList<Object>();
	
	public void addToPrintLog(Object string){
		printLog.add(string);
	}
	
	public List<Object> getPrintLog(){
		return this.printLog;
	}
	
	public List<Object> doStartExecution() throws IllegalStateException{
		printLog.clear();
		
		if(mainSequence == null) {
			throw new IllegalStateException("mainSequence does not exist in program.");
		}

		mainSequence.execute(this,null);

		return this.getPrintLog();
	}
	
	private Worm programHolder = null;
	
	public void assignProgram(Worm w, IActionHandler handler) {
		this.setProgramHolder(w);
		if(w != null) {
			w.assignProgram(this,handler);
			this.setActionHandler(handler);
		}
	}
	
	public void setProgramHolder(Worm w) {
		this.programHolder = w;
	}
	
	public Worm getProgramHolder() {
		return this.programHolder;
	}
	
	public void interruptProgram() {
		System.out.println("Program should break");
	}


}
