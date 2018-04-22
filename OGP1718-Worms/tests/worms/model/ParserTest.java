package worms.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import worms.model.values.Direction;
import worms.model.values.Location;
import worms.model.values.Name;
import worms.model.values.Radius;
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
		Program program = ProgramParser.parseProgramFromString("a := 5;\na := a + a;\nprint a;", myFactory);
		program.doStartExecution();
		System.out.println("*************************");
	}

	@Test
	public void printNumberTest() {
		System.out.println("NUMBER PRINT TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString("print 5.0;", myFactory);
		program.doStartExecution();
		System.out.println("*************************");
	}
	
	@Test
	public void printNumberAdditionTest() {
		System.out.println("NUMBER PRINT TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString("print 5.0 + 5.0;", myFactory);
		program.doStartExecution();
		System.out.println("*************************");
	}

	@Test
	public void printBooleanTest() {
		System.out.println("NUMBER Boolean TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString("print true;print false;", myFactory);
		program.doStartExecution();
		System.out.println("*************************");
	}

	@Test
	public void printNullTest() {
		System.out.println("NUMBER NULL TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString("print null;", myFactory);
		program.doStartExecution();
		System.out.println("*************************");
	}

	@Test
	public void printSelfTest() {
		System.out.println("PRINT SELF TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString("print self;", myFactory);
		Worm testSelf = new Worm(new Location(0, 0), new Direction(0), null, new Radius(World.getWormMinimumRadius()),
				Name.DEFAULT_NAME, null);
		testSelf.assignProgram(program);
		program.doStartExecution();
		System.out.println("*************************");
	}
	
	@Test
	public void printLogicNotTest() {
		System.out.println("PRINT LOGIC NOT TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString("a := true;print ! a;", myFactory);
		program.doStartExecution();
		System.out.println("*************************");
	}

	@Test
	public void printETest() {
		System.out.println("PRINT E TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString("print (e);", myFactory);
		Worm testSelf = new Worm(new Location(0, 0), new Direction(0), null, new Radius(World.getWormMinimumRadius()),
				Name.DEFAULT_NAME, null);
		testSelf.assignProgram(program);
		program.doStartExecution();
		System.out.println("*************************");
	}
	
	@Test
	public void printLogicANDTest() {
		System.out.println("PRINT LOGIC NOT TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString("a := true;b:= false;print a && b;", myFactory);
		program.doStartExecution();
		program = ProgramParser.parseProgramFromString("a := true;b:= true;print a && b;", myFactory);
		program.doStartExecution();
		try {
			program = ProgramParser.parseProgramFromString("a := 5.0;b:= true;print a && b;", myFactory);
			program.doStartExecution();
			fail();
		} catch (Exception e) {
			System.out.println("Succeeded type check");
		}
		System.out.println("*************************");
	}
	
	@Test
	public void printLogicLesserTest() {
		System.out.println("PRINT LOGIC NOT TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString("a := 5.0;b:= 10;print a < b;", myFactory);
		program.doStartExecution();
		program = ProgramParser.parseProgramFromString("a := 10;b:= 5;print a < b;", myFactory);
		program.doStartExecution();
		try {
			program = ProgramParser.parseProgramFromString("a := 5.0;b:= true;print a && b;", myFactory);
			program.doStartExecution();
			fail();
		} catch (Exception e) {
			System.out.println("Succeeded type check");
		}
		System.out.println("*************************");
	}
	
	@Test
	public void printLogicEqualTest() {
		System.out.println("PRINT LOGIC NOT TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString("a := 5.0;b:= 5.0;print a == b;", myFactory);
		program.doStartExecution();
		program = ProgramParser.parseProgramFromString("a := 8;b:= 5;print a == b;", myFactory);
		program.doStartExecution();
		program = ProgramParser.parseProgramFromString("a := self;b:= self;print a == b;", myFactory);
		Worm testSelf = new Worm(new Location(0, 0), new Direction(0), null, new Radius(World.getWormMinimumRadius()),
				Name.DEFAULT_NAME, null);
		testSelf.assignProgram(program);
		program.doStartExecution();
		try {
			program = ProgramParser.parseProgramFromString("a := 5.0;b:= true;print a && b;", myFactory);
			program.doStartExecution();
			fail();
		} catch (Exception e) {
			System.out.println("Succeeded type check");
		}
		System.out.println("*************************");
	}
}
