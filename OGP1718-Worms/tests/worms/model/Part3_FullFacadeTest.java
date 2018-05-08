package worms.model;

import static java.lang.Math.*;
import static org.junit.Assert.*;

import java.util.*;
import java.util.stream.IntStream;
import java.math.BigInteger;

import org.junit.*;

import worms.facade.Facade;
import worms.facade.IFacade;
import worms.internal.gui.game.IActionHandler;
import worms.programs.IProgramFactory;
import worms.model.ProgramFactory;
import worms.programs.ProgramParser;
import worms.util.*;

public class Part3_FullFacadeTest {

	private static int max_score = 0;
	private static int score = 0;

	private static final double EPS = 1e-3;
	
	public final static double GAME_STANDARD_ACCELERATION = 5.0;

	private final static IFacade facade = new Facade();
	private final static IActionHandler actionHandler = new SimpleActionHandler(facade);
	private final static ProgramFactory programFactory = new ProgramFactory();

	private static final double[] FIXTURE_LOCATION = new double[] { 3.0, -7.0 };
	private static final double FIXTURE_RADIUS = 0.30;
	private static final String FIXTURE_NAME = "Test";
	private static final double FIXTURE_DIRECTION = 3 * PI / 7;
	private static final int FIXTURE_MAX_ACTION_POINTS = referenceMaxActionPoints(FIXTURE_RADIUS);
	private static Food fixtureFood;

	private static Worm fixtureWorm;
	private boolean[][] map10x10, map5x5;
	private static World theWorld, otherWorld;
	private static Team theTeam = null;

	@Before
	public void setUp() {
		fixtureWorm = facade.createWorm(null, FIXTURE_LOCATION, FIXTURE_DIRECTION, FIXTURE_RADIUS, FIXTURE_NAME, null);
		fixtureFood = facade.createFood(null, FIXTURE_LOCATION);
		map10x10 = new boolean[][] { { false, false, false, false, false, false, false, false, false, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false }, };
		theWorld = facade.createWorld(10.0, 10.0, map10x10);
		map5x5 = new boolean[][] { { false, false, false, false, false, }, { true, true, true, true, true, },
				{ true, true, true, true, true, }, { true, true, true, true, true, },
				{ true, true, true, true, true, }, };
		otherWorld = facade.createWorld(5.0, 5.0, map5x5);
		try {
			theTeam = facade.createTeam(theWorld, "TheTeam");
		} catch (MustNotImplementException exc) {
		}
	}

	@AfterClass
	public static void tearDownAfterClass() {
		System.out.println();
		System.out.println("===============================");
		System.out.println();
		System.out.println("   FINAL SCORE: " + score + "/" + max_score);
		System.out.println();
		System.out.println("   FINAL PERCENTAGE: " + Math.round(score*100/max_score) + "%");
		System.out.println();
	}

	/**********************
	 * AUXILIARY METHODS
	 **********************/

	private static final double DENSITY = 1062;

	private static final double referenceWormMass(double radius) {
		return DENSITY * (4.0 / 3.0 * PI * pow(radius, 3));
	}

	private static int referenceMaxActionPoints(double radius) {
		return (int) round(referenceWormMass(radius));
	}

	private static final double FULL_TURN_COST = 60;

	private static int referenceCostToTurn(double angle) {
		return (int) ceil(FULL_TURN_COST * abs(angle) / 2 / PI);
	}

	private static double referenceJumpDistance(int actionPoints, double radius, double theta) {
		double v0 = referenceJumpV0(actionPoints, radius);
		double d = v0 * v0 * sin(2 * theta) / GAME_STANDARD_ACCELERATION;
		return d;
	}

	private static double[] referenceJumpStep(double dt, double[] loc, long actionPoints, double radius, double theta) {
		double v0 = referenceJumpV0(actionPoints, radius);
		double v0x = v0 * cos(theta);
		double v0y = v0 * sin(theta);
		double x = loc[0] + v0x * dt;
		double y = loc[1] + v0y * dt - GAME_STANDARD_ACCELERATION * dt * dt / 2;

		return new double[] { x, y };
	}

	private static double referenceJumpTime(int actionPoints, double radius, double theta) {
		double v0 = referenceJumpV0(actionPoints, radius);
		double t = referenceJumpDistance(actionPoints, radius, theta) / (v0 * cos(theta));
		return t;
	}

	private static double referenceJumpV0(long actionPoints, double radius) {
		double force = (5 * actionPoints) + (referenceWormMass(radius) * GAME_STANDARD_ACCELERATION);
		double v0 = 0.5 * force / referenceWormMass(radius);
		return v0;
	}

	private static double getDistance(double[] p1, double[] p2) {
		double dx = p1[0]-p2[0];
		double dy = p1[1]-p2[1];
		return Math.sqrt(dx*dx + dy*dy);
	}
	

	/**************
	 * WORLD TESTS
	 *************/

	@Test
	public void createWorld_LegalCase() {
		max_score += 10;
		boolean[][] passableMap = new boolean[][] { { true, false, true, true, false },
				{ false, false, true, true, true }, { true, true, true, true, false },
				{ false, false, false, false, false } };
		World theWorld = facade.createWorld(5.0, 4.0, passableMap);
		assertEquals(5.0, facade.getWorldWidth(theWorld), EPS);
		assertEquals(4.0, facade.getWorldHeight(theWorld), EPS);
		assertFalse(facade.hasActiveGame(theWorld));
		score += 1;
		assertTrue(facade.isPassable(theWorld, new double[] { 0.5, 3.5 }));
		assertFalse(facade.isPassable(theWorld, new double[] { 4.5, 3.5 }));
		assertTrue(facade.isPassable(theWorld, new double[] { 2.5, 2.5 }));
		assertTrue(facade.isPassable(theWorld, new double[] { 0.5, 3.5 }));
		assertFalse(facade.isPassable(theWorld, new double[] { 0.5, 0.5 }));
		assertFalse(facade.isPassable(theWorld, new double[] { 3.5, 0.5 }));
		score += 7;
		assertEquals(0, facade.getAllItems(theWorld).size());
		score += 1;
		try {
			assertEquals(0, facade.getAllTeams(theWorld).size());
			score += 1;
		} catch (MustNotImplementException exc) {
			max_score -= 1;
		}
	}

	@Test
	public void createWorld_IllegalDimension() {
		max_score += 1;
		boolean[][] passableMap = new boolean[][] { { true, false, true, true, false },
				{ false, false, true, true, true }, { true, true, true, true, false },
				{ false, false, false, false, false } };
		try {
			facade.createWorld(-1.0, 4.0, passableMap);
			fail();
		} catch (ModelException exc) {
		}
		try {
			facade.createWorld(10.0, Double.NaN, passableMap);
			fail();
		} catch (ModelException exc) {
		}
		score += 1;
	}

	@Test
	public void createWorld_IllegalMap() {
		max_score += 5;
		// No effective map.
		try {
			facade.createWorld(10.0, 4.0, null);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
		// Map with no cells.
		try {
			facade.createWorld(10.0, 4.0, new boolean[0][0]);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
		// Non-rectangular map
		try {
			boolean[][] passableMap = new boolean[][] { { true, false, true, true }, { false, false, true, true, true },
					{ true, true, true, true, false }, { false, false, false } };
			facade.createWorld(10.0, Double.NaN, passableMap);
			fail();
		} catch (ModelException exc) {
			score += 3;
		}
	}

	@Test
	public void terminateWorld_EmptyWorld() {
		max_score += 1;
		facade.terminate(theWorld);
		assertEquals(0, facade.getAllItems(theWorld).size());
		try {
			assertEquals(0, facade.getAllTeams(theWorld).size());
		} catch (MustNotImplementException exc) {
			max_score -= 1;
		}
		score += 1;
	}

	@Test
	public void terminateWorld_WorldWithGameObjects() {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.5, 8.0 }, FIXTURE_DIRECTION, 0.95, "Addy", null);
		Worm worm2 = facade.createWorm(theWorld, new double[] { 6.0, 8.0 }, FIXTURE_DIRECTION, 0.95, "Addy", null);
		facade.terminate(theWorld);
		assertEquals(0, facade.getAllItems(theWorld).size());
		assertNull(facade.getWorld(worm1));
		assertNull(facade.getWorld(worm2));
		score += 4;
	}

	@Test
	public void terminateWorld_WorldWithTeams() {
		max_score += 2;
		try {
			facade.createTeam(theWorld, "TeamA");
			facade.createTeam(theWorld, "TeamB");
			facade.terminate(theWorld);
			assertEquals(0, facade.getAllTeams(theWorld).size());
			score += 2;
		} catch (MustNotImplementException exc) {
			max_score -= 2;
		}
	}

	@Test
	public void isPassablePoint_TrueCases() {
		max_score += 2;
		assertTrue(facade.isPassable(theWorld, new double[] { 4.0, 6.0 }));
		assertTrue(facade.isPassable(theWorld, new double[] { 0.0, 8.99 }));
		assertTrue(facade.isPassable(theWorld, new double[] { 8.99, 0.0 }));
		assertTrue(facade.isPassable(theWorld, new double[] { 8.99, 8.99 }));
		score += 2;
	}

	@Test
	public void isPassablePoint_FalseCases() {
		max_score += 2;
		assertFalse(facade.isPassable(theWorld, new double[] { 9.3, 9.7 }));
		assertFalse(facade.isPassable(theWorld, new double[] { 9.01, 0.0 }));
		assertFalse(facade.isPassable(theWorld, new double[] { 9.01, 0.0 }));
		assertFalse(facade.isPassable(theWorld, new double[] { 9.01, 9.01 }));			
		score += 2;
	}

	@Test
	public void isPassablePoint_OutsideWorld() {
		max_score += 2;
		assertTrue(facade.isPassable(theWorld, new double[] { 10.0, 12.0 }));
		assertTrue(facade.isPassable(theWorld, new double[] { Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY }));
		score += 2;
	}

	@Test
	public void isPassableArea_TrueCases() {
		max_score += 4;
		map10x10 = new boolean[][] { { false, false, false, false, false, false, false, false, false, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, false, false, false, false, false, false, false, false, false }, };
		theWorld = facade.createWorld(10.0, 10.0, map10x10);
		assertTrue(facade.isPassable(theWorld, new double[] { 3.0, 7.0 }, 1.95));
		assertTrue(facade.isPassable(theWorld, new double[] { 3.0, 3.0 }, 1.95));
		assertTrue(facade.isPassable(theWorld, new double[] { 7.0, 7.0 }, 1.95));
		assertTrue(facade.isPassable(theWorld, new double[] { 7.0, 3.0 }, 1.95));
		score += 4;
	}

	@Test
	public void isPassableArea_FalseCases() {
		max_score += 4;
		map10x10 = new boolean[][] { { false, false, false, false, false, false, false, false, false, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, false, false, false, false, false, false, false, false, false }, };
		theWorld = facade.createWorld(10.0, 10.0, map10x10);
		//False cases completely in world
		assertFalse(facade.isPassable(theWorld, new double[] { 3.0, 7.3 }, 1.95));
		assertFalse(facade.isPassable(theWorld, new double[] { 3.0, 2.9 }, 1.95));
		assertFalse(facade.isPassable(theWorld, new double[] { 7.1, 7.0 }, 1.95));
		assertFalse(facade.isPassable(theWorld, new double[] { 7.0, 2.9 }, 1.95));
		score += 4;
	}

	@Test
	public void isPassableArea_AreasNotCompleteltInWorld() {
		max_score += 4;
		map10x10 = new boolean[][] { { false, false, false, false, false, false, false, false, false, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, false, false, false, false, false, false, false, false, false }, };
		theWorld = facade.createWorld(10.0, 10.0, map10x10);
		assertFalse(facade.isPassable(theWorld, new double[] { 3.0, 7.3 }, 3.95));
		assertTrue(facade.isPassable(theWorld, new double[] { 20.0, -10.0 }, 1.95));
		score += 4;
	}

	@Test
	public void isAdjacent_TrueCases() {
		max_score += 6;
		map10x10 = new boolean[][] { { false, false, false, false, false, false, false, false, false, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, false, false, false, false, false, false, false, false, false }, };
		theWorld = facade.createWorld(10.0, 10.0, map10x10);
		assertTrue(facade.isAdjacent(theWorld, new double[] { 3.00, 7.95 }, 1.00));
		assertTrue(facade.isAdjacent(theWorld, new double[] { 2.05, 3.00 }, 1.00));
		assertTrue(facade.isAdjacent(theWorld, new double[] { 7.00, 2.05 }, 1.00));
		assertTrue(facade.isAdjacent(theWorld, new double[] { 3.00, 7.95 }, 1.00));
		score += 6;
	}

	@Test
	public void isAdjacent_FalseCases() {
		max_score += 8;
		map10x10 = new boolean[][] { { false, false, false, false, false, false, false, false, false, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, true, true, true, true, true, true, true, true, false },
				{ false, false, false, false, false, false, false, false, false, false }, };
		theWorld = facade.createWorld(10.0, 10.0, map10x10);
		assertFalse(facade.isAdjacent(theWorld, new double[] { 3.00, 7.85 }, 1.00));
		assertFalse(facade.isAdjacent(theWorld, new double[] { 3.00, 8.05 }, 1.00));
		assertFalse(facade.isAdjacent(theWorld, new double[] { 2.15, 3.00 }, 1.00));
		assertFalse(facade.isAdjacent(theWorld, new double[] { 1.95, 3.00 }, 1.00));
		assertFalse(facade.isAdjacent(theWorld, new double[] { 7.00, 2.15 }, 1.00));
		assertFalse(facade.isAdjacent(theWorld, new double[] { 7.00, 1.95 }, 1.00));
		assertFalse(facade.isAdjacent(theWorld, new double[] { 3.00, 7.85 }, 1.00));
		assertFalse(facade.isAdjacent(theWorld, new double[] { 3.00, 8.05 }, 1.00));
		score += 8;
	}

	@Test
	public void addWorm_LegalCase() {
		max_score += 5;
		Worm wormToAdd = facade.createWorm(null, new double[] { 3.0, 7.0 }, FIXTURE_DIRECTION, 1.95, "Addy", null);
		facade.addWorm(theWorld, wormToAdd);
		assertEquals(theWorld, facade.getWorld(wormToAdd));
		assertTrue(facade.hasAsWorm(theWorld, wormToAdd));
		score += 5;
	}

