package worms.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import worms.facade.Facade;
import worms.internal.gui.game.IActionHandler;
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
		testSelf.assignProgram(program,null);
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
		testSelf.assignProgram(program,null);
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
		testSelf.assignProgram(program,null);
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
	public void ifTest() {
		System.out.println("PRINT IF TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString(""
				+ "a := true;"
				+ "if a:{"
				+ "b := 5;"
				+ "print b + b;}"
				+ "else:"
				+ "print a;", myFactory);
		program.doStartExecution();
		System.out.println("*************************");
	}
	
	@Test
	public void whileTest() {
		System.out.println("PRINT WHILE (+IF) TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString(""
				+ "a := 1.0;"
				+ "while a < 10:{"
				+ "print a;"
				+ "a := a + 1.0;"
				+ "	if a == 5.0:{"
				+ "		print 999;"
				+ "	}"
				+ "}"
				+ "print a;" , myFactory);
		program.doStartExecution();
		System.out.println("*************************");
	}
	
	@Test
	public void whileBreakTest() {
		System.out.println("PRINT WHILE BREAK TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString(""
				+ "a := 0.0;"
				+ "while a < 10:{"
				+ "a := a + 0.5;"
				+ "a := a + 0.5;"
				+ "if 0.0 < a:{ "
				+ "	print a;"
				+ "	if a == 5.0:{"
				+ "		break;"
				+ "	}"
				+ "}"
				+ "}"
				+ "print a;" , myFactory);
		program.doStartExecution();
		System.out.println("*************************");
	}
	
	@Test
	public void mainBreakTest() {
		System.out.println("PRINT UNUSABLE BREAK TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString(""
				+ "a := 0.0;"
				+ "break;"
				+ "print a;", myFactory);
		program.doStartExecution();
		System.out.println("*************************");
	}
	
	boolean[][] map10x10 = new boolean[][] { { false, false, false, false, false, false, false, false, false, false },
		{ true, true, true, true, true, true, true, true, true, false },
		{ true, true, true, true, true, true, true, true, true, false },
		{ true, true, true, true, true, true, true, true, true, false },
		{ true, true, true, true, true, true, true, true, true, false },
		{ true, true, true, true, true, true, true, true, true, false },
		{ true, true, true, true, true, true, true, true, true, false },
		{ true, true, true, true, true, true, true, true, true, false },
		{ true, true, true, true, true, true, true, true, true, false },
		{ true, true, true, true, true, true, true, true, true, false }, };
	
	Facade facade = new Facade();
		
	@Test
	public void testSearch() {
		World w = new World(map10x10);
		double[] worm_location1 = new double[] { 5.0, 7.5 };
		double[] worm_location2 = new double[] { 7.5, 7.5 }; //RIGHT
		double[] worm_location3 = new double[] { 2.5, 7.5 }; //LEFT
		double worm_orientation = 0;
		double worm_radius = 1.5;
		
		Worm theWorm = facade.createWorm(w, worm_location1, Math.PI, worm_radius, "Worm A", null); // -> looks left
		Worm theWorm2 = facade.createWorm(w, worm_location2, worm_orientation, worm_radius, "Worm B", null);
		Worm theWorm3 = facade.createWorm(w, worm_location3, worm_orientation, worm_radius, "Worm C", null);
		
		System.out.println("PRINT SEARCH TEST");
		System.out.println("TheWorm ref code: " + theWorm);
		System.out.println("TheWorm2 ref code: " + theWorm2);
		System.out.println("TheWorm3 ref code: " + theWorm3);
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString("a := searchobj -3.14;print self;print a;print isfood a;print isworm a;", myFactory);
		theWorm.assignProgram(program,null);
		program.doStartExecution();
		System.out.println("*************************");
	}
	
	@Test
	public void testDistance() {
		World w = new World(map10x10);
		double[] worm_location1 = new double[] { 5.0, 8.5 };
		double[] worm_location2 = new double[] { 7.5, 8.5 }; //RIGHT
		double[] worm_location3 = new double[] { 2.5, 8.5 }; //LEFT
		double worm_orientation = 0;
		double worm_radius = 0.5;
		
		Worm theWorm = facade.createWorm(w, worm_location1, Math.PI, worm_radius, "Worm A", null); // -> looks left
		Worm theWorm2 = facade.createWorm(w, worm_location2, worm_orientation, worm_radius, "Worm B", null);
		Worm theWorm3 = facade.createWorm(w, worm_location3, worm_orientation, worm_radius, "Worm C", null);
		
		System.out.println("PRINT SEARCH TEST");
		System.out.println("TheWorm ref code: " + theWorm);
		System.out.println("TheWorm2 ref code: " + theWorm2);
		System.out.println("TheWorm3 ref code: " + theWorm3);
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString("a := searchobj -3.14;print self;print a;print distance a;", myFactory);
		theWorm.assignProgram(program,null);
		program.doStartExecution();
		System.out.println("*************************");
	}
	
	@Test
	public void testSameTeam() {
		World w = new World(map10x10);
		double[] worm_location1 = new double[] { 3.5, 7.5 };
		double[] worm_location2 = new double[] { 7.5, 7.5 };
		double worm_orientation = 0;
		double worm_radius = 1.5;
		
		Worm theWorm = facade.createWorm(w, worm_location1, worm_orientation, worm_radius, "Worm A", null);
		Worm theWorm2 = facade.createWorm(w, worm_location2, worm_orientation, worm_radius, "Worm B", null);

		System.out.println("PRINT SAME TEAM TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString("a := searchobj 0;print sameteam a;", myFactory);
		theWorm.assignProgram(program,null);
		program.doStartExecution();
		w.terminate();
		
		w = new World(map10x10);
		Team team = facade.createTeam(w, "TheTeam");
		theWorm = facade.createWorm(w, worm_location1, worm_orientation, worm_radius, "Worm A", team);
		theWorm2 = facade.createWorm(w, worm_location2, worm_orientation, worm_radius, "Worm B", team);
		program = ProgramParser.parseProgramFromString("a := searchobj 0;print sameteam a;", myFactory);
		theWorm.assignProgram(program,null);
		program.doStartExecution();
		w.terminate();
		
		w = new World(map10x10);
		theWorm = facade.createWorm(w, worm_location1, worm_orientation, worm_radius, "Worm A", team);
		theWorm2 = facade.createWorm(w, worm_location2, worm_orientation, worm_radius, "Worm B", null);
		program = ProgramParser.parseProgramFromString("a := searchobj 0;print sameteam a;", myFactory);
		theWorm.assignProgram(program,null);
		program.doStartExecution();
		w.terminate();
		System.out.println("*************************");
	}
	
	String programText = "// Worm that turns towards nearest worm, fires, and moves; or turns and tries to jump if no worm is in sight.\r\n" + 
			"\r\n" + 
			"def updateNearestWorm: {\r\n" + 
			"  delta := 0;\r\n" + 
			"  nearestWorm := null;\r\n" + 
			"  turnToNearest := 0;\r\n" + 
			"  while delta < 6.28: {\r\n" + 
			"    obj := searchobj delta;\r\n" + 
			"    if (obj != null && isworm obj):\r\n" + 
			"      if !sameteam obj:\r\n" + 
			"	      if (nearestWorm == null) || ((distance obj) < (distance nearestWorm)): {\r\n" + 
			"	        nearestWorm := obj;\r\n" + 
			"	        turnToNearest := delta;\r\n" + 
			"	      }\r\n" + 
			"    delta := delta + 0.2;\r\n" + 
			"  }\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"// main program\r\n" + 
			"\r\n" + 
			"while true: {\r\n" + 
			"  invoke updateNearestWorm;\r\n" + 
			"  \r\n" + 
			"  if nearestWorm != null: {\r\n" + 
			"    print nearestWorm;\r\n" + 
			"    print distance nearestWorm;\r\n" + 
			"    turn turnToNearest;\r\n" + 
			"    fire;\r\n" + 
			"    move;\r\n" + 
			"  }\r\n" + 
			"  else {\r\n" + 
			"    turn 0.2;\r\n" + 
			"    jump;\r\n" + 
			"  }\r\n" + 
			"}\r\n" + 
			"";
	@Test
	public void testProgram() {
		World w = facade.createWorld(10.0, 10.0, map10x10);
		IActionHandler actionHandler = new SimpleActionHandler(facade);
		double[] worm_location1 = new double[] { 3.5, 7.5 };
		double[] worm_location2 = new double[] { 7.5, 7.5 };
		double worm_orientation = 0;
		double worm_radius = 1.5;
		
		Worm theWorm = facade.createWorm(w, worm_location1, worm_orientation, worm_radius, "Worm A", null);
		Worm theWorm2 = facade.createWorm(w, worm_location2, worm_orientation, worm_radius, "Worm B", null);

		System.out.println("PRINT PROGRAM TEST");
		ProgramFactory myFactory = new ProgramFactory();
		Program program = ProgramParser.parseProgramFromString(programText, myFactory);
		theWorm.assignProgram(program,actionHandler);
		
		try {
			program.doStartExecution();
			program.wait();
			this.wait();
		} catch (Exception e) {

		}
		System.out.println(w);
		//w.terminate();
	}
}
