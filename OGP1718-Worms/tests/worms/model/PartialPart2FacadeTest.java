package worms.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import worms.facade.Facade;
import worms.facade.IFacade;
import worms.model.Worm;

public class PartialPart2FacadeTest {

	private static final double EPS = 1e-4;

	private IFacade facade;

	// X X X X //
	// . . . . //
	// . . . . //
	// X X X X //
	private boolean[][] passableMap = new boolean[][] { //
			{ false, false, false, false }, //
			{ true, true, true, true }, //
			{ true, true, true, true }, //
			{ false, false, false, false } };

	private World world;

	@Before
	public void setup() {
		facade = new Facade();
		world = facade.createWorld(4.0, 4.0, passableMap);
	}

	@Test
	public void testMaximumActionPoints() {
		Worm worm = facade.createWorm(world, new double[] { 1, 2 }, 0, 1, "Test", null);
		assertEquals(4448, facade.getMaxNbActionPoints(worm));
	}

	@Test
	public void testMoveHorizontal() {
		Worm worm = facade.createWorm(world, new double[] { 1, 2 }, 0, 1, "Test", null);
		facade.move(worm);
		double[] xy = facade.getLocation(worm);
		assertEquals(2, xy[0], EPS);
		assertEquals(2, xy[1], EPS);
	}

	@Test
	public void testMoveVertical() {
		Worm worm = facade.createWorm(world, new double[] { 1, 1.5 }, Math.PI / 2, 0.5, "Test", null);
		facade.move(worm);
		double[] xy = facade.getLocation(worm);
		assertEquals(1, xy[0], EPS);
		assertEquals(2.0, xy[1], EPS);
	}

	@Test
	public void testFall() {
		// . X .
		// . w .
		// . . .
		// X X X
		World world = facade.createWorld(3.0, 4.0, new boolean[][] { //
				{ true, false, true }, //
				{ true, true, true }, //
				{ true, true, true }, //
				{ false, false, false } });
		Worm worm = facade.createWorm(world, new double[] { 1.5, 2.5 }, 3*Math.PI/2, 0.5, "Test", null);
		assertFalse(facade.canFall(worm));
		facade.move(worm);
		assertTrue(facade.canFall(worm));
		facade.fall(worm);
		double[] xy = facade.getLocation(worm);
		assertEquals(1.5, xy[0], EPS);
		assertEquals(1.5, xy[1], EPS);
	}

}