	@Test
	public void addWorm_TerminatedWorld() {
		max_score += 1;
		Worm wormToAdd = facade.createWorm(null, new double[] { 3.0, 7.0 }, FIXTURE_DIRECTION, 1.95, "Addy", null);
		facade.terminate(theWorld);
		try {
			facade.addWorm(theWorld, wormToAdd);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void addWorm_WorldWithActiveGame() {
		max_score += 1;
		Worm wormToAdd = facade.createWorm(null, new double[] { 3.0, 7.0 }, FIXTURE_DIRECTION, 1.95, "Addy", null);
		facade.startGame(theWorld);
		try {
			facade.addWorm(theWorld, wormToAdd);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void addWorm_NonEffectiveWorm() {
		max_score += 1;
		try {
			facade.addWorm(theWorld, null);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void addWorm_TerminatedWorm() {
		max_score += 1;
		Worm wormToAdd = facade.createWorm(null, new double[] { 3.0, 7.0 }, FIXTURE_DIRECTION, 1.95, "Addy", null);
		facade.terminate(wormToAdd);
		try {
			facade.addWorm(theWorld, wormToAdd);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void addWorm_WormAlreadyInWorld() {
		max_score += 1;
		Worm wormToAdd = facade.createWorm(theWorld, new double[] { 3.0, 7.0 }, FIXTURE_DIRECTION, 1.95, "Addy", null);
		try {
			facade.addWorm(theWorld, wormToAdd);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void addWorm_WormInOtherWorld() {
		max_score += 1;
		World otherWorld = facade.createWorld(10.0, 10.0, map10x10);
		Worm wormToAdd = facade.createWorm(theWorld, new double[] { 3.0, 7.0 }, FIXTURE_DIRECTION, 1.95, "Addy", null);
		try {
			facade.addWorm(otherWorld, wormToAdd);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void addWorm_NotFullyInWorld() {
		max_score += 5;
		Worm wormToAdd = facade.createWorm(null, new double[] { 1.8, 7.0 }, FIXTURE_DIRECTION, 1.95, "Addy", null);
		try {
			facade.addWorm(theWorld, wormToAdd);
			fail();
		} catch (ModelException exc) {
			score += 5;
		}
	}

	@Test
	public void addWorm_NotFullyOnPassableTerrain() {
		max_score += 10;
		Worm wormToAdd = facade.createWorm(null, new double[] { 7.5, 7.0 }, FIXTURE_DIRECTION, 1.95, "Addy", null);
		try {
			facade.addWorm(theWorld, wormToAdd);
			fail();
		} catch (ModelException exc) {
			score += 10;
		}
	}

	@Test
	public void addWorm_NotAdjacentToImpassableTerrain() {
		max_score += 3;
		Worm wormToAdd = facade.createWorm(null, new double[] { 3.0, 6.5 }, FIXTURE_DIRECTION, 1.95, "Addy", null);
		try {
			facade.addWorm(theWorld, wormToAdd);
			fail();
		} catch (ModelException exc) {
			score += 3;
		}
	}

	@Test
	public void removeWorm_LegalCase() {
		max_score += 5;
		Worm wormToRemove = facade.createWorm(theWorld, new double[] { 3.0, 7.0 }, FIXTURE_DIRECTION, 1.95, "Addy",
				null);
		facade.removeWorm(theWorld, wormToRemove);
		assertEquals(null, facade.getWorld(wormToRemove));
		assertFalse(facade.hasAsWorm(theWorld, wormToRemove));
		score += 5;
	}

	@Test
	public void removeWorm_NonEffectiveWorm() {
		max_score += 1;
		try {
			facade.removeWorm(theWorld, null);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void removeWorm_WormNotInWorld() {
		max_score += 1;
		try {
			facade.removeWorm(theWorld, fixtureWorm);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void getAllWorms_BasicCase() {
		max_score += 3;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.5, 8.0 }, FIXTURE_DIRECTION, 0.95, "Addy", null);
		Worm worm2 = facade.createWorm(theWorld, new double[] { 6.0, 8.0 }, FIXTURE_DIRECTION, 0.95, "Addy", null);
		List<Worm> allWorms = facade.getAllWorms(theWorld);
		assertEquals(2, allWorms.size());
		assertTrue(allWorms.contains(worm1));
		assertTrue(allWorms.contains(worm2));
		score += 3;
	}

	@Test
	public void getAllWorms_LeakTest() {
		max_score += 8;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.5, 8.0 }, FIXTURE_DIRECTION, 0.95, "Addy", null);
		Worm worm2 = facade.createWorm(theWorld, new double[] { 6.0, 8.0 }, FIXTURE_DIRECTION, 0.95, "Addy", null);
		List<Worm> allWorms = facade.getAllWorms(theWorld);
		allWorms.remove(worm2);
		assertTrue(facade.hasAsWorm(theWorld, worm2));
		allWorms.add(fixtureWorm);
		facade.removeWorm(theWorld, worm2);
		assertEquals(1, facade.getAllWorms(theWorld).size());
		score += 8;
	}

	@Test
	public void addFood_LegalCase() {
		max_score += 3;
		Food foodToAdd = facade.createFood(null, new double[] { 8.795, 7.0 });
		facade.addFood(theWorld, foodToAdd);
		assertEquals(theWorld, facade.getWorld(foodToAdd));
		assertTrue(facade.hasAsFood(theWorld, foodToAdd));
		score += 3;
	}

	@Test
	public void addFood_NotAdjacentToImpassableTerrain() {
		max_score += 1;
		Food foodToAdd = facade.createFood(null, new double[] { 6.3, 7.0 });
		try {
			facade.addFood(theWorld, foodToAdd);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void removeFood_LegalCase() {
		max_score += 3;
		Food foodToRemove = facade.createFood(theWorld, new double[] { 3.0, 8.79 });
		facade.removeFood(theWorld, foodToRemove);
		assertEquals(null, facade.getWorld(foodToRemove));
		assertFalse(facade.hasAsFood(theWorld, foodToRemove));
		score += 3;
	}

	@Test
	public void removeFood_FoodNotInWorld() {
		max_score += 1;
		try {
			facade.removeFood(theWorld, fixtureFood);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void getAllItems_BasicCase() {
		max_score += 3;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.5, 8.0 }, FIXTURE_DIRECTION, 0.95, "Addy", null);
		Worm worm2 = facade.createWorm(theWorld, new double[] { 6.0, 8.0 }, FIXTURE_DIRECTION, 0.95, "Addy", null);
		Food food1 = facade.createFood(theWorld, new double[] { 0.22, 8.79 });
		Food food2 = facade.createFood(theWorld, new double[] { 8.79, 8.79 });
		Collection<Object> allItems = facade.getAllItems(theWorld);
		assertEquals(4, allItems.size());
		assertTrue(allItems.contains(worm1));
		assertTrue(allItems.contains(worm2));
		assertTrue(allItems.contains(food1));
		assertTrue(allItems.contains(food2));
		score += 3;
	}

	@Test
	public void getAllItems_LeakTest() {
		max_score += 6;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.5, 8.0 }, FIXTURE_DIRECTION, 0.95, "Addy", null);
		facade.createFood(theWorld, new double[] { 0.22, 8.79 });
		Collection<Object> allItems = facade.getAllItems(theWorld);
		allItems.remove(worm1);
		assertTrue(facade.hasAsWorm(theWorld, worm1));
		allItems.add(fixtureWorm);
		assertEquals(1, facade.getAllWorms(theWorld).size());
		score += 6;
	}

	@Test
	public void getAllTeams_BasicCase() {
		max_score += 2;
		try {
			Team team1 = facade.createTeam(theWorld, "TeamA");
			Team team2 = facade.createTeam(theWorld, "TeamB");
			Set<Team> allTeams = facade.getAllTeams(theWorld);
			assertEquals(3, allTeams.size());
			assertTrue(allTeams.contains(team1));
			assertTrue(allTeams.contains(team2));
			assertTrue(allTeams.contains(theTeam));
			score += 2;
		} catch (MustNotImplementException exc) {
			max_score -= 2;
		}
	}

	@Test
	public void getAllTeams_LeakTest() {
		max_score += 6;
		try {
			Team team1 = facade.createTeam(theWorld, "TeamA");
			facade.createTeam(theWorld, "TeamB");
			Set<Team> allTeams = facade.getAllTeams(theWorld);
			allTeams.remove(team1);
			assertTrue(facade.getAllTeams(theWorld).contains(team1));
			Team team3 = facade.createTeam(null, "TeamC");
			allTeams.add(team3);
			assertEquals(3, facade.getAllTeams(theWorld).size());
			score += 6;
		} catch (MustNotImplementException exc) {
			max_score -= 6;
		}
	}

	@Test
	public void getActiveWorm_NoActiveGame() {
		max_score += 1;
		assertNull(facade.getActiveWorm(theWorld));
		score += 1;
	}

	@Test
	public void getActiveWorm_NoWormsInWorld() {
		max_score += 1;
		facade.startGame(theWorld);
		assertNull(facade.getActiveWorm(theWorld));
		score += 1;
	}

	@Test
	public void getActiveWorm_WormsInWorld() {
		max_score += 2;
		facade.createWorm(theWorld, new double[] { 7.95, 4.0 }, 0.1, 1.0, "WormA", null);
		facade.createWorm(theWorld, new double[] { 7.95, 4.0 }, 0.1, 1.0, "WormB", null);
		facade.startGame(theWorld);
		assertTrue(facade.getAllWorms(theWorld).contains(facade.getActiveWorm(theWorld)));
		score += 2;
	}

	@Test
	public void startGame_SingleWormInWorld() {
		max_score += 4;
		Worm worm = facade.createWorm(theWorld, new double[] { 7.95, 4.0 }, 0.1, 1.0, "Worm", null);
		facade.decreaseNbActionPoints(worm, 20);
		BigInteger nbHitPoints = facade.getNbHitPoints(worm);
		facade.startGame(theWorld);
		assertEquals(worm, facade.getActiveWorm(theWorld));
		assertEquals(facade.getMaxNbActionPoints(worm), facade.getNbActionPoints(worm));
		assertEquals(nbHitPoints.add(BigInteger.TEN), facade.getNbHitPoints(worm));
		score += 4;
	}

	@Test
	public void startGame_SeveralWormsInWorld() {
		max_score += 6;
		Worm wormA = facade.createWorm(theWorld, new double[] { 7.95, 4.0 }, 0.1, 1.0, "WormA", null);
		Worm wormB = facade.createWorm(theWorld, new double[] { 2.1, 6.99 }, 0.4, 2.0, "WormB", null);
		Worm wormC = facade.createWorm(theWorld, new double[] { 8.48, 8.48 }, 4.5, 0.5, "WormC", null);
		facade.decreaseNbActionPoints(wormA, 20);
		BigInteger nbHitPointsA = facade.getNbHitPoints(wormA);
		facade.decreaseNbActionPoints(wormB, 20);
		BigInteger nbHitPointsB = facade.getNbHitPoints(wormB);
		facade.decreaseNbActionPoints(wormC, 20);
		BigInteger nbHitPointsC = facade.getNbHitPoints(wormC);
		facade.startGame(theWorld);
		Worm activeWorm = facade.getActiveWorm(theWorld);
		assertTrue(facade.getAllWorms(theWorld).contains(activeWorm));
		if (activeWorm == wormA) {
			assertEquals(facade.getMaxNbActionPoints(wormA), facade.getNbActionPoints(wormA));
			assertEquals(nbHitPointsA.add(BigInteger.TEN), facade.getNbHitPoints(wormA));
		}
		if (activeWorm == wormB) {
			assertEquals(facade.getMaxNbActionPoints(wormB), facade.getNbActionPoints(wormB));
			assertEquals(nbHitPointsB.add(BigInteger.TEN), facade.getNbHitPoints(wormB));
		}
		if (activeWorm == wormC) {
			assertEquals(facade.getMaxNbActionPoints(wormC), facade.getNbActionPoints(wormC));
			assertEquals(nbHitPointsC.add(BigInteger.TEN), facade.getNbHitPoints(wormC));
		}
		score += 6;
	}

	@Test
	public void finishGame_RegularCase() {
		max_score += 1;
		facade.createWorm(theWorld, new double[] { 7.95, 4.0 }, 0.1, 1.0, "Worm", null);
		facade.startGame(theWorld);
		facade.finishGame(theWorld);
		assertNull(facade.getActiveWorm(theWorld));
		score += 1;
	}

	@Test
	public void finishGame_NoActiveGame() {
		max_score += 1;
		facade.createWorm(theWorld, new double[] { 7.95, 4.0 }, 0.1, 1.0, "Worm", null);
		facade.finishGame(theWorld);
		assertNull(facade.getActiveWorm(theWorld));
		score += 1;
	}

	@Test
	public void activateNextWorm_SeveralWorms() {
		max_score += 2;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 7.95, 4.0 }, 0.1, 1.0, "FirstWorm", null);
		Worm worm2 = facade.createWorm(theWorld, new double[] { 2.5, 7.95 }, 0.1, 1.0, "SecondWorm", null);
		Worm worm3 = facade.createWorm(theWorld, new double[] { 6.5, 7.95 }, 0.1, 1.0, "ThirdWorm", null);
		facade.startGame(theWorld);
		Set<Worm> activatedWorms = new HashSet<>();
		activatedWorms.add(facade.getActiveWorm(theWorld));
		facade.activateNextWorm(theWorld);
		activatedWorms.add(facade.getActiveWorm(theWorld));
		facade.activateNextWorm(theWorld);
		activatedWorms.add(facade.getActiveWorm(theWorld));
		assertTrue(activatedWorms.contains(worm1));
		assertTrue(activatedWorms.contains(worm2));
		assertTrue(activatedWorms.contains(worm3));
		score += 2;
	}

	@Test
	public void activateNextWorm_WormsLeavingWorld() {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 7.95, 4.0 }, 0.1, 1.0, "FirstWorm", null);
		Worm worm2 = facade.createWorm(theWorld, new double[] { 2.5, 7.95 }, 0.1, 1.0, "SecondWorm", null);
		Worm worm3 = facade.createWorm(theWorld, new double[] { 6.5, 7.95 }, 0.1, 1.0, "ThirdWorm", null);
		facade.startGame(theWorld);
		Worm firstActivatedWorm = facade.getActiveWorm(theWorld);
		if (firstActivatedWorm == worm1) {
			facade.removeWorm(theWorld, worm2);
			facade.activateNextWorm(theWorld);
			assertEquals(worm3, facade.getActiveWorm(theWorld));
		} else if (firstActivatedWorm == worm2) {
			facade.removeWorm(theWorld, worm3);
			facade.activateNextWorm(theWorld);
			assertEquals(worm1, facade.getActiveWorm(theWorld));
		} else {
			facade.removeWorm(theWorld, worm1);
			facade.activateNextWorm(theWorld);
			assertEquals(worm2, facade.getActiveWorm(theWorld));
		}
		score += 4;
	}

	@Test
	public void activateNextWorm_NoMoreActiveWorms() {
		max_score += 1;
		Worm worm = facade.createWorm(theWorld, new double[] { 7.95, 4.0 }, 0.1, 1.0, "Worm", null);
		facade.startGame(theWorld);
		facade.removeWorm(theWorld, worm);
		facade.activateNextWorm(theWorld);
		assertNull(facade.getActiveWorm(theWorld));
		score += 1;
	}

	@Test
	public void getWinner_NoActiveGame() {
		max_score += 1;
		facade.createWorm(theWorld, new double[] { 7.95, 4.0 }, 0.1, 1.0, "Worm", null);
		assertNull(facade.getWinner(theWorld));
		score += 1;
	}

	@Test
	public void getWinner_WinningWorm() {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 7.95, 4.0 }, 0.1, 1.0, "FirstWorm", null);
		Worm worm2 = facade.createWorm(theWorld, new double[] { 2.5, 7.95 }, 0.1, 1.0, "SecondWorm", null);
		Worm worm3 = facade.createWorm(theWorld, new double[] { 6.5, 7.95 }, 0.1, 1.0, "ThirdWorm", null);
		facade.startGame(theWorld);
		facade.removeWorm(theWorld, worm3);
		facade.activateNextWorm(theWorld);
		facade.removeWorm(theWorld, worm2);
		facade.activateNextWorm(theWorld);
		facade.activateNextWorm(theWorld);
		assertEquals(facade.getName(worm1), facade.getWinner(theWorld));
		score += 4;
	}

	@Test
	public void getWinner_WinningTeam() {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 7.95, 4.0 }, 0.1, 1.0, "FirstWorm", null);
		Worm worm2 = facade.createWorm(theWorld, new double[] { 2.5, 7.95 }, 0.1, 1.0, "SecondWorm", null);
		Worm worm3 = facade.createWorm(theWorld, new double[] { 6.5, 7.95 }, 0.1, 1.0, "ThirdWorm", null);
		Team someTeam = facade.createTeam(theWorld, "SomeTeam");
		facade.addWormsToTeam(someTeam, worm1, worm3);
		facade.startGame(theWorld);
		facade.activateNextWorm(theWorld);
		facade.removeWorm(theWorld, worm2);
		facade.activateNextWorm(theWorld);
		facade.activateNextWorm(theWorld);
		assertEquals(facade.getName(someTeam), facade.getWinner(theWorld));
		score += 4;
	}

	@Test
	public void getWinner_WorldWithoutWorms() {
		max_score += 1;
		facade.startGame(theWorld);
		assertNull(facade.getWinner(theWorld));
		score += 1;
	}

	@Test
	public void getWinner_SeveralWormsNotInTeam() {
		max_score += 1;
		facade.createWorm(theWorld, new double[] { 7.95, 4.0 }, 0.1, 1.0, "FirstWorm", null);
		facade.createWorm(theWorld, new double[] { 2.5, 7.95 }, 0.1, 1.0, "SecondWorm", null);
		facade.startGame(theWorld);
		assertNull(facade.getWinner(theWorld));
		score += 1;
	}

	@Test
	public void getWinner_SeveralWormsInDifferentTeams() {
		max_score += 1;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 7.95, 4.0 }, 0.1, 1.0, "FirstWorm", null);
		Worm worm2 = facade.createWorm(theWorld, new double[] { 2.5, 7.95 }, 0.1, 1.0, "SecondWorm", null);
		Team teamA = facade.createTeam(theWorld, "TeamA");
		facade.addWormsToTeam(teamA, worm1);
		Team teamB = facade.createTeam(theWorld, "TeamB");
		facade.addWormsToTeam(teamB, worm2);
		facade.startGame(theWorld);
		assertNull(facade.getWinner(theWorld));
		score += 1;
	}

	@Test
	public void getWinner_IndividualWormTeamWorm() {
		max_score += 1;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 7.95, 4.0 }, 0.1, 1.0, "FirstWorm", null);
		facade.createWorm(theWorld, new double[] { 2.5, 7.95 }, 0.1, 1.0, "SecondWorm", null);
		Team teamA = facade.createTeam(theWorld, "TeamA");
		facade.addWormsToTeam(teamA, worm1);
		facade.startGame(theWorld);
		assertNull(facade.getWinner(theWorld));
		score += 1;
	}

	/**************
	 * WORM TESTS
	 *************/

	@Test
	public void createWorm_LegalCaseNoWorldNoTeam() {
		max_score += 12;
		double[] location = new double[] { 3.0, -7.0 };
		double direction = 3 * PI / 7;
		double radius = 0.30;
		String name = "Test";
		Worm newWorm = facade.createWorm(null, location, direction, radius, name, null);
		assertNull(facade.getWorld(newWorm));
		assertNull(facade.getTeam(newWorm));
		assertEquals(location[0], facade.getLocation(newWorm)[0], EPS);
		assertEquals(location[1], facade.getLocation(newWorm)[1], EPS);
		score += 2;
		assertEquals(direction, facade.getOrientation(newWorm), EPS);
		score += 2;
		assertEquals(radius, facade.getRadius(newWorm), EPS);
		score += 2;
		assertEquals(Math.round(facade.getMass(newWorm)), facade.getNbActionPoints(newWorm));
		assertEquals(Math.round(facade.getMass(newWorm)), facade.getMaxNbActionPoints(newWorm));
		score += 2;
		assertEquals(name, facade.getName(newWorm));
		score += 2;
		assertTrue(facade.getNbHitPoints(newWorm).compareTo(BigInteger.valueOf(1000)) >= 0);
		assertTrue(facade.getNbHitPoints(newWorm).compareTo(BigInteger.valueOf(2000)) <= 0);
		score += 2;
	}

	@Test
	public void createWorm_LegalCaseInWorld() {
		max_score += 6;
		double[] location = new double[] { 8.68, 8.68 };
		double direction = 3 * PI / 7;
		double radius = 0.30;
		String name = "Test";
		Worm newWorm = facade.createWorm(theWorld, location, direction, radius, name, null);
		assertEquals(theWorld, facade.getWorld(newWorm));
		assertTrue(facade.hasAsWorm(theWorld, newWorm));
		score += 6;
	}

	@Test
	public void createWorm_LegalCaseInTeam() {
		max_score += 6;
		double[] location = new double[] { 8.68, 8.68 };
		double direction = 3 * PI / 7;
		double radius = 0.30;
		String name = "Test";
		try {
			Worm newWorm = facade.createWorm(theWorld, location, direction, radius, name, theTeam);
			assertEquals(theTeam, facade.getTeam(newWorm));
			assertEquals(1, facade.getAllWormsOfTeam(theTeam).size());
			assertTrue(facade.getAllWormsOfTeam(theTeam).contains(newWorm));
			score += 6;
		} catch (MustNotImplementException exc) {
			max_score -= 6;
		}
	}

	@Test
	public void createWorm_IllegalWorld() {
		max_score += 2;
		World someWorld = facade.createWorld(10.0, 20.0, map10x10);
		facade.terminate(someWorld);
		try {
			facade.createWorm(someWorld, new double[] { 8.68, 8.68 }, 0.3, 0.3, "Worm", null);
			fail();
		} catch (ModelException exc) {
			score += 2;
		}
	}

	@Test
	public void createWorm_InfinitePosition() {
		max_score += 3;
		double[] location = new double[] { Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY };
		double direction = 3 * PI / 7;
		double radius = 0.30;
		String name = "Test";
		Worm newWorm = facade.createWorm(null, location, direction, radius, name, null);
		assertEquals(Double.POSITIVE_INFINITY, facade.getLocation(newWorm)[0], EPS);
		assertEquals(Double.NEGATIVE_INFINITY, facade.getLocation(newWorm)[1], EPS);
		score += 3;
	}

