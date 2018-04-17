package worms.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import worms.facade.Facade;
import worms.facade.IFacade;
import worms.internal.gui.game.IActionHandler;
import worms.model.Worm;
import worms.programs.IProgramFactory;
import worms.programs.ProgramParser;

public class PartialPart3FacadeTest {

	private static final double EPS = 1e-4;

	private IFacade facade;

	@Before
	public void setup() {
		facade = new Facade();
	}

	
	@Test
	public void testProgram() {
		World world = facade.createWorld(100.0, 100.0, new boolean[][] { {true}, {false} });
		IProgramFactory<?, ?, ?, ? extends Program> programFactory = facade.createProgramFactory();

		Program program = ProgramParser.parseProgramFromString("x := 0; while x < 1.5: { x := x + 0.1; } turn x;", programFactory);
		Worm worm = facade.createWorm(world, new double[] { 50.0, 50.51 }, 0, 0.5, "TestWorm", null);
		IActionHandler handler = new SimpleActionHandler(facade);
		facade.loadProgramOnWorm(worm, program, handler);
		double oldOrientation = facade.getOrientation(worm);
		facade.startGame(world); // this will run the program
		double newOrientation = facade.getOrientation(worm);
		assertEquals(oldOrientation + 1.5, newOrientation, EPS);
	}

}
