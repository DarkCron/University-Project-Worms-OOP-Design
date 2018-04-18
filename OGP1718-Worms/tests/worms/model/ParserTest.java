package worms.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import worms.programs.ProgramParser;

public class ParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void assignmentTest() {
		System.out.println("ASSIGNMENT TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString("a := 5;\nprint a;", myFactory);
		program.doStartExecution();
		System.out.println("************************");
	}
	
	@Test
	public void additionAssignmentTest() {
		System.out.println("ADDITION ASSIGNMENT TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString("a := 5 + 6;\nprint a;", myFactory);
		program.doStartExecution();
		System.out.println("*************************");
	}

	@Test
	public void additionTest() {
		System.out.println("ADDITION TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString("a := 5;\na := 5 + 5;\nprint a;", myFactory);
		program.doStartExecution();
		System.out.println("*************************");
	}
}