	@Test
	public void createWorm_IllegalLocation() {
		max_score += 2;
		double direction = 3 * PI / 7;
		double radius = 0.30;
		String name = "Test";
		// Illegal x-displacement
		try {
			double[] location = new double[] { Double.NaN, 1.0 };
			facade.createWorm(null, location, direction, radius, name, null);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
		// Illegal y-displacement
		try {
			double[] location = new double[] { 1.0, Double.NaN };
			facade.createWorm(null, location, direction, radius, name, null);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void createWorm_IllegalOrientation() {
		max_score += 2;
		double[] location = new double[] { 3.0, -7.0 };
		double radius = 0.30;
		String name = "Test";
		// Negative orientation
		try {
			double direction = -1.0;
			facade.createWorm(null, location, direction, radius, name, null);
			fail();
		} catch (ModelException exc) {
		}
		// Orientation too large
		try {
			double direction = 7.0;
			facade.createWorm(null, location, direction, radius, name, null);
			fail();
		} catch (ModelException exc) {
		}
		// Infinite Orientation
		try {
			double direction = Double.POSITIVE_INFINITY;
			facade.createWorm(null, location, direction, radius, name, null);
			fail();
		} catch (ModelException exc) {
		}
		// Orientation not a number
		try {
			double direction = Double.NaN;
			facade.createWorm(null, location, direction, radius, name, null);
			fail();
		} catch (ModelException exc) {
		}
		score += 2;
	}

	@Test
	public void createWorm_IllegalRadius() {
		max_score += 2;
		double[] location = new double[] { 3.0, -7.0 };
		double direction = 3 * PI / 7;
		String name = "Test";
		// Radius below 0.25
		try {
			double radius = 0.2;
			facade.createWorm(null, location, direction, radius, name, null);
			fail();
		} catch (ModelException exc) {
		}
		// Negative radius
		try {
			double radius = -4.0;
			facade.createWorm(null, location, direction, radius, name, null);
			fail();
		} catch (ModelException exc) {
		}
		// Infinite Radius
		try {
			double radius = Double.POSITIVE_INFINITY;
			facade.createWorm(null, location, direction, radius, name, null);
			fail();
		} catch (ModelException exc) {
		}
		// Radius not a number
		try {
			double radius = Double.NaN;
			facade.createWorm(null, location, direction, radius, name, null);
			fail();
		} catch (ModelException exc) {
		}
		score += 2;
	}

	@Test
	public void createWorm_IllegalTeam() {
		max_score += 2;
		double[] location = new double[] { 3.0, -7.0 };
		double direction = 3 * PI / 7;
		String name = "Test";
		// Radius below 0.25
		try {
			double radius = 0.2;
			facade.createWorm(null, location, direction, radius, name, theTeam);
			fail();
		} catch (ModelException exc) {
			score += 2;
		} catch (MustNotImplementException exc) {
			max_score -= 2;
		}

	}

	@Test
	public void terminateWorm_NoWorldNoTeam() {
		max_score += 1;
		Worm someWorm = facade.createWorm(null, new double[] { -4.0, 7.0 }, 0.8, 1.0, "Worm", null);
		facade.terminate(someWorm);
		assertTrue(facade.isTerminated(someWorm));
		score += 1;
	}

	@Test
	public void terminateWorm_InWorldNoTeam() {
		max_score += 1;
		facade.terminate(fixtureWorm);
		assertTrue(facade.isTerminated(fixtureWorm));
		assertNull(facade.getWorld(fixtureWorm));
		assertFalse(facade.hasAsWorm(theWorld, fixtureWorm));
		score += 1;
	}

	@Test
	public void terminateWorm_InWorldInTeam() {
		max_score += 1;
		try {
			facade.addWormsToTeam(theTeam, fixtureWorm);
			facade.terminate(fixtureWorm);
			assertTrue(facade.isTerminated(fixtureWorm));
			assertNull(facade.getTeam(fixtureWorm));
			assertTrue(facade.getAllWormsOfTeam(theTeam).isEmpty());
			score += 1;
		} catch (MustNotImplementException exc) {
			max_score = -1;
		}
	}

	@Test
	public void setRadius_IllegalValues() {
		max_score += 1;
		try {
			facade.setRadius(fixtureWorm, 0.2);
			fail();
		} catch (ModelException exc) {
		}
		try {
			facade.setRadius(fixtureWorm, -0.5);
			fail();
		} catch (ModelException exc) {
		}
		try {
			facade.setRadius(fixtureWorm, Double.NaN);
			fail();
		} catch (ModelException exc) {
		}
		score += 1;
	}

	@Test
	public void setRadius_LargerRadiusUpdatesMass() {
		max_score += 2;
		double newRadius = 2.0;
		facade.setRadius(fixtureWorm, newRadius);
		assertEquals(newRadius, facade.getRadius(fixtureWorm), EPS);
		assertEquals(referenceWormMass(newRadius), facade.getMass(fixtureWorm), EPS);
		score += 2;
	}

	@Test
	public void setRadius_LargerRadiusUpdatesMaxActionPoints() {
		max_score += 2;
		double newRadius = 2.0;
		facade.setRadius(fixtureWorm, newRadius);
		assertEquals(newRadius, facade.getRadius(fixtureWorm), EPS);
		assertEquals(referenceMaxActionPoints(newRadius), facade.getMaxNbActionPoints(fixtureWorm));
		score += 2;
	}

	@Test
	public void setRadius_ShrinkingRadiusReducesActionPoints() {
		max_score += 5;
		Worm worm = fixtureWorm;
		Assume.assumeTrue(referenceMaxActionPoints(FIXTURE_RADIUS) == facade.getNbActionPoints(worm));
		try {
			facade.setRadius(worm, FIXTURE_RADIUS / 2);
			assertEquals(FIXTURE_RADIUS / 2, facade.getRadius(worm), EPS);
			assertEquals(referenceMaxActionPoints(FIXTURE_RADIUS / 2), facade.getMaxNbActionPoints(worm));
			assertEquals(facade.getMaxNbActionPoints(worm), facade.getNbActionPoints(worm));
		} catch (ModelException e) {
			// interpretation 2: new radius is invalid
			// ensure that nothing has been modified
			assertEquals(FIXTURE_RADIUS, facade.getRadius(worm), EPS);
			assertEquals(referenceMaxActionPoints(FIXTURE_RADIUS), facade.getMaxNbActionPoints(worm));
			assertEquals(facade.getMaxNbActionPoints(worm), facade.getNbActionPoints(worm));
		}
		score += 5;
	}

	@Test
	public void setRadius_LargerRadiusOnImpasableTerrain() {
		max_score += 5;
		double[] location = new double[] { 1.0, 8.68 };
		double direction = 3 * PI / 7;
		double radius = 0.30;
		String name = "Test";
		Worm newWorm = facade.createWorm(theWorld, location, direction, radius, name, null);
		try {
			facade.setRadius(newWorm, 2.0);
			fail();
		} catch (ModelException exc) {
			score += 5;
		}
	}

	@Test
	public void getMass_SingleCase() {
		max_score += 2;
		double expectedMass = 1062.0 * (4.0 / 3.0 * PI * pow(FIXTURE_RADIUS, 3));
		assertEquals(expectedMass, facade.getMass(fixtureWorm), EPS);
		score += 2;
	}

	@Test
	public void decreaseActionPoints_LegalCase() {
		max_score += 1;
		long oldNbActionPoints = facade.getNbActionPoints(fixtureWorm);
		facade.decreaseNbActionPoints(fixtureWorm, 3);
		assertEquals(oldNbActionPoints - 3, facade.getNbActionPoints(fixtureWorm));
		score += 1;
	}

	@Test
	public void decreaseActionPoints_NegativeDelta() {
		max_score += 1;
		facade.decreaseNbActionPoints(fixtureWorm, 5);
		long oldNbActionPoints = facade.getNbActionPoints(fixtureWorm);
		facade.decreaseNbActionPoints(fixtureWorm, -3);
		assertEquals(oldNbActionPoints + 3, facade.getNbActionPoints(fixtureWorm));
		score += 1;
	}

	@Test
	public void decreaseActionPoints_DeltaTooLarge() {
		max_score += 1;
		long oldNbActionPoints = facade.getNbActionPoints(fixtureWorm);
		facade.decreaseNbActionPoints(fixtureWorm, Integer.MAX_VALUE);
		assertEquals(oldNbActionPoints, facade.getNbActionPoints(fixtureWorm));
		score += 1;
	}

	@Test
	public void decreaseActionPoints_DeltaTooNegative() {
		max_score += 1;
		long oldNbActionPoints = facade.getNbActionPoints(fixtureWorm);
		long maxNbActionPoints = facade.getMaxNbActionPoints(fixtureWorm);
		long delta = oldNbActionPoints - maxNbActionPoints - 10;
		facade.decreaseNbActionPoints(fixtureWorm, delta);
		assertEquals(oldNbActionPoints, facade.getNbActionPoints(fixtureWorm));
		score += 1;
	}

	@Test
	public void incrementHitPoints_LegalCase() {
		max_score += 2;
		BigInteger oldNbHitPoints = facade.getNbHitPoints(fixtureWorm);
		facade.incrementNbHitPoints(fixtureWorm, 3);
		assertEquals(oldNbHitPoints.add(BigInteger.valueOf(3)), facade.getNbHitPoints(fixtureWorm));
		score += 1;
		oldNbHitPoints = facade.getNbHitPoints(fixtureWorm);
		facade.incrementNbHitPoints(fixtureWorm, -100);
		assertEquals(oldNbHitPoints.add(BigInteger.valueOf(-100)), facade.getNbHitPoints(fixtureWorm));
		score += 1;
	}

	@Test
	public void incrementHitPoints_ZeroCase() {
		max_score += 2;
		BigInteger oldNbHitPoints = facade.getNbHitPoints(fixtureWorm);
		facade.incrementNbHitPoints(fixtureWorm, -oldNbHitPoints.longValue());
		assertEquals(BigInteger.ZERO, facade.getNbHitPoints(fixtureWorm));
		assertTrue(facade.isTerminated(fixtureWorm));
		score += 2;
	}

	@Test
	public void incrementHitPoints_NegativeCase() {
		max_score += 2;
		BigInteger oldNbHitPoints = facade.getNbHitPoints(fixtureWorm);
		facade.incrementNbHitPoints(fixtureWorm, -2 * oldNbHitPoints.longValue());
		assertEquals(BigInteger.ZERO, facade.getNbHitPoints(fixtureWorm));
		assertTrue(facade.isTerminated(fixtureWorm));
		score += 2;
	}

	@Test
	public void rename_LegalCases() {
		max_score += 3;
		Worm worm = fixtureWorm;
		facade.rename(worm, "John");
		assertEquals("John", facade.getName(worm));
		facade.rename(worm, "James o'Hara");
		assertEquals("James o'Hara", facade.getName(worm));
		facade.rename(worm, "J ");
		assertEquals("J ", facade.getName(worm));
		facade.rename(worm, "J\"");
		assertEquals("J\"", facade.getName(worm));
		facade.rename(worm, "J_a_");
		assertEquals("J_a_", facade.getName(worm));
		score += 3;
	}

	@Test
	public void rename_NameTooShort() {
		max_score += 1;
		try {
			facade.rename(fixtureWorm, "J");
			fail();
		} catch (ModelException exc) {
		}
		try {
			facade.rename(fixtureWorm, "");
			fail();
		} catch (ModelException exc) {
		}
		score += 1;
	}

	@Test
	public void rename_NameWithIllegalCharacters() {
		max_score += 1;
		try {
			facade.rename(fixtureWorm, "John123");
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void rename_NameWithoutStartingCapital() {
		max_score += 1;
		try {
			facade.rename(fixtureWorm, "john");
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void rename_NullName() {
		max_score += 1;
		try {
			facade.rename(fixtureWorm, null);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void turn_HalfTurn() {
		max_score += 1;
		facade.turn(fixtureWorm, PI);
		assertEquals(PI + FIXTURE_DIRECTION, facade.getOrientation(fixtureWorm), EPS);
		assertEquals(referenceCostToTurn(PI),
				facade.getMaxNbActionPoints(fixtureWorm) - facade.getNbActionPoints(fixtureWorm));
		score += 1;
	}

	@Test
	public void turn_QuarterTurnNegative() {
		max_score += 2;
		facade.turn(fixtureWorm, PI);
		facade.turn(fixtureWorm, -PI / 2);
		assertEquals(PI + FIXTURE_DIRECTION - PI / 2, facade.getOrientation(fixtureWorm), EPS);
		assertEquals(referenceCostToTurn(PI + PI / 2),
				facade.getMaxNbActionPoints(fixtureWorm) - facade.getNbActionPoints(fixtureWorm));
		score += 2;
	}

	@Test
	public void turn_IllegalAngles() {
		max_score += 3;
		try {
			facade.turn(fixtureWorm, -7);
			fail();
		} catch (ModelException exc) {
		}
		try {
			facade.turn(fixtureWorm, +7);
			fail();
		} catch (ModelException exc) {
		}
		try {
			facade.turn(fixtureWorm, Double.NaN);
			fail();
		} catch (ModelException exc) {
		}
		score += 3;
	}

	@Test
	public void getFurthestLocationInDirection_WormNotInWorld() {
		max_score += 5;
		Worm theWorm = facade.createWorm(null, new double[] { 8.0, 4.0 }, 3 * PI / 4.0, 1.0, "Worm", null);
		double[] destination = facade.getFurthestLocationInDirection(theWorm, 3 * PI / 4, 2.0);
		assertEquals(8.0 - Math.sqrt(2.0), destination[0], EPS);
		assertEquals(4.0 + Math.sqrt(2.0), destination[1], EPS);
		score += 5;
	}

	@Test
	public void getFurthestLocationInDirection_NoMovePossible() {
		max_score += 3;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 8.0, 4.0 }, 0.0, 1.0, "Worm", null);
		double[] destination = facade.getFurthestLocationInDirection(theWorm, 0.3, 5.0);
		assertEquals(facade.getLocation(theWorm)[0], destination[0], EPS);
		assertEquals(facade.getLocation(theWorm)[1], destination[1], EPS);
		score += 3;
	}

	@Test
	public void getFurthestLocationInDirection_MaximalMoveWithinWorld() {
		max_score += 5;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 8.0, 4.0 }, 3 * PI / 4.0, 1.0, "Worm", null);
		double[] destination = facade.getFurthestLocationInDirection(theWorm, 3 * PI / 4, 2.0);
		assertEquals(8.0 - Math.sqrt(2.0), destination[0], EPS);
		assertEquals(4.0 + Math.sqrt(2.0), destination[1], EPS);
		score += 5;
	}

	@Test
	public void getFurthestLocationInDirection_MaximalMoveOutsideWorld() {
		max_score += 5;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 8.0, 1.0 }, 5 * PI / 6.0, 1.0, "Worm", null);
		double[] destination = facade.getFurthestLocationInDirection(theWorm, 5 * PI / 6, 20.0);
		assertEquals(8.0 + 20 * Math.cos(5 * PI / 6), destination[0], EPS);
		assertEquals(1.0 + 20 * Math.sin(5 * PI / 6), destination[1], EPS);
		score += 5;
	}

	@Test
	public void getFurthestLocationInDirection_NonMaximalMove() {
		max_score += 8;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 8.0, 3.0 }, 3 * PI / 4.0, 1.0, "Worm", null);
		double[] destination = facade.getFurthestLocationInDirection(theWorm, 3 * PI / 4, 10.0);
		assertEquals(8.0 - 5.0, destination[0], EPS);
		assertEquals(3.0 + 5.0, destination[1], EPS);
		score += 8;
	}

	@Test
	public void getFurthestLocationInDirection_IllegalCases() {
		max_score += 6;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 8.0, 4.0 }, PI, 1.0, "Worm", null);
		// Negative orientation
		try {
			facade.getFurthestLocationInDirection(theWorm, -0.5, 3.0);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
		// Orientation not below 2*PI
		try {
			facade.getFurthestLocationInDirection(theWorm, 3 * PI, 3.0);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
		// Orientation not a number
		try {
			facade.getFurthestLocationInDirection(theWorm, Double.NaN, 3.0);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
		// Negative maximum distance
		try {
			facade.getFurthestLocationInDirection(theWorm, 0.5, -3.0);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
		// Infinite maximum distance
		try {
			facade.getFurthestLocationInDirection(theWorm, 0.5, Double.POSITIVE_INFINITY);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
		// Maximum distance not a number
		try {
			facade.getFurthestLocationInDirection(theWorm, 0.5, Double.NaN);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void move_ReachingImpassableTerrainInDirection() {
		max_score += 15;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 8.0, 7.5 }, 3 * PI / 4.0, 1.0, "Worm", null);
		long oldNbActionPoints = facade.getNbActionPoints(theWorm);
		facade.move(theWorm);
		double[] newLocation = facade.getLocation(theWorm);
		double[] expectedLocation = new double[] {7.5,8.0};
		assertTrue(facade.isAdjacent(theWorld, newLocation, facade.getRadius(theWorm)));
		assertEquals(expectedLocation[0]+facade.getRadius(theWorm)*0.1/2.0, newLocation[0], facade.getRadius(theWorm)*0.12/2.0);
		assertEquals(expectedLocation[1]-facade.getRadius(theWorm)*0.1/2.0, newLocation[1], facade.getRadius(theWorm)*0.12/2.0);
		// We allow a deviation of +/-1 in calculating the action points.
		assertTrue((facade.getNbActionPoints(theWorm) >= oldNbActionPoints - 4)
				&& (facade.getNbActionPoints(theWorm) <= oldNbActionPoints - 2));
		score += 15;
	}

	@Test
	public void move_OnlyPassableTerrainInDirection() {
		max_score += 15;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 7.0, 3.0 }, PI, 2.0, "Worm", null);
		long oldNbActionPoints = facade.getNbActionPoints(theWorm);
		facade.move(theWorm);
		double[] newLocation = facade.getLocation(theWorm);
		assertEquals(7.0 - 2.0, newLocation[0], EPS);
		assertEquals(3.0, newLocation[1], EPS);
		// We allow a deviation of +/-1 in calculating the action points.
		assertTrue((facade.getNbActionPoints(theWorm) >= oldNbActionPoints - 3)
				&& (facade.getNbActionPoints(theWorm) <= oldNbActionPoints - 1));
		score += 15;
	}

	@Test
	public void move_ReachingImpassableTerrainDeviatingDirectionAlmostVertical() {
		max_score += 15;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 8.0, 5.0 }, 3 * PI / 4.0, 1.0, "Worm", null);
		long oldNbActionPoints = facade.getNbActionPoints(theWorm);
		facade.move(theWorm);
		double[] newLocation = facade.getLocation(theWorm);
		// The worm will move in an almost vertical direction, such that he is still
		// adjacent to the vertical wall.
		double[] expectedLocation = new double[] {8.0,6.0};
		assertTrue(facade.isAdjacent(theWorld, newLocation, facade.getRadius(theWorm)));
		assertEquals(expectedLocation[0]-facade.getRadius(theWorm)*0.1/2.0, newLocation[0], facade.getRadius(theWorm)*0.12/2.0);
		assertEquals(expectedLocation[1]-facade.getRadius(theWorm)*0.1/2.0, newLocation[1], facade.getRadius(theWorm)*0.12/2.0);
		assertTrue((facade.getNbActionPoints(theWorm) >= oldNbActionPoints - 5)
				&& (facade.getNbActionPoints(theWorm) <= oldNbActionPoints - 3));
		score += 15;
	}

	@Test
	public void move_ReachingImpassableTerrainDeviatingDirectionDiagonalDirection() {
		max_score += 15;
		map10x10 = new boolean[][] { { true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, false, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false }, };
		World otherWorld = facade.createWorld(10.0, 10.0, map10x10);
		Worm theWorm = facade.createWorm(otherWorld, new double[] { 7.0, 5.0 }, PI, 2.0, "Worm", null);
		long oldNbActionPoints = facade.getNbActionPoints(theWorm);
		facade.move(theWorm);
		double[] newLocation = facade.getLocation(theWorm);
		// The worm will move adjacent to the cell in row 7 and column 4.
		assertTrue(facade.isAdjacent(otherWorld, newLocation, 2.0));
		double distanceToEdge = Math.sqrt(Math.pow(newLocation[0] - 5.0, 2.0) + Math.pow(newLocation[1] - 2.0, 2.0));
		assertEquals(2.1, distanceToEdge, 0.2);
		assertTrue((facade.getNbActionPoints(theWorm) >= oldNbActionPoints - 5)
				&& (facade.getNbActionPoints(theWorm) <= oldNbActionPoints - 3));
		score += 15;
	}

