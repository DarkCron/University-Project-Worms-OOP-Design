package worms.programs.internal.parser.example;

import java.io.IOException;

import worms.programs.IProgramFactory;
import worms.programs.ParseOutcome;
import worms.programs.ProgramParser;

public class ExamplePrinter {
	public static void main(String[] args) throws IOException {
		IProgramFactory<PrintingObject, PrintingObject, PrintingObject, PrintingProgram> factory = PrintingObjectFactory.create();
		ProgramParser<?, ?, ?, PrintingProgram> parser = ProgramParser.create(factory);

		/*
		ParseOutcome<PrintingProgram> outcome = parser.parseString(
				"def controlled_move:"
				+ "  if getap self > 100.0:"
				+ "    { jump; print getx self; print gety self; }"
				+ "  else move;"
				+ ""
				+ "max_distance := 10.0;"
				+ "while true: {"
				+ "  w := searchobj 0.0;"
				+ "  if isworm w: {"
				+ "    if sameteam w:"
				+ "      invoke controlled_move;"
				+ "    else if distance w < max_distance:"
				+ "      fire;"
				+ "    else"
				+ "      { turn d; max_distance := max_distance + 0.1; }"
				+ "  }"
				+ "}");
				*/
		//ParseOutcome<PrintingProgram> outcome = parser.parseFile("programs/program_example.txt");
		//ParseOutcome<PrintingProgram> outcome = parser.parseFile("programs/syntax_test.txt");
		ParseOutcome<PrintingProgram> outcome = parser.parseFile("programs/program.txt");
		
		System.out.println(outcome);
		
	}

}
