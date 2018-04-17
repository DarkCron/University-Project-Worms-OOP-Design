package worms.programs.internal.parser.example;

import java.util.List;

public class PrintingProgram {

	private List<PrintingObject> procedures;
	private PrintingObject main;

	public PrintingProgram(List<PrintingObject> procedures, PrintingObject main) {
		if (main == null) {
			throw new NullPointerException("main null");
		}
		this.main = main;
		this.procedures = procedures;
	}

	@Override
	public String toString() {
		return "Procedures: " + procedures.toString() + "\nMain:" + main.toString();
	}
}