	@Test
	public void move_ReachingImpassableTerrainDeviatingDirectionSeveralDirections() {
		max_score += 15;
		map10x10 = new boolean[][] { { true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, false, false, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, false, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false }, };
		World otherWorld = facade.createWorld(10.0, 10.0, map10x10);
		Worm theWorm = facade.createWorm(otherWorld, new double[] { 7.0, 5.0 }, PI, 2.0, "Worm", null);
		long oldNbActionPoints = facade.getNbActionPoints(theWorm);
		facade.move(theWorm);
		double[] newLocation = facade.getLocation(theWorm);
		// The worm will move adjacent to the cell in row 1 and column 6 or adjacent to
		// row 9 and column 6.
		assertTrue(facade.isAdjacent(otherWorld, newLocation, 2.0));
		double distanceToEdge = Math.min(
				Math.sqrt(Math.pow(newLocation[0] - 5.0, 2.0) + Math.pow(newLocation[1] - 2.0, 2.0)),
				Math.sqrt(Math.pow(newLocation[0] - 5.0, 2.0) + Math.pow(newLocation[1] - 8.0, 2.0)) );
		assertEquals(2.1, distanceToEdge, 0.2);
		assertTrue((facade.getNbActionPoints(theWorm) >= oldNbActionPoints - 5)
				&& (facade.getNbActionPoints(theWorm) <= oldNbActionPoints - 3));
		score += 15;
	}

	@Test
	public void move_OverlappingWormAtDestination() {
		max_score += 12;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 8.0, 8.0 }, PI, 1.0, "Worm", null);
		Worm otherWorm = facade.createWorm(theWorld, new double[] { 4.5, 7.0 }, 3 * PI / 4.0, 2.0, "WormA", null);
		long oldHitPointsTheWorm = facade.getNbHitPoints(theWorm).longValue();
		long oldHitPointsOtherWorm = facade.getNbHitPoints(otherWorm).longValue();
		facade.move(theWorm);
		long newHitPointsTheWorm = facade.getNbHitPoints(theWorm).longValue();
		long newHitPointOtherWorm = facade.getNbHitPoints(otherWorm).longValue();
		long deltaTheWorm = oldHitPointsTheWorm - newHitPointsTheWorm;
		long deltaOtherWorm = oldHitPointsOtherWorm - newHitPointOtherWorm;
		long delta = deltaTheWorm + deltaOtherWorm;
		assertTrue((delta >= 1) && (delta <= 10));
		assertEquals(deltaTheWorm, Math.round(delta / (2.0 / 3.0)));
		assertEquals(deltaOtherWorm, delta - deltaTheWorm);
		score += 12;
	}

	@Test
	public void move_OverlappingWormsAtDestination() {
		max_score += 14;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 6.0, 6.0 }, PI, 3.0, "Worm", null);
		Worm otherWorm1 = facade.createWorm(theWorld, new double[] { 1.0, 8.0 }, 3 * PI / 4.0, 1.0, "WormA", null);
		Worm otherWorm2 = facade.createWorm(theWorld, new double[] { 3.5, 7.5 }, 3 * PI / 4.0, 1.5, "WormB", null);
		long oldHitPointsTheWorm = facade.getNbHitPoints(theWorm).longValue();
		long oldHitPointsOtherWorm1 = facade.getNbHitPoints(otherWorm1).longValue();
		long oldHitPointsOtherWorm2 = facade.getNbHitPoints(otherWorm2).longValue();
		facade.move(theWorm);
		long newHitPointsTheWorm = facade.getNbHitPoints(theWorm).longValue();
		long newHitPointOtherWorm1 = facade.getNbHitPoints(otherWorm1).longValue();
		long newHitPointOtherWorm2 = facade.getNbHitPoints(otherWorm2).longValue();
		long deltaTheWorm = oldHitPointsTheWorm - newHitPointsTheWorm;
		long deltaOtherWorm1 = oldHitPointsOtherWorm1 - newHitPointOtherWorm1;
		long deltaOtherWorm2 = oldHitPointsOtherWorm2 - newHitPointOtherWorm2;
		assertTrue(deltaTheWorm <= 0);
		assertTrue((deltaOtherWorm1 >= 1) && (deltaOtherWorm1 <= 14));
		assertTrue((deltaOtherWorm2 >= 1) && (deltaOtherWorm2 <= 15));
		// Large margin to cope with possible rounding errors.
		assertEquals(deltaTheWorm, (-deltaOtherWorm1 / 4.0) + (-deltaOtherWorm2 / 3.0), 1.0);
		score += 14;
	}

	@Test
	public void move_DestinationOutsideWorld() {
		max_score += 10;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 8.0, 1.2 }, 3 * PI / 2.0, 1.0, "Worm", null);
		
		long oldNbActionPoints = facade.getNbActionPoints(theWorm);
		facade.move(theWorm);
		double[] newLocation = facade.getLocation(theWorm);
		assertEquals(8.0 - 0.0, newLocation[0], EPS);
		assertEquals(1.2 - 1.0, newLocation[1], EPS);
		assertEquals(oldNbActionPoints - (1 + 1 * 3), facade.getNbActionPoints(theWorm));
		assertNull(facade.getWorld(theWorm));
		assertFalse(facade.hasAsWorm(theWorld, theWorm));
		score += 10;
	}

	@Test
	public void move_WormNotInWorld() {
		max_score += 2;
		Worm theWorm = facade.createWorm(null, new double[] { 8.0, 1.2 }, 3 * PI / 2.0, 1.0, "Worm", null);
		try {
			facade.move(theWorm);
			fail();
		} catch (ModelException exc) {
			score += 2;
		}
	}

	@Test
	public void move_NotEnoughActionPoints() {
		max_score += 2;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 8.0, 7.5 }, 3 * PI / 4.0, 1.0, "Worm", null);
		long oldNbActionPoints = facade.getNbActionPoints(theWorm);
		facade.decreaseNbActionPoints(theWorm, oldNbActionPoints - 1);
		try {
			facade.move(theWorm);
			fail();
		} catch (ModelException exc) {
			score += 2;
		}
	}

	@Test
	public void move_TerminatedWorm() {
		max_score += 1;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 8.0, 3.0 }, 3 * PI / 4.0, 1.0, "Worm", null);
		facade.terminate(theWorm);
		try {
			facade.move(theWorm);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void fall_InWorldNoOverlappingWorms() {
		max_score += 15;
		map10x10 = new boolean[][] { { true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, false, false, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, false, true, true, true, true, false }, };
		World otherWorld = facade.createWorld(10.0, 10.0, map10x10);
		Worm theWorm = facade.createWorm(otherWorld, new double[] { 4.5, 6.0 }, PI, 2.0, "Worm", null);
		try {
			BigInteger oldNbHitPoints = facade.getNbHitPoints(theWorm);
			facade.fall(theWorm);
			double[] newLocation = facade.getLocation(theWorm);
			assertTrue(facade.isAdjacent(otherWorld, newLocation, facade.getRadius(theWorm)));
			assertEquals(4.5, newLocation[0], EPS);
			assertEquals(3.0+facade.getRadius(theWorm)*0.1/2.0, newLocation[1], facade.getRadius(theWorm)*0.12/2.0);
			assertEquals(oldNbHitPoints.subtract(BigInteger.valueOf(9)),facade.getNbHitPoints(theWorm) );
			score += 15;
		} catch (MustNotImplementException exc) {
			max_score -= 15;
		}
	}

	@Test
	public void fall_InWorldOverlappingWorms() {
		max_score += 12;
		map10x10 = new boolean[][] { { true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, false, false, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, false, false, false, true, true, true, false }, };
		World otherWorld = facade.createWorld(10.0, 10.0, map10x10);
		Worm theWorm = facade.createWorm(otherWorld, new double[] { 4.5, 6.0 }, PI, 2.0, "Worm", null);
		Worm worm1 = facade.createWorm(otherWorld, new double[] { 5.5, 2.0 }, PI, 1.0, "WormA", null);
		Worm worm2 = facade.createWorm(otherWorld, new double[] { 3.5, 2.0 }, PI, 1.0, "WormB", null);
		try {
			long oldNbHitPoints_TheWorm = facade.getNbHitPoints(theWorm).longValue();
			long oldNbHitPoints_worm1 = facade.getNbHitPoints(worm1).longValue();
			long oldNbHitPoints_worm2 = facade.getNbHitPoints(worm2).longValue();
			facade.fall(theWorm);
			assertEquals(facade.getNbHitPoints(worm1).longValue(), oldNbHitPoints_worm1 / 2);
			assertEquals(facade.getNbHitPoints(worm2).longValue(), oldNbHitPoints_worm2 / 2);
			assertEquals(oldNbHitPoints_TheWorm - 9 + oldNbHitPoints_worm1 / 2 + oldNbHitPoints_worm2 / 2,
					facade.getNbHitPoints(theWorm).longValue());

			score += 12;
		} catch (MustNotImplementException exc) {
			max_score -= 12;
		}
	}

	@Test
	public void fall_OutOfWorld() {
		max_score += 6;
		// Worm hanging on the ceiling.
		Worm theWorm = facade.createWorm(theWorld, new double[] { 3.0, 8.0 }, 3 * PI / 4.0, 1.0, "Worm", null);
		try {
			facade.fall(theWorm);
			assertFalse(facade.hasAsWorm(theWorld, theWorm));
			assertNull(facade.getWorld(theWorm));
			score += 3;
		} catch (MustNotImplementException exc) {
			max_score -= 3;
		}
		// Worm hanging on side wall.
		Worm otherWorm = facade.createWorm(theWorld, new double[] { 8.0, 3.0 }, 3 * PI / 4.0, 1.0, "WormB", null);
		try {
			facade.fall(otherWorm);
			assertFalse(facade.hasAsWorm(theWorld, otherWorm));
			assertNull(facade.getWorld(otherWorm));
			score += 3;
		} catch (MustNotImplementException exc) {
			max_score -= 3;
		}
	}

	@Test
	public void jumpStep_LegalCase() {
		max_score += 4;
		double[] worm_location = new double[] { 7.5, 1.5 };
		double worm_orientation = 2 * PI / 3.0;
		double worm_radius = 1.5;
		long worm_actionPoints = referenceMaxActionPoints(worm_radius);
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		double[] expectedLocation = referenceJumpStep(0.1, worm_location, worm_actionPoints, worm_radius,
				worm_orientation);
		double[] stepLocation = facade.getJumpStep(theWorm, 0.1);
		assertEquals("Result must have exactly 2 coordinates", 2, stepLocation.length);
		assertEquals("x coordinate must match", expectedLocation[0], stepLocation[0], EPS);
		assertEquals("y coordinate must match", expectedLocation[1], stepLocation[1], EPS);
		score += 4;
	}

	@Test
	public void jumpStep_VerticalJump() {
		max_score += 2;
		double[] worm_location = new double[] { 7.5, 1.5 };
		double worm_orientation = PI / 2.0;
		double worm_radius = 1.5;
		long worm_actionPoints = referenceMaxActionPoints(worm_radius);
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		double[] expectedLocation = referenceJumpStep(0.1, worm_location, worm_actionPoints, worm_radius,
				worm_orientation);
		double[] stepLocation = facade.getJumpStep(theWorm, 0.1);
		assertEquals("Result must have exactly 2 coordinates", 2, stepLocation.length);
		assertEquals("x coordinate must match", expectedLocation[0], stepLocation[0], EPS);
		assertEquals("y coordinate must match", expectedLocation[1], stepLocation[1], EPS);
		score += 2;
	}

	@Test
	public void jumpStep_HorizontalJump() {
		max_score += 2;
		double[] worm_location = new double[] { 7.5, 1.5 };
		double worm_orientation = PI;
		double worm_radius = 1.5;
		long worm_actionPoints = referenceMaxActionPoints(worm_radius);
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		double[] expectedLocation = referenceJumpStep(0.1, worm_location, worm_actionPoints, worm_radius,
				worm_orientation);
		double[] stepLocation = facade.getJumpStep(theWorm, 0.1);
		assertEquals("Result must have exactly 2 coordinates", 2, stepLocation.length);
		assertEquals("x coordinate must match", expectedLocation[0], stepLocation[0], EPS);
		assertEquals("y coordinate must match", expectedLocation[1], stepLocation[1], EPS);
		score += 2;
	}

	@Test
	public void jumpStep_NoActionPoints() {
		max_score += 1;
		double[] worm_location = new double[] { 7.5, 1.5 };
		double worm_orientation = PI;
		double worm_radius = 1.5;
		long worm_actionPoints = referenceMaxActionPoints(worm_radius);
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		facade.decreaseNbActionPoints(theWorm, worm_actionPoints);
		try {
			facade.getJumpStep(theWorm, 0.1);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void jumpStep_ImproperOrientation() {
		max_score += 1;
		double[] worm_location = new double[] { 7.5, 1.5 };
		double worm_orientation = 5.0 * PI / 4.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		try {
			facade.getJumpStep(theWorm, 0.1);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void jumpStep_ImproperDt() {
		max_score += 1;
		double[] worm_location = new double[] { 7.5, 1.5 };
		double worm_orientation = 5.0 * PI / 4.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		try {
			facade.getJumpStep(theWorm, Double.NaN);
			fail();
		} catch (ModelException exc) {
		}
		try {
			facade.getJumpStep(theWorm, -1.0);
			fail();
		} catch (ModelException exc) {
		}
		score += 1;
	}

	@Test
	public void jumpTime_ReachingImpassableTerrain() {
		max_score += 14;
		double[] worm_location = new double[] { 7.5, 6.5 };
		double worm_orientation = 3 * PI / 4.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		double jumpTime = facade.getJumpTime(theWorm, 0.01);
		assertEquals(0.39, jumpTime, 0.05);
		double[] locationAfterJump = facade.getJumpStep(theWorm, jumpTime);
		assertTrue(facade.isAdjacent(theWorld, locationAfterJump, worm_radius));
		score += 14;
	}

	@Test
	public void jumpTime_OutsideWorld() {
		max_score += 8;
		double[] worm_location = new double[] { 8.5, 0.6 };
		double worm_orientation = 7.0 * PI / 8.0;
		double worm_radius = 0.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		try {
			facade.getJumpTime(theWorm, 0.05);
			fail();
		} catch (ModelException exc) {
			score += 8;
		}
	}

	@Test
	public void jumpTime_TooShort() {
		max_score += 8;
		double[] worm_location = new double[] { 7.5, 7.3 };
		double worm_orientation = 7.0 * PI / 8.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		try {
			facade.getJumpTime(theWorm, 0.05);
			fail();
		} catch (ModelException exc) {
			score += 8;
		}
	}

	@Test
	public void jumpTime_WormCannotJump() {
		max_score += 1;
		try {
			facade.getJumpTime(fixtureWorm, 0.001);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void jump_ReachingImpassableTerrain() {
		max_score += 9;
		double[] worm_location = new double[] { 7.5, 6.5 };
		double worm_orientation = 3 * PI / 4.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		facade.jump(theWorm, 0.01);
		assertTrue(facade.isAdjacent(theWorld, facade.getLocation(theWorm), worm_radius));
		assertEquals(0, facade.getNbActionPoints(theWorm));
		score += 9;
	}

	@Test
	public void jump_SingleOverlappingWorm() {
		max_score += 11;
		map10x10 = new boolean[][] { { true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, false, false, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ false, false, false, false, false, false, false, false, false, false }, };
		World otherWorld = facade.createWorld(10.0, 10.0, map10x10);
		double[] worm_location = new double[] { 2.5, 3.0 };
		double worm_orientation = PI / 4.0;
		double worm_radius = 2.0;
		Worm theWorm = facade.createWorm(otherWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		Worm worm1 = facade.createWorm(otherWorld, new double[] { 8.0, 4.0 }, 0.5, 1.0, "WormA", null);
		long oldNbHitPoints_TheWorm = facade.getNbHitPoints(theWorm).longValue();
		long oldNbHitPoints_worm1 = facade.getNbHitPoints(worm1).longValue();
		facade.jump(theWorm, 0.01);
		assertTrue(facade.isAdjacent(theWorld, facade.getLocation(theWorm), worm_radius));
		assertEquals(0, facade.getNbActionPoints(theWorm));
		long newNbHitPoints_TheWorm = facade.getNbHitPoints(theWorm).longValue();
		long newNbHitPoints_worm1 = facade.getNbHitPoints(worm1).longValue();
		assertTrue((newNbHitPoints_TheWorm < oldNbHitPoints_TheWorm) ^ (newNbHitPoints_worm1 < oldNbHitPoints_worm1));
		assertTrue((newNbHitPoints_TheWorm == 0) || (newNbHitPoints_TheWorm >= oldNbHitPoints_TheWorm - 5));
		assertTrue((newNbHitPoints_worm1 == 0) || (newNbHitPoints_worm1 >= oldNbHitPoints_TheWorm - 40));
		score += 11;
	}

	@Test
	public void jump_SeveralOverlappingWorms() {
		max_score += 13;
		map10x10 = new boolean[][] { { true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, false, false, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ false, false, false, false, false, false, false, false, false, false }, };
		World otherWorld = facade.createWorld(10.0, 10.0, map10x10);
		double[] worm_location = new double[] { 2.5, 3.0 };
		double worm_orientation = PI / 4.0;
		double worm_radius = 2.0;
		Worm theWorm = facade.createWorm(otherWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		Worm worm1 = facade.createWorm(otherWorld, new double[] { 8.0, 4.0 }, 0.5, 1.0, "WormA", null);
		Worm worm2 = facade.createWorm(otherWorld, new double[] { 6.0, 4.0 }, 0.5, 3.0, "WormB", null);
		long oldNbHitPoints_TheWorm = facade.getNbHitPoints(theWorm).longValue();
		long oldNbHitPoints_worm1 = facade.getNbHitPoints(worm1).longValue();
		long oldNbHitPoints_worm2 = facade.getNbHitPoints(worm2).longValue();
		facade.jump(theWorm, 0.01);
		assertTrue(facade.isAdjacent(theWorld, facade.getLocation(theWorm), worm_radius));
		assertEquals(0, facade.getNbActionPoints(theWorm));
		long newNbHitPoints_TheWorm = facade.getNbHitPoints(theWorm).longValue();
		long newNbHitPoints_worm1 = facade.getNbHitPoints(worm1).longValue();
		long newNbHitPoints_worm2 = facade.getNbHitPoints(worm2).longValue();
		assertTrue(((newNbHitPoints_TheWorm == oldNbHitPoints_TheWorm) && (newNbHitPoints_worm1 < oldNbHitPoints_worm1)
				&& (newNbHitPoints_worm2 < oldNbHitPoints_worm2))
				|| ((newNbHitPoints_TheWorm < oldNbHitPoints_TheWorm) && ((newNbHitPoints_worm1 == oldNbHitPoints_worm1)
						|| (newNbHitPoints_worm2 == oldNbHitPoints_worm2))));
		score += 13;
	}

	@Test
	public void jump_NoJumpPossible() {
		max_score += 3;
		try {
			double[] worm_location = new double[] { 7.5, 7.5 };
			double worm_orientation = 3 * PI / 2.0;
			double worm_radius = 1.5;
			Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
			facade.jump(theWorm, 0.01);
			fail();
		} catch (ModelException exc) {
			score += 3;
		}
	}

	@Test
	public void jump_DirectedDownward() {
		max_score += 1;
		try {
			double[] worm_location = new double[] { 7.5, 6.5 };
			double worm_orientation = 3 * PI / 2.0;
			double worm_radius = 1.5;
			Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
			facade.jump(theWorm, 0.01);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void eat_WormNotInWorld() {
		max_score += 1;
		facade.eat(fixtureWorm);
		assertEquals(FIXTURE_RADIUS, facade.getRadius(fixtureWorm), EPS);
		assertEquals(FIXTURE_LOCATION[0], facade.getLocation(fixtureWorm)[0], EPS);
		assertEquals(FIXTURE_LOCATION[1], facade.getLocation(fixtureWorm)[1], EPS);
		score += 1;
	}

	@Test
	public void eat_NoOverlappingPortion() {
		max_score += 2;
		double[] worm_location = new double[] { 4.5, 7.5 };
		double worm_orientation = 3 * PI / 2.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		double[] location = new double[] { 0.5, 8.795 };
		Food newFood = facade.createFood(theWorld, location);
		facade.eat(theWorm);
		assertFalse(facade.isTerminated(newFood));
		assertEquals(1.5, facade.getRadius(theWorm), EPS);
		assertEquals(4.5, facade.getLocation(theWorm)[0], EPS);
		assertEquals(7.5, facade.getLocation(theWorm)[1], EPS);
		score += 2;
	}

	@Test
	public void eat_OverlapWithSingleHealthyPortion() {
		max_score += 8;
		double[] worm_location = new double[] { 4.5, 7.5 };
		double worm_orientation = 3 * PI / 2.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		double[] location = new double[] { 4.5, 8.795 };
		Food newFood = facade.createFood(theWorld, location);
		long oldNbActionPoints = facade.getNbActionPoints(theWorm);
		facade.eat(theWorm);
		assertTrue(facade.isTerminated(newFood));
		assertEquals(1.5 * 1.1, facade.getRadius(theWorm), EPS);
		assertTrue(facade.isAdjacent(theWorld, facade.getLocation(theWorm), facade.getRadius(theWorm)));
		assertTrue(getDistance(worm_location,facade.getLocation(theWorm)) < facade.getRadius(theWorm)*0.2);
		assertEquals(oldNbActionPoints - 8, facade.getNbActionPoints(theWorm));
		score += 8;
	}

	@Test
	public void eat_OverlapWithSinglePoisonousPortion() {
		max_score += 8;
		double[] worm_location = new double[] { 4.5, 7.49 };
		double worm_orientation = 3 * PI / 2.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		double[] location = new double[] { 4.5, 8.795 };
		Food newFood = facade.createFood(theWorld, location);
		facade.poison(newFood);
		long oldNbActionPoints = facade.getNbActionPoints(theWorm);
		facade.eat(theWorm);
		assertTrue(facade.isTerminated(newFood));
		assertEquals(1.5 * 0.9, facade.getRadius(theWorm), EPS);
		assertTrue(facade.isAdjacent(theWorld, facade.getLocation(theWorm), facade.getRadius(theWorm)));
		assertTrue(getDistance(worm_location,facade.getLocation(theWorm)) < facade.getRadius(theWorm)*0.2);
		long expectedNbActionPoints = Math.min(oldNbActionPoints - 8,
				referenceMaxActionPoints(facade.getRadius(theWorm)));
		assertEquals(expectedNbActionPoints, facade.getNbActionPoints(theWorm));
		score += 8;
	}

	@Test
	public void eat_SmallWormOverlapWithSinglePoisonousPortion() {
		max_score += 6;
		double[] worm_location = new double[] { 4.5, 8.73 };
		double worm_orientation = 3 * PI / 2.0;
		double worm_radius = 0.26;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		double[] location = new double[] { 4.5, 8.795 };
		Food newFood = facade.createFood(theWorld, location);
		facade.poison(newFood);
		long oldNbActionPoints = facade.getNbActionPoints(theWorm);
		facade.eat(theWorm);
		assertTrue(facade.isTerminated(newFood));
		assertEquals(0.25, facade.getRadius(theWorm), EPS);
		assertTrue(facade.isAdjacent(theWorld, facade.getLocation(theWorm), facade.getRadius(theWorm)));
		assertTrue(getDistance(worm_location,facade.getLocation(theWorm)) < facade.getRadius(theWorm)*0.2);
		long expectedNbActionPoints = Math.min(oldNbActionPoints - 8,
				referenceMaxActionPoints(facade.getRadius(theWorm)));
		assertEquals(expectedNbActionPoints, facade.getNbActionPoints(theWorm));
		score += 6;
	}

	@Test
	public void eat_OverlapWithSeveralHealthyPortions() {
		max_score += 2;
		double[] worm_location = new double[] { 4.5, 7.5 };
		double worm_orientation = 3 * PI / 2.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		Food newFood1 = facade.createFood(theWorld, new double[] { 4.5, 8.795 });
		Food newFood2 = facade.createFood(theWorld, new double[] { 4.2, 8.795 });
		Food newFood3 = facade.createFood(theWorld, new double[] { 4.8, 8.795 });
		long oldNbActionPoints = facade.getNbActionPoints(theWorm);
		facade.eat(theWorm);
		assertTrue(facade.isTerminated(newFood1) ^ facade.isTerminated(newFood2) ^ facade.isTerminated(newFood3));
		assertEquals(1.5 * 1.1, facade.getRadius(theWorm), EPS);
		assertTrue(facade.isAdjacent(theWorld, facade.getLocation(theWorm), facade.getRadius(theWorm)));
		assertTrue(getDistance(worm_location,facade.getLocation(theWorm)) < facade.getRadius(theWorm)*0.2);
		assertEquals(oldNbActionPoints - 8, facade.getNbActionPoints(theWorm));
		score += 2;
	}

	@Test
	public void eat_WormLocatedInCorner() { 
		max_score += 18;
		double[] worm_location = new double[] { 7.5, 7.5 };
		double worm_orientation = 3 * PI / 2.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		Food newFood1 = facade.createFood(theWorld, new double[] { 7.5, 8.795 });
		Food newFood2 = facade.createFood(theWorld, new double[] { 8.795, 7.5 });
		long oldNbActionPoints = facade.getNbActionPoints(theWorm);
		facade.eat(theWorm);
		assertTrue(facade.isTerminated(newFood1) ^ facade.isTerminated(newFood2));
		assertEquals(1.5 * 1.1, facade.getRadius(theWorm), EPS);
		assertTrue(facade.isAdjacent(theWorld, facade.getLocation(theWorm), facade.getRadius(theWorm)));
		assertTrue(getDistance(worm_location,facade.getLocation(theWorm)) < facade.getRadius(theWorm)*0.2);
		assertEquals(oldNbActionPoints - 8, facade.getNbActionPoints(theWorm));
		score += 18;
	}

	@Test
	public void eat_NotEnoughActionPoints() {
		max_score += 2;
		double[] worm_location = new double[] { 4.5, 7.5 };
		double worm_orientation = 3 * PI / 2.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		double[] location = new double[] { 4.5, 8.795 };
		Food newFood = facade.createFood(theWorld, location);
		facade.decreaseNbActionPoints(theWorm, facade.getNbActionPoints(theWorm) - 7);
		long oldNbActionPoints = facade.getNbActionPoints(theWorm);
		facade.eat(theWorm);
		assertFalse(facade.isTerminated(newFood));
		assertEquals(1.5, facade.getRadius(theWorm), EPS);
		assertEquals(4.5, facade.getLocation(theWorm)[0], EPS);
		assertEquals(7.5, facade.getLocation(theWorm)[1], EPS);
		assertEquals(oldNbActionPoints, facade.getNbActionPoints(theWorm));
		score += 2;
	}

	@Test
	public void eat_EnlargedWormNotFullyInWorld() {//TODO impossible
		max_score += 8;
		double[] worm_location = new double[] { 7.5, 1.505 };
		double worm_orientation = 3 * PI / 2.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		double[] location = new double[] { 8.795, 1.5 };
		Food newFood = facade.createFood(theWorld, location);
		long oldNbActionPoints = facade.getNbActionPoints(theWorm);
		facade.eat(theWorm);
		assertTrue(facade.isTerminated(newFood));
		assertNull(facade.getWorld(theWorm));
		assertTrue(facade.getAllItems(theWorld).isEmpty());
		assertEquals(oldNbActionPoints - 8, facade.getNbActionPoints(theWorm));
		score += 8;
	}

	@Test
	public void eat_ExplodingWorm() {
		max_score += 15;
		map10x10 = new boolean[][] { { true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, false, false, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, false, false, false,false, false,false },
				{ true, true, true, true, false, true, true, true, true, false },
				{ true, true, true, true, false, true, true, true, true, false },
				{ true, true, true, true, false, true, true, true, true, false },
				{ true, true, true, true, false, true, true, true, true, false },
				{ false,false,false,false,false,false,false,false,false,false }, };
		World otherWorld = facade.createWorld(10.0, 10.0, map10x10);
		double[] worm_location = new double[] { 7.0, 3.0 };
		double worm_orientation = PI / 4.0;
		double worm_radius = 2.0;
		Worm theWorm = facade.createWorm(otherWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		facade.createFood(otherWorld, new double[] { 8.79, 3.0 });
		facade.eat(theWorm);
		assertTrue(facade.isTerminated(theWorm));
		assertTrue(facade.getAllItems(otherWorld).isEmpty());
		score += 15;
	}

	@Test
	public void fire_LegalCase() {
		max_score += 12;
		double[] worm_location = new double[] { 7.5, 3.505 };
		double worm_orientation = 3 * PI / 2.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		for (int nbTries = 1; nbTries <= 10; nbTries++) {
			long oldNbActionPoints = facade.getNbActionPoints(theWorm);
			Projectile projectile = facade.fire(theWorm);
			assertEquals(3 * PI / 2.0, facade.getOrientation(projectile), EPS);
			double[] projectileLocation = facade.getLocation(projectile);
			assertEquals(7.5, projectileLocation[0], EPS);
			assertEquals(3.505 - worm_radius - facade.getRadius(projectile), projectileLocation[1], EPS);
			assertTrue(facade.getNbActionPoints(theWorm) < oldNbActionPoints);
		}
		score += 12;
	}

	@Test
	public void fire_WormOverlapsWithBullet() {
		max_score += 12;
		double[] worm_location = new double[] { 7.495, 5.8 };
		double worm_orientation = PI / 2.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		Projectile projectile = facade.fire(theWorm);
		facade.jump(projectile, 0.00001);
		Worm otherWorm = facade.createWorm(theWorld, new double[] { 7.495, 7.5 }, worm_orientation, worm_radius, "Worm",
				null);
		BigInteger oldNbHitPoints = facade.getNbHitPoints(otherWorm);
		assertNull(facade.fire(otherWorm));
		assertTrue(oldNbHitPoints.compareTo(facade.getNbHitPoints(otherWorm)) > 0);
		assertTrue(facade.isTerminated(projectile));
		score += 12;
	}

	@Test
	public void fire_BulletNotFullyInWorld() {
		max_score += 7;
		double[] worm_location = new double[] { 7.5, 1.50005 };
		double worm_orientation = 3 * PI / 2.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		try {
			facade.fire(theWorm);
			fail();
		} catch (ModelException exc) {
			score += 7;
		}
	}

	@Test
	public void fire_NotInWorld() {
		max_score += 1;
		try {
			facade.fire(fixtureWorm);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	@Test
	public void fire_NotEnoughActionPoints() {
		max_score += 1;
		try {
			double[] worm_location = new double[] { 7.5, 1.505 };
			double worm_orientation = 3 * PI / 2.0;
			double worm_radius = 1.5;
			Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
			facade.decreaseNbActionPoints(theWorm, facade.getNbActionPoints(theWorm) - 20);
			facade.fire(theWorm);
			fail();
		} catch (ModelException exc) {
			score += 1;
		}
	}

	/**************
	 * FOOD TESTS
	 *************/

	@Test
	public void createFood_LegalCaseNoWorld() {
		max_score += 1;
		double[] location = new double[] { 3.0, -7.0 };
		Food newFood = facade.createFood(null, location);
		assertNull(facade.getWorld(newFood));
		assertEquals(location[0], facade.getLocation(newFood)[0], EPS);
		assertEquals(location[1], facade.getLocation(newFood)[1], EPS);
		assertEquals(0.2, facade.getRadius(newFood), EPS);
		assertEquals(150.0 * (4.0 / 3.0 * PI * Math.pow(0.2, 3.0)), facade.getMass(newFood), EPS);
		assertFalse(facade.isPoisonous(newFood));
		score += 1;
	}

	@Test
	public void createFood_LegalCaseinWorld() {
		max_score += 1;
		double[] location = new double[] { 3.0, 8.79 };
		Food newFood = facade.createFood(theWorld, location);
		assertEquals(theWorld, facade.getWorld(newFood));
		assertTrue(facade.hasAsFood(theWorld, newFood));
		assertEquals(location[0], facade.getLocation(newFood)[0], EPS);
		assertEquals(location[1], facade.getLocation(newFood)[1], EPS);
		assertEquals(0.2, facade.getRadius(newFood), EPS);
		assertEquals(150.0 * (4.0 / 3.0 * PI * Math.pow(0.2, 3.0)), facade.getMass(newFood), EPS);
		assertFalse(facade.isPoisonous(newFood));
		score += 1;
	}

	@Test
	public void terminateFood_NoWorld() {
		max_score += 1;
		facade.terminate(fixtureFood);
		assertTrue(facade.isTerminated(fixtureFood));
		score += 1;
	}

	@Test
	public void terminateFood_InWorld() {
		max_score += 1;
		double[] location = new double[] { 3.0, 8.79 };
		Food newFood = facade.createFood(theWorld, location);
		facade.terminate(newFood);
		assertTrue(facade.isTerminated(newFood));
		assertNull(facade.getWorld(newFood));
		assertFalse(facade.hasAsFood(theWorld, newFood));
		score += 1;
	}

	/*******************
	 * PROJECTILE TESTS
	 *******************/

	@Test
	public void jumpStepProjectile_LegalCase() {
		max_score += 4;
		double[] worm_location = new double[] { 7.5, 1.5 };
		double worm_orientation = 2 * PI / 3.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		Projectile projectile = facade.fire(theWorm);
		double[] stepLocation = facade.getJumpStep(projectile, 0.1);
		double[] expectedRifleLocation = new double[] { 2.997, 9.275 };
		double[] expectedBazookaLocation = new double[] { 6.114, 3.875 };
		assertEquals("Result must have exactly 2 coordinates", 2, stepLocation.length);
		assertTrue((Math.abs(stepLocation[0] - expectedRifleLocation[0]) < 0.01)
				|| (Math.abs(stepLocation[0] - expectedBazookaLocation[0]) < 0.01));
		assertTrue((Math.abs(stepLocation[1] - expectedRifleLocation[1]) < 0.01)
				|| (Math.abs(stepLocation[1] - expectedBazookaLocation[1]) < 0.01));
		score += 4;
	}

	@Test
	public void jumpTimeProjectile_ReachingImpassableTerrain() {
		max_score += 8;
		double[] worm_location = new double[] { 7.5, 1.5 };
		double worm_orientation = PI / 2.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		Projectile projectile = facade.fire(theWorm);
		double jumpTime = facade.getJumpTime(projectile, 0.00001);
		assertTrue((Math.abs(jumpTime - 0.08) < 0.01) || (Math.abs(jumpTime - 0.53) < 0.01));
		double[] locationAfterJump = facade.getJumpStep(projectile, jumpTime);
		assertTrue(facade.isAdjacent(theWorld, locationAfterJump, facade.getRadius(projectile))
				|| (!facade.isPassable(theWorld, locationAfterJump, facade.getRadius(projectile))));
		score += 8;
	}

	@Test
	public void jumpTimeProjectile_HittingWorm() {
		max_score += 8;
		double[] worm_location = new double[] { 7.5, 1.5 };
		double worm_orientation = PI / 2.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		facade.createWorm(theWorld, new double[] { 7.5, 7.5 }, 0.0, 1.5, "Other", null);
		Projectile projectile = facade.fire(theWorm);
		double jumpTime = facade.getJumpTime(projectile, 0.00001);
		assertTrue((Math.abs(jumpTime - 0.08) < 0.01) || (Math.abs(jumpTime - 0.53) < 0.01));
		double[] locationAfterJump = facade.getJumpStep(projectile, jumpTime);
		assertTrue(locationAfterJump[1] >= 6.0 - facade.getRadius(projectile));
		score += 8;
	}

	@Test
	public void jumpTimeProjectile_OutsideWorld() {
		max_score += 6;
		double[] worm_location = new double[] { 7.5, 1.5 };
		double worm_orientation = 0.99 * PI;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		try {
			Projectile projectile = facade.fire(theWorm);
			facade.getJumpTime(projectile, 0.00001);
			fail();
		} catch (ModelException exc) {
			score += 6;
		}
	}

	@Test
	public void jumpProjectile_HittingSingleWorm() {
		max_score += 10;
		double[] worm_location = new double[] { 7.5, 1.5 };
		double worm_orientation = PI / 2.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		Worm hittedWorm = facade.createWorm(theWorld, new double[] { 7.5, 7.5 }, 0.0, 1.5, "Other", null);
		BigInteger oldNbHitPoints = facade.getNbHitPoints(hittedWorm);
		Projectile projectile = facade.fire(theWorm);
		facade.jump(projectile, 0.00001);
		assertTrue(oldNbHitPoints.compareTo(facade.getNbHitPoints(hittedWorm)) > 0);
		assertTrue(facade.isTerminated(projectile));
		score += 10;
	}

	@Test
	public void jumpProjectile_HittingSeveralWorms() {
		max_score += 6;
		double[] worm_location = new double[] { 7.5, 1.5 };
		double worm_orientation = PI / 2.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		Worm hittedWorm1 = facade.createWorm(theWorld, new double[] { 7.5, 7.5 }, 0.0, 1.5, "OtherA", null);
		Worm hittedWorm2 = facade.createWorm(theWorld, new double[] { 7.5, 8.0 }, 0.0, 1.0, "OtherB", null);
		BigInteger oldNbHitPoints1 = facade.getNbHitPoints(hittedWorm1);
		BigInteger oldNbHitPoints2 = facade.getNbHitPoints(hittedWorm2);
		Projectile projectile = facade.fire(theWorm);
		facade.jump(projectile, 0.00001);
		assertTrue(oldNbHitPoints1.compareTo(facade.getNbHitPoints(hittedWorm1)) > 0);
		assertTrue(oldNbHitPoints2.compareTo(facade.getNbHitPoints(hittedWorm2)) > 0);
		assertTrue(facade.isTerminated(projectile));
		score += 6;
	}

	@Test
	public void jumpProjectile_OutsideWorld() {
		max_score += 4;
		double[] worm_location = new double[] { 7.5, 1.5 };
		double worm_orientation = 0.99 * PI;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		try {
			Projectile projectile = facade.fire(theWorm);
			facade.jump(projectile, 0.0001);
			fail();
		} catch (ModelException exc) {
			score += 4;
		}
	}

	@Test
	public void jumpProjectile_ReachingImpassableTerrain() {
		max_score += 6;
		double[] worm_location = new double[] { 7.5, 1.5 };
		double worm_orientation = PI / 2.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		Projectile projectile = facade.fire(theWorm);
		facade.jump(projectile, 0.00001);
		double[] locationAfterJump = facade.getLocation(projectile);
		assertTrue(facade.isAdjacent(theWorld, locationAfterJump, facade.getRadius(projectile))
				|| (!facade.isPassable(theWorld, locationAfterJump, facade.getRadius(projectile))));
		assertFalse(facade.isTerminated(projectile));
		score += 6;
	}

	/**************
	 * TEAM TESTS
	 *************/

	@Test
	public void createTeam_LegalCaseNoWorld() {
		max_score += 2;
		try {
			Team theTeam = facade.createTeam(null, "Team");
			assertEquals(0, facade.getNbWormsOfTeam(theTeam));
			assertEquals("Team", facade.getName(theTeam));
			assertFalse(facade.isTerminated(theTeam));
			score += 2;
		} catch (MustNotImplementException exc) {
			max_score -= 2;
		}
	}

	@Test
	public void createTeam_LegalCaseInWorld() {
		max_score += 4;
		try {
			assertEquals(0, facade.getNbWormsOfTeam(theTeam));
			assertEquals("TheTeam", facade.getName(theTeam));
			assertFalse(facade.isTerminated(theTeam));
			assertTrue(facade.getAllTeams(theWorld).contains(theTeam));
			score += 4;
		} catch (MustNotImplementException exc) {
			max_score -= 4;
		}
	}

	@Test
	public void createTeam_IllegalName() {
		max_score += 1;
		try {
			facade.createTeam(theWorld, "'abc");
			fail();
		} catch (ModelException exc) {
			score += 1;
		} catch (MustNotImplementException exc) {
			max_score -= 1;
		}
	}

	@Test
	public void createTeam_TooManyTeams() {
		max_score += 3;
		try {
			facade.createTeam(theWorld, "TeamB");
			score -= 3;
			facade.createTeam(theWorld, "TeamC");
			facade.createTeam(theWorld, "TeamD");
			facade.createTeam(theWorld, "TeamE");
			facade.createTeam(theWorld, "TeamF");
			facade.createTeam(theWorld, "TeamG");
			facade.createTeam(theWorld, "TeamH");
			facade.createTeam(theWorld, "TeamI");
			facade.createTeam(theWorld, "TeamJ");
			// We must reach this point, otherwise we are not
			// able to have 10 teams in a world.
			score += 3;
			facade.createTeam(theWorld, "TeamH");
			fail();
		} catch (ModelException exc) {
			score += 3;
		} catch (MustNotImplementException exc) {
			max_score -= 3;
		}
	}

	@Test
	public void terminate_SingleCase() {
		max_score += 5;
		try {
			Worm wormA = facade.createWorm(null, new double[] { 2.0, Double.POSITIVE_INFINITY }, 2.0, 12.0, "WormA",
					theTeam);
			Worm wormB = facade.createWorm(null, new double[] { 2.0, Double.POSITIVE_INFINITY }, 2.0, 12.0, "WormB",
					theTeam);
			facade.terminate(theTeam);
			assertTrue(facade.isTerminated(theTeam));
			assertEquals(0, facade.getAllWormsOfTeam(theTeam).size());
			assertNull(facade.getTeam(wormA));
			assertNull(facade.getTeam(wormB));
			score += 5;
		} catch (MustNotImplementException exc) {
			max_score -= 5;
		}
	}

	@Test
	public void getAllWormsOfTeam_LegalCase() {
		max_score += 2;
		try {
			Worm wormA = facade.createWorm(null, new double[] { 2.0, Double.POSITIVE_INFINITY }, 2.0, 12.0, "WormA",
					theTeam);
			Worm wormB = facade.createWorm(null, new double[] { 2.0, Double.POSITIVE_INFINITY }, 2.0, 12.0, "WormB",
					theTeam);
			List<Worm> members = facade.getAllWormsOfTeam(theTeam);
			assertEquals(2, members.size());
			assertTrue(members.contains(wormA));
			assertTrue(members.contains(wormB));
			score += 2;
		} catch (MustNotImplementException exc) {
			max_score -= 2;
		}
	}

	@Test
	public void getAllWormsOfTeam_LeakTest() {
		max_score += 10;
		try {
			facade.createWorm(null, new double[] { 2.0, Double.POSITIVE_INFINITY }, 2.0, 12.0, "WormA", theTeam);
			facade.createWorm(null, new double[] { 2.0, Double.POSITIVE_INFINITY }, 2.0, 12.0, "WormB", theTeam);
			List<Worm> members = facade.getAllWormsOfTeam(theTeam);
			Worm wormC = facade.createWorm(null, new double[] { 2.0, Double.POSITIVE_INFINITY }, 2.0, 12.0, "WormC",
					null);
			members.add(wormC);
			assertEquals(2, facade.getNbWormsOfTeam(theTeam));
			assertFalse(facade.getAllWormsOfTeam(theTeam).contains(wormC));
			score += 10;
		} catch (MustNotImplementException exc) {
			max_score -= 10;
		}
	}

	@Test
	public void addWormsToTeam_SeveralWormsOfSameWorld() {
		max_score += 4;
		try {
			Worm wormA = facade.createWorm(theWorld, new double[] { 2.3, 7.0 }, 2.0, 2.0, "WormA", null);
			Worm wormB = facade.createWorm(theWorld, new double[] { 6.8, 7.0 }, 2.0, 2.0, "WormB", null);
			Worm wormC = facade.createWorm(theWorld, new double[] { 7.0, 2.5 }, 2.0, 2.0, "WormC", null);
			facade.addWormsToTeam(theTeam, wormA, wormB, wormC);
			assertEquals(3, facade.getNbWormsOfTeam(theTeam));
			assertTrue(facade.getAllWormsOfTeam(theTeam).contains(wormA));
			assertEquals(theTeam, facade.getTeam(wormA));
			assertTrue(facade.getAllWormsOfTeam(theTeam).contains(wormB));
			assertEquals(theTeam, facade.getTeam(wormB));
			assertTrue(facade.getAllWormsOfTeam(theTeam).contains(wormC));
			assertEquals(theTeam, facade.getTeam(wormC));
			score += 4;
		} catch (MustNotImplementException exc) {
			max_score -= 4;
		}
	}

	@Test
	public void addWormsToTeam_WormsFromDifferentWorlds() {
		max_score += 4;
		try {
			Worm wormA = facade.createWorm(theWorld, new double[] { 2.3, 7.0 }, 2.0, 2.0, "WormA", null);
			Worm wormB = facade.createWorm(otherWorld, new double[] { 2.5, 3.0 }, 2.0, 1.0, "WormB", null);
			Worm wormC = facade.createWorm(null, new double[] { 7.0, 2.5 }, 2.0, 2.0, "WormC", null);
			facade.addWormsToTeam(theTeam, wormA, wormB, wormC);
			assertEquals(3, facade.getNbWormsOfTeam(theTeam));
			assertTrue(facade.getAllWormsOfTeam(theTeam).contains(wormA));
			assertEquals(theTeam, facade.getTeam(wormA));
			assertTrue(facade.getAllWormsOfTeam(theTeam).contains(wormB));
			assertEquals(theTeam, facade.getTeam(wormB));
			assertTrue(facade.getAllWormsOfTeam(theTeam).contains(wormC));
			assertEquals(theTeam, facade.getTeam(wormC));
			score += 4;
		} catch (MustNotImplementException exc) {
			max_score -= 4;
		}
	}

	@Test
	public void addWormsToTeam_NullSequenceOfWorms() {
		max_score += 1;
		try {
			facade.addWormsToTeam(theTeam, (Worm[]) null);
			fail();
		} catch (ModelException exc) {
			score += 1;
		} catch (MustNotImplementException exc) {
			max_score -= 1;
		}
	}

	@Test
	public void addWormsToTeam_WormWithSameNameAlreadyInTeam() {
		max_score += 3;
		try {
			facade.createWorm(theWorld, new double[] { 2.3, 7.0 }, 2.0, 2.0, "WormA", theTeam);
			Worm wormABis = facade.createWorm(otherWorld, new double[] { 2.5, 3.0 }, 2.0, 1.0, "WormA", null);
			facade.addWormsToTeam(theTeam, wormABis);
			fail();
		} catch (ModelException exc) {
			score += 3;
		} catch (MustNotImplementException exc) {
			max_score -= 3;
		}
	}

	@Test
	public void addWormsToTeam_WormsWitSameName() {
		max_score += 3;
		try {
			Worm wormA = facade.createWorm(theWorld, new double[] { 2.3, 7.0 }, 2.0, 2.0, "WormA", null);
			Worm wormABis = facade.createWorm(otherWorld, new double[] { 2.5, 3.0 }, 2.0, 1.0, "WormA", null);
			facade.addWormsToTeam(theTeam, wormA, wormABis);
			fail();
		} catch (ModelException exc) {
			assertEquals(0, facade.getNbWormsOfTeam(theTeam));
			score += 3;
		} catch (MustNotImplementException exc) {
			max_score -= 3;
		}
	}

	@Test
	public void addWormsToTeam_SameWormSeveralTimes() {
		max_score += 3;
		try {
			Worm wormA = facade.createWorm(theWorld, new double[] { 2.3, 7.0 }, 2.0, 2.0, "WormA", null);
			facade.addWormsToTeam(theTeam, wormA, wormA);
			// Accepting the worm once is OK.
			assertEquals(1, facade.getNbWormsOfTeam(theTeam));
			assertTrue(facade.getAllWormsOfTeam(theTeam).contains(wormA));
			score += 3;
		} catch (ModelException exc) {
			// Rejecting duplicate worms is also OK.
			score += 3;
		} catch (MustNotImplementException exc) {
			max_score -= 3;
		}
	}

	@Test
	public void addWormsToTeam_WormTooLight() {
		max_score += 4;
		Worm wormA = facade.createWorm(theWorld, new double[] { 2.3, 7.0 }, 2.0, 2.0, "WormA", theTeam);
		Worm wormB = facade.createWorm(otherWorld, new double[] { 2.5, 3.0 }, 2.0, 1.0, "WormB", null);
		try {
			facade.addWormsToTeam(theTeam, wormB);
			fail();
		} catch (ModelException exc) {
			score += 4;
		} catch (MustNotImplementException exc) {
			max_score -= 4;
		}
	}

	@Test
	public void addWormsToTeam_WormTooHeavy() {
		max_score += 4;
		Worm wormA = facade.createWorm(theWorld, new double[] { 2.3, 8.0 }, 2.0, 1.0, "WormA", theTeam);
		Worm wormB = facade.createWorm(otherWorld, new double[] { 2.5, 2.0 }, 2.0, 2.0, "WormB", null);
		try {
			facade.addWormsToTeam(theTeam, wormB);
			fail();
		} catch (ModelException exc) {
			score += 4;
		} catch (MustNotImplementException exc) {
			max_score -= 4;
		}
	}

	@Test
	public void addWormsToTeam_WormAlreadyInOtherTeam() {
		max_score += 2;
		try {
			Worm wormA = facade.createWorm(theWorld, new double[] { 2.3, 7.0 }, 2.0, 2.0, "WormA", theTeam);
			Team otherTeam = facade.createTeam(null, "OtherTeam");
			facade.addWormsToTeam(otherTeam, wormA);
			fail();
		} catch (ModelException exc) {
			score += 2;
		} catch (MustNotImplementException exc) {
			max_score -= 2;
		}
	}

	@Test
	public void addWormsToTeam_TerminatedWorm() {
		max_score += 2;
		try {
			Worm wormA = facade.createWorm(theWorld, new double[] { 2.3, 7.0 }, 2.0, 2.0, "WormA", null);
			facade.terminate(wormA);
			facade.addWormsToTeam(theTeam, wormA);
			fail();
		} catch (ModelException exc) {
			score += 2;
		} catch (MustNotImplementException exc) {
			max_score -= 2;
		}
	}

	@Test
	public void addWormsToTeam_NonEffectiveWorm() {
		max_score += 2;
		try {
			Worm wormA = facade.createWorm(theWorld, new double[] { 2.3, 7.0 }, 2.0, 2.0, "WormA", null);
			Worm[] wormsToAdd = new Worm[] { wormA, null };
			facade.addWormsToTeam(theTeam, wormsToAdd);
			fail();
		} catch (ModelException exc) {
			score += 2;
		} catch (MustNotImplementException exc) {
			max_score -= 2;
		}
	}

	@Test
	public void removeWormsFromTeam_SeveralWormsFromTeam() {
		max_score += 4;
		try {
			Worm wormA = facade.createWorm(theWorld, new double[] { 2.3, 7.0 }, 2.0, 2.0, "WormA", theTeam);
			Worm wormB = facade.createWorm(theWorld, new double[] { 6.8, 7.0 }, 2.0, 2.0, "WormB", theTeam);
			Worm wormC = facade.createWorm(theWorld, new double[] { 7.0, 2.5 }, 2.0, 2.0, "WormC", theTeam);
			facade.removeWormsFromTeam(theTeam, wormA, wormC);
			assertEquals(1, facade.getNbWormsOfTeam(theTeam));
			assertFalse(facade.getAllWormsOfTeam(theTeam).contains(wormA));
			assertNull(facade.getTeam(wormA));
			assertTrue(facade.getAllWormsOfTeam(theTeam).contains(wormB));
			assertFalse(facade.getAllWormsOfTeam(theTeam).contains(wormC));
			assertNull(facade.getTeam(wormC));
			score += 4;
		} catch (MustNotImplementException exc) {
			max_score -= 4;
		}
	}

	@Test
	public void removeWormsFromTeam_NoWorms() {
		max_score += 1;
		try {
			facade.createWorm(theWorld, new double[] { 2.3, 7.0 }, 2.0, 2.0, "WormA", theTeam);
			facade.createWorm(theWorld, new double[] { 6.8, 7.0 }, 2.0, 2.0, "WormB", theTeam);
			facade.createWorm(theWorld, new double[] { 7.0, 2.5 }, 2.0, 2.0, "WormC", theTeam);
			facade.removeWormsFromTeam(theTeam);
			assertEquals(3, facade.getNbWormsOfTeam(theTeam));
			score += 1;
		} catch (MustNotImplementException exc) {
			max_score -= 1;
		}
	}

	@Test
	public void removeWormsFromTeam_NullSequenceOfWorms() {
		max_score += 1;
		try {
			facade.createWorm(theWorld, new double[] { 2.3, 7.0 }, 2.0, 2.0, "WormA", theTeam);
			facade.removeWormsFromTeam(theTeam, (Worm[]) null);
			fail();
		} catch (ModelException exc) {
			score += 1;
		} catch (MustNotImplementException exc) {
			max_score -= 1;
		}
	}

	@Test
	public void removeWormsFromTeam_WormOfOtherTeam() {
		max_score += 4;
		Team otherTeam = null;
		try {
			otherTeam = facade.createTeam(null, "OtherTeam");
			Worm wormA = facade.createWorm(theWorld, new double[] { 2.3, 7.0 }, 2.0, 2.0, "WormA", theTeam);
			Worm wormB = facade.createWorm(theWorld, new double[] { 6.8, 7.0 }, 2.0, 2.0, "WormB", otherTeam);
			facade.createWorm(theWorld, new double[] { 7.0, 2.5 }, 2.0, 2.0, "WormC", otherTeam);
			facade.removeWormsFromTeam(otherTeam, wormB, wormA);
			fail();
		} catch (ModelException exc) {
			assertEquals(2, facade.getAllWormsOfTeam(otherTeam).size());
			score += 4;
		} catch (MustNotImplementException exc) {
			max_score -= 4;
		}
	}

	@Test
	public void removeWormsFromTeam_NonEffectiveWorm() {
		max_score += 4;
		try {
			Worm wormA = facade.createWorm(theWorld, new double[] { 2.3, 7.0 }, 2.0, 2.0, "WormA", theTeam);
			Worm wormB = facade.createWorm(theWorld, new double[] { 6.8, 7.0 }, 2.0, 2.0, "WormB", theTeam);
			Worm wormC = facade.createWorm(theWorld, new double[] { 7.0, 2.5 }, 2.0, 2.0, "WormC", theTeam);
			facade.removeWormsFromTeam(theTeam, new Worm[] { wormB, null, wormC });
			fail();
		} catch (ModelException exc) {
			assertEquals(3, facade.getAllWormsOfTeam(theTeam).size());
			score += 4;
		} catch (MustNotImplementException exc) {
			max_score -= 4;
		}
	}

	@Test
	public void mergeTeams_LegalCase() {
		max_score += 10;
		try {
			Worm wormA = facade.createWorm(theWorld, new double[] { 2.3, 7.0 }, 2.0, 2.0, "WormA", theTeam);
			Worm wormB = facade.createWorm(theWorld, new double[] { 6.8, 7.0 }, 2.0, 2.0, "WormB", theTeam);
			Team otherTeam = facade.createTeam(otherWorld, "OtherTeam");
			Worm wormC = facade.createWorm(theWorld, new double[] { 7.0, 2.5 }, 2.0, 2.0, "WormC", otherTeam);
			Worm wormD = facade.createWorm(otherWorld, new double[] { 2.5, 2.0 }, 2.0, 2.0, "WormD", otherTeam);
			facade.mergeTeams(theTeam, otherTeam);
			assertEquals(4, facade.getNbWormsOfTeam(theTeam));
			assertEquals(0, facade.getNbWormsOfTeam(otherTeam));
			assertTrue(facade.getAllWormsOfTeam(theTeam).contains(wormC));
			assertEquals(theTeam, facade.getTeam(wormC));
			assertTrue(facade.getAllWormsOfTeam(theTeam).contains(wormD));
			assertEquals(theTeam, facade.getTeam(wormD));
			score += 10;
		} catch (MustNotImplementException exc) {
			max_score -= 10;
		}
	}

	@Test
	public void mergeTeams_NameConflict() {
		max_score += 6;
		Team otherTeam = null;
		try {
			facade.createWorm(theWorld, new double[] { 2.3, 7.0 }, 2.0, 2.0, "WormA", theTeam);
			facade.createWorm(theWorld, new double[] { 6.8, 7.0 }, 2.0, 2.0, "WormB", theTeam);
			otherTeam = facade.createTeam(otherWorld, "OtherTeam");
			facade.createWorm(theWorld, new double[] { 7.0, 2.5 }, 2.0, 2.0, "WormA", otherTeam);
			facade.createWorm(otherWorld, new double[] { 2.5, 2.0 }, 2.0, 2.0, "WormD", otherTeam);
			facade.mergeTeams(theTeam, otherTeam);
			fail();
		} catch (ModelException exc) {
			assertEquals(2, facade.getAllWormsOfTeam(theTeam).size());
			assertEquals(2, facade.getAllWormsOfTeam(otherTeam).size());
			score += 6;
		} catch (MustNotImplementException exc) {
			max_score -= 6;
		}
	}

	@Test
	public void mergeTeams_NonEffectiveTeam() {
		max_score += 1;
		try {
			facade.mergeTeams(theTeam, null);
			fail();
		} catch (ModelException exc) {
			score += 1;
		} catch (MustNotImplementException exc) {
			max_score -= 1;
		}
	}

	/***************
	 * WIZARD TESTS
	 **************/

	@Test
	public void castSpell_NotEnoughGameObjects() {
		max_score += 2;
		try {
			facade.castSpell(theWorld);
			fail();
		} catch (ModelException exc) {
			score += 2;
		}
	}

	@Test
	public void castSpell_WormsSameTeam() {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, FIXTURE_DIRECTION, 1.5, "WormA", theTeam);
		BigInteger nbHitPoints1 = facade.getNbHitPoints(worm1);
		Worm worm2 = facade.createWorm(theWorld, new double[] { 6.0, 7.5 }, FIXTURE_DIRECTION, 1.5, "WormB", theTeam);
		BigInteger nbHitPoints2 = facade.getNbHitPoints(worm2);
		facade.castSpell(theWorld);
		BigInteger expectedNbHitPoints = nbHitPoints1.add(nbHitPoints2).divide(BigInteger.valueOf(2));
		assertEquals(expectedNbHitPoints, facade.getNbHitPoints(worm1));
		assertEquals(expectedNbHitPoints, facade.getNbHitPoints(worm2));
		score += 4;
	}

	@Test
	public void castSpell_WormsDifferentTeams() {
		max_score += 4;
		Worm largestWorm = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, FIXTURE_DIRECTION, 1.5, "WormA",
				theTeam);
		long nbActionPointsLargest = facade.getNbActionPoints(largestWorm);
		Worm smallestWorm = facade.createWorm(theWorld, new double[] { 6.0, 8.0 }, FIXTURE_DIRECTION, 1.0, "WormB",
				null);
		facade.decreaseNbActionPoints(smallestWorm, 10);
		long nbActionPointsSmallest = facade.getNbActionPoints(smallestWorm);
		facade.castSpell(theWorld);
		long actionPointsToTransfer = Math.min(nbActionPointsLargest, 5);
		assertEquals(nbActionPointsLargest - actionPointsToTransfer, facade.getNbActionPoints(largestWorm));
		assertEquals(nbActionPointsSmallest + actionPointsToTransfer, facade.getNbActionPoints(smallestWorm));
		score += 4;
	}

	@Test
	public void castSpell_WormsWithFood() {
		max_score += 4;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, FIXTURE_DIRECTION, 1.5, "WormA", theTeam);
		double[] location = new double[] { 8.795, 1.5 };
		Food theFood = facade.createFood(theWorld, location);
		facade.castSpell(theWorld);
		assertEquals(1.5 * 1.1, facade.getRadius(theWorm), EPS);
		assertFalse(facade.isTerminated(theFood));
		score += 4;
	}

	@Test
	public void castSpell_Food() {
		max_score += 4;
		Food food1 = facade.createFood(theWorld, new double[] { 8.795, 1.5 });
		Food food2 = facade.createFood(theWorld, new double[] { 8.795, 7.5 });
		facade.poison(food2);
		facade.castSpell(theWorld);
		assertTrue(facade.isPoisonous(food1));
		assertFalse(facade.isPoisonous(food2));
		score += 4;
	}

	@Test
	public void castSpell_WormWithProjectile() {
		max_score += 4;
		double[] worm_location = new double[] { 7.5, 3.505 };
		double worm_orientation = 3 * PI / 2.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		long oldNbActionPoints = facade.getNbActionPoints(theWorm);
		facade.fire(theWorm);
		facade.castSpell(theWorld);
		assertTrue(facade.getNbActionPoints(theWorm) < oldNbActionPoints);
		score += 4;
	}

	@Test
	public void castSpell_FoodWithProjectile() {
		max_score += 4;
		double[] worm_location = new double[] { 7.5, 3.505 };
		double worm_orientation = 3 * PI / 2.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		Projectile projectile = facade.fire(theWorm);
		facade.terminate(theWorm);
		double[] location = new double[] { 8.795, 1.5 };
		Food theFood = facade.createFood(theWorld, location);
		facade.castSpell(theWorld);
		assertTrue(facade.isTerminated(theFood));
		assertTrue(facade.isTerminated(projectile));
		score += 4;
	}

	@Test
	public void castSpell_Projectiles() {
		max_score += 4;
		double[] worm_location = new double[] { 7.5, 3.505 };
		double worm_orientation = 3 * PI / 2.0;
		double worm_radius = 1.5;
		Worm theWorm = facade.createWorm(theWorld, worm_location, worm_orientation, worm_radius, "Worm", null);
		Projectile projectile1 = facade.fire(theWorm);
		int oldNbHitPoints1 = facade.getNbHitPoints(projectile1);
		Projectile projectile2 = facade.fire(theWorm);
		int oldNbHitPoints2 = facade.getNbHitPoints(projectile2);
		facade.terminate(theWorm);
		facade.castSpell(theWorld);
		assertTrue((facade.getNbHitPoints(projectile1) == oldNbHitPoints1 + 2) || (oldNbHitPoints1 == 7)
				|| (oldNbHitPoints1 == 10));
		assertTrue((facade.getNbHitPoints(projectile2) == oldNbHitPoints2 + 2) || (oldNbHitPoints2 == 7)
				|| (oldNbHitPoints2 == 10));
		score += 4;
	}

	/****************
	 * PROGRAM TESTS
	 ***************/

	@Test
	public void loadProgram_ActiveGame() {
		max_score += 2;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 7.5, 3.505 }, 3 * PI / 2.0, 1.5, "Worm", null);
		facade.startGame(theWorld);
		String code = "print 2.0;";
		Program theProgram = ProgramParser.parseProgramFromString(code, programFactory);
		try {
			facade.loadProgramOnWorm(theWorm, theProgram, actionHandler);
			fail();
		} catch (ModelException exc) {
			score += 2;
		}
	}

	/******************
	 * STATEMENT TESTS
	 ******************/

	@Test
	public void testPrintStatement_LegalCase() throws ModelException {
		max_score += 2;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 7.5, 3.505 }, 3 * PI / 2.0, 1.5, "Worm", null);
		String code = "print 4.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(theWorm, program, actionHandler);
		List<Object> results = facade.executeProgram(theWorm);
		Object[] expecteds = { 4.0 };
		assertArrayEquals(expecteds, results.toArray());
		score += 2;
	}

	@Test
	public void testSequenceStatement_EmptyBody() throws ModelException {
		max_score += 3;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "{ }";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = {};
		assertArrayEquals(expecteds, results.toArray());
		score += 3;
	}

	@Test
	public void testSequenceStatement_NonNestedNonInterruptable() throws ModelException {
		max_score += 3;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "{ print 4.0; " + "print 12.0; }";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = { 4.0, 12.0 };
		assertArrayEquals(expecteds, results.toArray());
		score += 3;
	}

	@Test
	public void testSequenceStatement_NestedNonInterruptable() throws ModelException {
		max_score += 5;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "print 4.0; " + " { print 12.0; print 6.0; } print 15.0; ";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = { 4.0, 12.0, 6.0, 15.0 };
		assertArrayEquals(expecteds, results.toArray());
		score += 5;
	}

	@Test
	public void testSequenceStatement_NonNestedInterruptable() throws ModelException {
		max_score += 10;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.2, 1.5, "WormA", theTeam);
		facade.decreaseNbActionPoints(worm1, facade.getNbActionPoints(worm1));
		String code = "{ print 4.0; turn 1.0; print 12.0; }";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		assertNull(results);
		facade.decreaseNbActionPoints(worm1, -facade.getMaxNbActionPoints(worm1));
		results = facade.executeProgram(worm1);
		Object[] expecteds = { 4.0, 12.0 };
		assertArrayEquals(expecteds, results.toArray());
		assertEquals(1.2, facade.getOrientation(worm1), EPS);
		score += 10;
	}

	@Test
	public void testSequenceStatement_NestedInterruptable() throws ModelException {
		max_score += 15;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.2, 1.5, "WormA", theTeam);
		String code = "{ print 4.0; { print 3.0; turn 1.0; print 2.0; } turn 0.4; print 12.0; }";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		facade.decreaseNbActionPoints(worm1, facade.getNbActionPoints(worm1));
		List<Object> results = facade.executeProgram(worm1);
		assertNull(results);
		facade.decreaseNbActionPoints(worm1, -10);
		results = facade.executeProgram(worm1);
		assertNull(results);
		facade.decreaseNbActionPoints(worm1, -facade.getMaxNbActionPoints(worm1));
		results = facade.executeProgram(worm1);
		Object[] expecteds = { 4.0, 3.0, 2.0, 12.0 };
		assertArrayEquals(expecteds, results.toArray());
		assertEquals(1.6, facade.getOrientation(worm1), EPS);
		score += 15;
	}

	@Test
	public void testAssignmentStatement_NewVariable() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "{ varname := 7.0; print varname; }";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = { 7.0 };
		assertArrayEquals(expecteds, results.toArray());
		score += 4;
	}

	@Test
	public void testAssignmentStatement_ExistingVariable() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "{ varname := 7.0; varname := 12.0; print varname; }";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = { 12.0 };
		assertArrayEquals(expecteds, results.toArray());
		score += 4;
	}

	@Test
	public void testAssignmentStatement_AssignmentToProcedureName() throws ModelException {
		max_score += 10;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "def p: print 4.0; p := 7.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			facade.executeProgram(worm1);
			fail();
		} catch (ModelException exc) {
			score += 10;
		} catch (MustNotImplementException exc) {
			max_score -= 10;
		}
	}

	@Test
	public void testTurnStatement_LegalCase() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 1.0, 1.5, "WormA", theTeam);
		String code = "turn 0.5; ";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = {};
		assertArrayEquals(expecteds, results.toArray());
		assertEquals(1.5, facade.getOrientation(worm1), EPS);
		score += 4;
	}

	@Test
	public void testTurnStatement_NonDoubleAngle() throws ModelException {
		max_score += 2;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "turn null;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			facade.executeProgram(worm1);
			fail();
		} catch (ModelException exc) {
			score += 2;
		}
	}

	@Test
	public void testTurnStatement_AngleOutOfRange() throws ModelException {
		max_score += 5;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "turn 10.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			facade.executeProgram(worm1);
			fail();
		} catch (ModelException exc) {
			score += 5;
		}
	}

	@Test
	public void testTurnStatement_NotEnoughActionPoints() throws ModelException {
		max_score += 6;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		facade.decreaseNbActionPoints(worm1, facade.getNbActionPoints(worm1));
		String code = "turn 1.0; print 4.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		assertNull(results);
		assertEquals(0.0, facade.getOrientation(worm1), EPS);
		score += 6;
	}

	@Test
	public void testMoveStatement_WormMoving() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 7.5, 6.0 }, PI / 4.0, 1.5, "WormA", theTeam);
		String code = "move; ";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = {};
		assertArrayEquals(expecteds, results.toArray());
		assertNotEquals(7.5, facade.getLocation(worm1)[0], EPS);
		score += 4;
	}

	@Test
	public void testMoveStatement_WormKeepsPosition() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 7.5, 7.5 }, PI / 2.0, 1.5, "WormA", theTeam);
		String code = "move; ";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = {};
		assertArrayEquals(expecteds, results.toArray());
		assertEquals(7.5, facade.getLocation(worm1)[0], EPS);
		assertEquals(7.5, facade.getLocation(worm1)[1], EPS);
		score += 4;
	}

	@Test
	public void testMoveStatement_NotEnoughActionPoints() throws ModelException {
		max_score += 6;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 7.5, 2.0 }, PI / 4.0, 1.5, "WormA", theTeam);
		String code = "move; print 4.0; ";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		facade.decreaseNbActionPoints(worm1, facade.getNbActionPoints(worm1));
		List<Object> results = facade.executeProgram(worm1);
		assertNull(results);
		score += 6;
	}

	@Test
	public void testJumpStatement_WormJumping() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 7.5, 5.5 }, 7 * PI / 12.0, 1.5, "WormA", theTeam);
		String code = "jump; ";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = {};
		assertArrayEquals(expecteds, results.toArray());
		assertNotEquals(7.5, facade.getLocation(worm1)[0], EPS);
		assertNotEquals(5.5, facade.getLocation(worm1)[1], EPS);
		score += 4;
	}

	@Test
	public void testJumpStatement_NoActionPoints() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 7.5, 5.5 }, 7 * PI / 12.0, 1.5, "WormA", theTeam);
		String code = "jump; ";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		facade.decreaseNbActionPoints(worm1, facade.getNbActionPoints(worm1));
		List<Object> results = facade.executeProgram(worm1);
		assertNull(results);
		score += 4;
	}

	@Test
	public void testJumpStatement_OrientedDown() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 7.5, 5.5 }, 3 * PI / 2.0, 1.5, "WormA", theTeam);
		String code = "jump; ";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			facade.executeProgram(worm1);
			fail();
		} catch (ModelException exc) {
			score += 4;
		}
	}

	@Test
	public void testEatStatement_WormEating() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 7.5, 5.5 }, 7 * PI / 12.0, 1.5, "WormA", theTeam);
		Food food = facade.createFood(theWorld, new double[] {8.795,5.5});
		String code = "eat; ";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = {};
		assertArrayEquals(expecteds, results.toArray());
		assertTrue(facade.getRadius(worm1) > 1.5);
		assertNotEquals(7.5, facade.getLocation(worm1)[0], EPS);
		assertTrue(facade.isTerminated(food));
		score += 4;
	}

	@Test
	public void testEatStatement_NothingToEat() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 7.5, 5.5 }, 7 * PI / 12.0, 1.5, "WormA", theTeam);
		String code = "eat; ";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = {};
		assertArrayEquals(expecteds, results.toArray());
		assertEquals(1.5,facade.getRadius(worm1),EPS);
		score += 4;
	}

	@Test
	public void testEatStatement_NotEnoughActionPoints() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 7.5, 5.5 }, 7 * PI / 12.0, 1.5, "WormA", theTeam);
		String code = "eat; ";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		facade.decreaseNbActionPoints(worm1, facade.getNbActionPoints(worm1));
		List<Object> results = facade.executeProgram(worm1);
		assertNull(results);
		score += 4;
	}

	@Test
	public void testFireStatement_WormFiring() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 7.5, 5.5 }, PI, 1.5, "WormA", theTeam);
		String code = "fire; ";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = {};
		assertArrayEquals(expecteds, results.toArray());
		assertEquals(2,facade.getAllItems(theWorld).size());
		score += 4;
	}

	@Test
	public void testFireStatement_NotEnoughActionPoints() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 7.5, 5.5 }, 7 * PI / 12.0, 1.5, "WormA", theTeam);
		String code = "fire; ";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		facade.decreaseNbActionPoints(worm1, facade.getNbActionPoints(worm1));
		List<Object> results = facade.executeProgram(worm1);
		assertNull(results);
		score += 4;
	}

	@Test
	public void testIfStatement_NonBooleanCondition() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "if 3.0 print 4.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			facade.executeProgram(worm1);
			fail();
		} catch (ModelException exc) {
			score += 4;
		}
	}

	@Test
	public void testIfStatement_ThenPartNonInterruptable() throws ModelException {
		max_score += 6;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "print 1.0; if true { print 2.0; print 3.0; } print 4.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = { 1.0, 2.0, 3.0, 4.0 };
		assertArrayEquals(expecteds, results.toArray());
		score += 6;
	}

	@Test
	public void testIfStatement_ElsePartNonInterruptable() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "print 1.0; if false print 5.0; else { print 2.0; print 3.0; } print 4.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = { 1.0, 2.0, 3.0, 4.0 };
		assertArrayEquals(expecteds, results.toArray());
		score += 4;
	}

	@Test
	public void testIfStatement_ThenPartInterruptable() throws ModelException {
		max_score += 10;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		facade.decreaseNbActionPoints(worm1, facade.getNbActionPoints(worm1));
		String code = "print 1.0; if true { print 2.0; turn 1.5; print 3.0; } print 4.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.decreaseNbActionPoints(worm1, facade.getNbActionPoints(worm1));
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		assertNull(results);
		facade.decreaseNbActionPoints(worm1, -facade.getMaxNbActionPoints(worm1));
		results = facade.executeProgram(worm1);
		Object[] expecteds = { 1.0, 2.0, 3.0, 4.0 };
		assertArrayEquals(expecteds, results.toArray());
		assertEquals(1.5, facade.getOrientation(worm1), EPS);
		score += 10;
	}

	@Test
	public void testIfStatement_ConditionNotReEvaluated() throws ModelException {
		max_score += 10;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		facade.decreaseNbActionPoints(worm1, facade.getNbActionPoints(worm1));
		String code = "v := true; print 1.0; if v { print 2.0; v := false; turn 1.5; print 3.0; } print 4.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.decreaseNbActionPoints(worm1, facade.getNbActionPoints(worm1));
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		assertNull(results);
		facade.decreaseNbActionPoints(worm1, -facade.getMaxNbActionPoints(worm1));
		results = facade.executeProgram(worm1);
		Object[] expecteds = { 1.0, 2.0, 3.0, 4.0 };
		assertArrayEquals(expecteds, results.toArray());
		assertEquals(1.5, facade.getOrientation(worm1), EPS);
		score += 10;
	}

	@Test
	public void testIfStatement_ElsePartInterruptable() throws ModelException {
		max_score += 10;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		facade.decreaseNbActionPoints(worm1, facade.getNbActionPoints(worm1));
		String code = "v := false; print 1.0; if v print 22.0; { print 2.0; v := true; turn 1.5; print 3.0; } print 4.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.decreaseNbActionPoints(worm1, facade.getNbActionPoints(worm1));
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		assertNull(results);
		facade.decreaseNbActionPoints(worm1, -facade.getMaxNbActionPoints(worm1));
		results = facade.executeProgram(worm1);
		Object[] expecteds = { 1.0, 2.0, 3.0, 4.0 };
		assertArrayEquals(expecteds, results.toArray());
		assertEquals(1.5, facade.getOrientation(worm1), EPS);
		score += 10;
	}

	@Test
	public void testWhileStatement_NonBooleanCondition() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "while 3.0 print 4.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			facade.executeProgram(worm1);
			fail();
		} catch (ModelException exc) {
			score += 4;
		}
	}

	@Test
	public void testWhileStatement_ConditionTypeChanges() throws ModelException {
		max_score += 8;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "v := true; while v v := null;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			facade.executeProgram(worm1);
			fail();
		} catch (ModelException exc) {
			score += 8;
		}
	}

	@Test
	public void testWhileSatement_BodyNonInterruptable() throws ModelException {
		max_score += 18;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "print 1.0; v := 1.0; while v < 8.5 { v := v + 1.0; print v; } print 10.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0 };
		assertArrayEquals(expecteds, results.toArray());
		score += 18;
	}

	@Test
	public void testWhileSatement_BodyInterruptable() throws ModelException {
		max_score += 25;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "print 1.0; v := 0.0; while v < 0.45 { v := v + 0.1; print v; turn v; } print 1.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		facade.decreaseNbActionPoints(worm1, facade.getNbActionPoints(worm1));
		List<Object> results = facade.executeProgram(worm1);
		assertNull(results);
		// Finishing the first two iterations.
		facade.decreaseNbActionPoints(worm1, -3);
		results = facade.executeProgram(worm1);
		assertNull(results);
		// Finishing the next two iterations.
		facade.decreaseNbActionPoints(worm1, -7);
		results = facade.executeProgram(worm1);
		assertNull(results);
		// Finishing program execution.
		facade.decreaseNbActionPoints(worm1, -facade.getMaxNbActionPoints(worm1));
		List<Object> finalResults = facade.executeProgram(worm1);
		double[] expecteds = { 1.0, 0.1, 0.2, 0.3, 0.4, 0.5, 1.0 };
		IntStream.range(0, expecteds.length)
				.forEach(i -> org.junit.Assert.assertEquals(expecteds[i], (Double) finalResults.get(i), EPS));
		score += 25;
	}

	@Test
	public void testBreakSatement_DirectlyInWhileBody() throws ModelException {
		max_score += 14;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "print 1.0; v := 1.0; while true { v := v + 1.0; print v; if 4.5 < v break; } print 10.0;";
		try {
			Program program = ProgramParser.parseProgramFromString(code, programFactory);
			facade.loadProgramOnWorm(worm1, program, actionHandler);
			List<Object> results = facade.executeProgram(worm1);
			Object[] expecteds = { 1.0, 2.0, 3.0, 4.0, 5.0, 10.0 };
			assertArrayEquals(expecteds, results.toArray());
			score += 14;
		} catch (MustNotImplementException exc) {
			max_score -= 14;
		}
	}

	@Test
	public void testBreakSatement_IndirectlyInWhileBody() throws ModelException {
		max_score += 20;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "print 1.0; v := 1.0; while true { v := v + 1.0; { print v; if 4.5 < v break; print 0.0;} } print 10.0;";
		try {
			Program program = ProgramParser.parseProgramFromString(code, programFactory);
			facade.loadProgramOnWorm(worm1, program, actionHandler);
			List<Object> results = facade.executeProgram(worm1);
			Object[] expecteds = { 1.0, 2.0, 0.0, 3.0, 0.0, 4.0, 0.0, 5.0, 10.0 };
			assertArrayEquals(expecteds, results.toArray());
			score += 20;
		} catch (MustNotImplementException exc) {
			max_score -= 20;
		}
	}

	@Test
	public void testBreakStatement_OutOfScope() throws ModelException {
		max_score += 8;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "v := true; while v {print v; v := false; } break;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			facade.executeProgram(worm1);
			fail();
		} catch (ModelException exc) {
			score += 8;
		}
	}

	@Test
	public void testBreakStatement_DirectlyInProcedure() throws ModelException {
		max_score += 12;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "def p: { print 10.0; break; print 15.0; } print 0.0; invoke p; print 20.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			List<Object> results = facade.executeProgram(worm1);
			Object[] expecteds = { 0.0, 10.0, 20.0};
			assertArrayEquals(expecteds, results.toArray());
			score += 12;
		} catch (MustNotImplementException exc) {
			max_score -= 12;
		}
	}

	@Test
	public void testBreakStatement_NestedBreaks() throws ModelException {
		max_score += 20;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "def p: { print 1.0; while true { print 2.0; break; } print 3.0; break; print 4.0;} def g: { print 10.0; invoke p; print 20.0; break; print 30.0; } print 0.0; invoke g; print 100.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			List<Object> results = facade.executeProgram(worm1);
			Object[] expecteds = { 0.0, 10.0, 1.0, 2.0, 3.0, 20.0, 100.0};
			assertArrayEquals(expecteds, results.toArray());
			score += 20;
		} catch (MustNotImplementException exc) {
			max_score -= 20;
		}
	}

	@Test
	public void testInvokeStatement_UndefinedProcedure() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "invoke p;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			facade.executeProgram(worm1);
			fail();
		} catch (ModelException exc) {
			score += 4;
		} catch (MustNotImplementException exc) {
			max_score -= 4;
		}
	}

	@Test
	public void testInvokeStatement_SimpleProcedure() throws ModelException {
		max_score += 6;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "def p: print 4.0; print 1.0; invoke p; print 7.0; invoke p; print 10.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			List<Object> results = facade.executeProgram(worm1);
			Object[] expecteds = { 1.0, 4.0, 7.0, 4.0, 10.0};
			assertArrayEquals(expecteds, results.toArray());
			score += 6;
		} catch (MustNotImplementException exc) {
			max_score -= 6;
		}
	}

	@Test
	public void testInvokeStatement_SeveralDefsOfProcedure() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "def p: print 4.0; def p: print 8.0; print 1.0; invoke p; print 7.0; ";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			List<Object> results = facade.executeProgram(worm1);
			Object[] expecteds = { 1.0, 8.0, 7.0 };
			assertArrayEquals(expecteds, results.toArray());
			score += 4;
		} catch (MustNotImplementException exc) {
			max_score -= 4;
		}
	}

	@Test
	public void testInvokeStatement_ProcedureIntroduingGlobalVariable() throws ModelException {
		max_score += 8;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "def p: a := 10; invoke p; print a;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			List<Object> results = facade.executeProgram(worm1);
			Object[] expecteds = { 10.0};
			assertArrayEquals(expecteds, results.toArray());
			score += 8;
		} catch (MustNotImplementException exc) {
			max_score -= 8;
		}
	}

	@Test
	public void testInvokeStatement_ProcedureChangingGlobalVariable() throws ModelException {
		max_score += 10;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "def p: a := a + 10.0; a := 3; invoke p; print a;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			List<Object> results = facade.executeProgram(worm1);
			Object[] expecteds = { 13.0};
			assertArrayEquals(expecteds, results.toArray());
			score += 10;
		} catch (MustNotImplementException exc) {
			max_score -= 10;
		}
	}

	@Test
	public void testInvokeStatement_ProcedureInvokingOtherProcedure() throws ModelException {
		max_score += 10;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "def p: print 1.0; def g: { print 2.0; invoke p; print 3.0; } invoke g; ";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			List<Object> results = facade.executeProgram(worm1);
			Object[] expecteds = { 2.0, 1.0, 3.0};
			assertArrayEquals(expecteds, results.toArray());
			score += 10;
		} catch (MustNotImplementException exc) {
			max_score -= 10;
		}
	}

	@Test
	public void testInvokeStatement_RecursiveProcedure() throws ModelException {
		max_score += 35;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "def p: { print a; if a < 5.5 { a := a + 1.0; invoke p; } } a := 0.0; invoke p; print a + 1.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			List<Object> results = facade.executeProgram(worm1);
			Object[] expecteds = { 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0};
			assertArrayEquals(expecteds, results.toArray());
			score += 35;
		} catch (MustNotImplementException exc) {
			max_score -= 35;
		}
	}

	@Test
	public void testInvokeStatement_Interrupted() throws ModelException {
		max_score += 25;
		//Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		double[] worm_location = new double[] { 7.5, 1.5 };
		double worm_orientation = 2 * PI / 3.0;
		Worm worm1 = facade.createWorm(theWorld, worm_location,worm_orientation, 1.5, "WormA", theTeam);
		String code = "def p: { print a; a := a + 5.0; jump; print a; } a := 10.0; invoke p; print a + 1.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		facade.decreaseNbActionPoints(worm1, facade.getNbActionPoints(worm1));
		try {
			List<Object> results = facade.executeProgram(worm1);
			assertNull(results);
			facade.decreaseNbActionPoints(worm1, -facade.getMaxNbActionPoints(worm1));
			results = facade.executeProgram(worm1);
			Object[] expecteds = { 10.0, 15.0, 16.0};
			assertArrayEquals(expecteds, results.toArray());
			score += 25;
		} catch (MustNotImplementException exc) {
			max_score -= 25;
		}
	}

	// break statement
	// nested break statements
	
	// Assignment: assignment to variable with same name as procedure.

	/*******************
	 * EXPRESSION TESTS
	 *******************/

	@Test
	public void testReadVariable_LegalCase() throws ModelException {
		max_score += 10;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 7.5, 3.505 }, 3 * PI / 2.0, 1.5, "Worm", null);
		String code = "v := 7.0; print v;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(theWorm, program, actionHandler);
		List<Object> results = facade.executeProgram(theWorm);
		Object[] expecteds = { 7.0 };
		assertArrayEquals(expecteds, results.toArray());
		score += 10;
	}

	@Test
	public void testReadVariable_IllegalCase() throws ModelException {
		max_score += 10;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 7.5, 3.505 }, 3 * PI / 2.0, 1.5, "Worm", null);
		String code = "print v;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(theWorm, program, actionHandler);
		try {
			facade.executeProgram(theWorm);
			fail();
		} catch (ModelException exc) {
			score += 10;
		}
	}

	@Test
	public void testBooleanLiteral() throws ModelException {
		max_score += 2;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 7.5, 3.505 }, 3 * PI / 2.0, 1.5, "Worm", null);
		String code = "print true;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(theWorm, program, actionHandler);
		List<Object> results = facade.executeProgram(theWorm);
		Object[] expecteds = { true };
		assertArrayEquals(expecteds, results.toArray());
		score += 2;
	}

	@Test
	public void testNull() throws ModelException {
		max_score += 2;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 7.5, 3.505 }, 3 * PI / 2.0, 1.5, "Worm", null);
		String code = "print null;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(theWorm, program, actionHandler);
		List<Object> results = facade.executeProgram(theWorm);
		Object[] expecteds = { null };
		assertArrayEquals(expecteds, results.toArray());
		score += 2;
	}

	@Test
	public void testSelf() throws ModelException {
		max_score += 2;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 7.5, 3.505 }, 3 * PI / 2.0, 1.5, "Worm", null);
		String code = "print self;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(theWorm, program, actionHandler);
		List<Object> results = facade.executeProgram(theWorm);
		Object[] expecteds = { theWorm };
		assertArrayEquals(expecteds, results.toArray());
		score += 2;
	}

	@Test
	public void testAddition_LegalCase() throws ModelException {
		max_score += 2;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 7.5, 3.505 }, 3 * PI / 2.0, 1.5, "Worm", null);
		String code = "print 2.0 + 4.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(theWorm, program, actionHandler);
		List<Object> results = facade.executeProgram(theWorm);
		Object[] expecteds = { 6.0 };
		assertArrayEquals(expecteds, results.toArray());
		score += 2;
	}

	@Test
	public void testAddition_IllegalCase() throws ModelException {
		max_score += 6;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 7.5, 3.505 }, 3 * PI / 2.0, 1.5, "Worm", null);
		String code = "print 2.0 + self;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(theWorm, program, actionHandler);
		try {
			facade.executeProgram(theWorm);
			fail();
		} catch (ModelException exc) {
			score += 6;
		}
	}

	@Test
	public void testConjunction_LegalCase() throws ModelException {
		max_score += 2;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 7.5, 3.505 }, 3 * PI / 2.0, 1.5, "Worm", null);
		String code = "print true && false;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(theWorm, program, actionHandler);
		List<Object> results = facade.executeProgram(theWorm);
		Object[] expecteds = { false };
		assertArrayEquals(expecteds, results.toArray());
		score += 2;
	}

	@Test
	public void testNegation_LegalCase() throws ModelException {
		max_score += 2;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 7.5, 3.505 }, 3 * PI / 2.0, 1.5, "Worm", null);
		String code = "print ! true;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(theWorm, program, actionHandler);
		List<Object> results = facade.executeProgram(theWorm);
		Object[] expecteds = { false };
		assertArrayEquals(expecteds, results.toArray());
		score += 2;
	}

	@Test
	public void testEquality_OperandsOfSameType() throws ModelException {
		max_score += 2;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 7.5, 3.505 }, 3 * PI / 2.0, 1.5, "Worm", null);
		String code = "print 2.0 == 2.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(theWorm, program, actionHandler);
		List<Object> results = facade.executeProgram(theWorm);
		Object[] expecteds = { true };
		assertArrayEquals(expecteds, results.toArray());
		score += 2;
	}

	@Test
	public void testEquality_OperandsOfDifferentType() throws ModelException {
		max_score += 2;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 7.5, 3.505 }, 3 * PI / 2.0, 1.5, "Worm", null);
		String code = "print true == self;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(theWorm, program, actionHandler);
		List<Object> results = facade.executeProgram(theWorm);
		Object[] expecteds = { false };
		assertArrayEquals(expecteds, results.toArray());
		score += 2;
	}

	@Test
	public void testEquality_LeftOperandNull() throws ModelException {
		max_score += 2;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 7.5, 3.505 }, 3 * PI / 2.0, 1.5, "Worm", null);
		String code = "print null == self;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(theWorm, program, actionHandler);
		List<Object> results = facade.executeProgram(theWorm);
		Object[] expecteds = { false };
		assertArrayEquals(expecteds, results.toArray());
		score += 2;
	}

	@Test
	public void testInEquality_OperandsOfProperType() throws ModelException {
		max_score += 2;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 7.5, 3.505 }, 3 * PI / 2.0, 1.5, "Worm", null);
		String code = "print 2.0 < 1.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(theWorm, program, actionHandler);
		List<Object> results = facade.executeProgram(theWorm);
		Object[] expecteds = { false };
		assertArrayEquals(expecteds, results.toArray());
		score += 2;
	}

	@Test
	public void testInEquality_IllegalCase() throws ModelException {
		max_score += 2;
		Worm theWorm = facade.createWorm(theWorld, new double[] { 7.5, 3.505 }, 3 * PI / 2.0, 1.5, "Worm", null);
		String code = "print 2.0 < self;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(theWorm, program, actionHandler);
		try {
			facade.executeProgram(theWorm);
			fail();
		} catch (ModelException exc) {
			score += 2;
		}
	}

	@Test
	public void testSearchObject_SingleObject() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", null);
		Food foodToSearch = facade.createFood(theWorld, new double[] { 8.795, 7.5 });
		String code = "print searchobj 0.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = { foodToSearch };
		assertArrayEquals(expecteds, results.toArray());
		score += 4;
	}

	@Test
	public void testSearchObject_SeveralCandidateObjects() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", null);
		Worm wormToSearch = facade.createWorm(theWorld, new double[] { 6.0, 7.5 }, FIXTURE_DIRECTION, 1.5, "WormB",
				null);
		facade.createFood(theWorld, new double[] { 8.795, 7.5 });
		String code = "print searchobj 0.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = { wormToSearch };
		assertArrayEquals(expecteds, results.toArray());
		score += 4;
	}

	@Test
	public void testSearchObject_NoObject() throws ModelException {
		max_score += 2;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 3 * PI / 2.0, 1.5, "WormA", null);
		facade.createWorm(theWorld, new double[] { 6.0, 7.5 }, FIXTURE_DIRECTION, 1.5, "WormB", null);
		String code = "print searchobj 0.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = { null };
		assertArrayEquals(expecteds, results.toArray());
		score += 2;
	}

	@Test
	public void testSearchObject_ObjectBehindImpassableTerrain() {
		max_score += 5;
		map10x10 = new boolean[][] { { false, true, true, true, true, true, true, true, true, false },
				{ true, false, true, true, false, false, true, true, true, false },
				{ true, true, false, true, true, true, true, true, true, false },
				{ true, true, true, false, true, true, true, true, true, false },
				{ true, true, true, true, false, true, true, true, false, false },
				{ true, true, true, true, true, false, true, true, true, false },
				{ true, true, true, true, true, true, false, true, true, false },
				{ true, true, true, true, true, true, true, false, true, false },
				{ false, true, true, true, true, true, true, true, false, false },
				{ false, false, true, true, true, true, true, true, true, false }, };
		World otherWorld = facade.createWorld(10.0, 10.0, map10x10);
		Worm theWorm = facade.createWorm(otherWorld, new double[] { 2.0, 2.0 }, PI / 4.0, 1.0, "WormA", null);
		Worm wormToSearch = facade.createWorm(otherWorld, new double[] { 8.0, 8.0 }, 0.0, 1.0, "WormB", null);
		String code = "print searchobj 0.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(theWorm, program, actionHandler);
		List<Object> results = facade.executeProgram(theWorm);
		Object[] expecteds = { wormToSearch };
		assertArrayEquals(expecteds, results.toArray());
		score += 5;
	}

	@Test
	public void testSameTeam_TrueCase() throws ModelException {
		max_score += 5;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		facade.createWorm(theWorld, new double[] { 6.0, 7.5 }, FIXTURE_DIRECTION, 1.5, "WormB", theTeam);
		String code = "print sameteam searchobj 0.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			List<Object> results = facade.executeProgram(worm1);
			Object[] expecteds = { true };
			assertArrayEquals(expecteds, results.toArray());
			score += 5;
		} catch (MustNotImplementException exc) {
			max_score -= 5;
		}
	}

	@Test
	public void testSameTeam_FalseCase() throws ModelException {
		max_score += 3;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		facade.createWorm(theWorld, new double[] { 6.0, 7.5 }, FIXTURE_DIRECTION, 1.5, "WormB", null);
		String code = "print sameteam searchobj 0.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			List<Object> results = facade.executeProgram(worm1);
			Object[] expecteds = { false };
			assertArrayEquals(expecteds, results.toArray());
			score += 3;
		} catch (MustNotImplementException exc) {
			max_score -= 3;
		}
	}

	@Test
	public void testSameTeam_NullCase() throws ModelException {
		max_score += 3;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "print sameteam null;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			List<Object> results = facade.executeProgram(worm1);
			Object[] expecteds = { false };
			assertArrayEquals(expecteds, results.toArray());
			score += 3;
		} catch (MustNotImplementException exc) {
			max_score -= 3;
		}
	}

	@Test
	public void testSameTeam_IllegalCase() throws ModelException {
		max_score += 4;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", null);
		facade.createFood(theWorld, new double[] { 8.795, 7.5 });
		String code = "print sameteam searchobj 0.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			facade.executeProgram(worm1);
			fail();
		} catch (ModelException exc) {
			score += 4;
		} catch (MustNotImplementException exc) {
			max_score -= 4;
		}
	}

	@Test
	public void testDistance_LegalCase() throws ModelException {
		max_score += 5;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		facade.createWorm(theWorld, new double[] { 6.0, 7.5 }, FIXTURE_DIRECTION, 1.5, "WormB", theTeam);
		String code = "print distance searchobj 0.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = { 1.0 };
		assertArrayEquals(expecteds, results.toArray());
		score += 5;
	}

	@Test
	public void testDistance_NullCase() throws ModelException {
		max_score += 2;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "print distance null;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			facade.executeProgram(worm1);
			fail();
		} catch (ModelException exc) {
			score += 2;
		}
	}

	@Test
	public void testDistance_IllegalCase() throws ModelException {
		max_score += 2;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "print distance 3.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		try {
			facade.executeProgram(worm1);
			fail();
		} catch (ModelException exc) {
			score += 2;
		}
	}

	@Test
	public void testIsWorm_TrueCase() throws ModelException {
		max_score += 2;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		facade.createWorm(theWorld, new double[] { 6.0, 7.5 }, FIXTURE_DIRECTION, 1.5, "WormB", null);
		String code = "print isworm searchobj 0.0;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = { true };
		assertArrayEquals(expecteds, results.toArray());
		score += 2;
	}

	@Test
	public void testIsWorm_FalseCase() throws ModelException {
		max_score += 2;
		Worm worm1 = facade.createWorm(theWorld, new double[] { 2.0, 7.5 }, 0.0, 1.5, "WormA", theTeam);
		String code = "print isworm 3.14;";
		Program program = ProgramParser.parseProgramFromString(code, programFactory);
		facade.loadProgramOnWorm(worm1, program, actionHandler);
		List<Object> results = facade.executeProgram(worm1);
		Object[] expecteds = { false };
		assertArrayEquals(expecteds, results.toArray());
		score += 2;
	}

}
