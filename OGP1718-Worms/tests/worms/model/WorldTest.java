package worms.model;

import static org.junit.Assert.*;

import java.io.Console;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import worms.exceptions.InvalidLocationException;
import worms.model.ShapeHelp.Rectangle;
import worms.model.values.Direction;
import worms.model.values.GameObjectTypeID;
import worms.model.values.Location;
import worms.model.values.Name;
import worms.model.values.Radius;

public class WorldTest {

	private boolean[][] passableMap = new boolean[][] { //
			{ false, false, false, false }, //
			{ true, true, true, true }, //
			{ true, true, true, true }, //
			{ false, false, false, false } };

	private boolean[][] passableMapAdjacent = new boolean[][] { //
			{ true, true, true, true }, //
			{ true, true, true, true }, //
			{ true, true, true, true }, //
			{ false, false, false, false } };

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAdjacent() {
		World w = new World(passableMapAdjacent);
		assertFalse(w.isAdjacantToImpassableTerrain(Location.ORIGIN, new Radius(1)));
		assertTrue(w.isAdjacantToImpassableTerrain(new Location(2, 1), new Radius(2d)));
		assertTrue(w.isAdjacantToImpassableTerrain(new Location(0, 1), new Radius(2d)));
		assertFalse(w.isAdjacantToImpassableTerrain(new Location(3, 3), new Radius(2d)));
	}
	
	@Test
	public void testGOID() {
		World w = new World(4, 4, passableMap);
		Food f = new Food(Location.ORIGIN, Radius.DEFAULT_RADIUS, w);
		assertTrue(GameObjectTypeID.typeExists(f.getClass()));
		Worm wo = new Worm(Location.ORIGIN, Direction.DEFAULT_DIRECTION, w, Radius.DEFAULT_RADIUS, Name.DEFAULT_NAME,null);
		assertTrue(GameObjectTypeID.typeExists(wo.getClass()));
	}

	@Test
	public void testWorldOutOfBounds() {
		try {
			World w = new World(4, 4, passableMap);
			Worm wo = new Worm(Location.ORIGIN, Direction.DEFAULT_DIRECTION, w, Radius.DEFAULT_RADIUS,
					Name.DEFAULT_NAME,null);
			w.addGameObject(wo);
			assertTrue(w.hasGameObject(wo));
			wo.setLocation(new Location(20, 20));

			assertTrue(wo.isTerminated());
			assertTrue(wo.getWorld() == null);
			assertFalse(w.hasGameObject(wo)); // worm is out of bounds, therefore not in game anymore
		} catch (Exception e) {
			// assertTrue(e.getClass() == InvalidLocationException.class);
		}

	}

	@Test
	public void testBaseWorld() {
		World w = new World(4, 4, passableMap);
//		assertEquals(true, World.isValidWorldSize(w.getWorldHeight(), 4d));
//		assertEquals(true, World.isValidWorldSize(w.getWorldWidth(), 4));
	}

	@Test
	public void worldAddGameObjectsTest() {
		World w = new World(4, 4, passableMap);
		w.addGameObject(new Food(Location.ORIGIN, Radius.DEFAULT_RADIUS, w));
		w.addGameObject(new Food(Location.ORIGIN, Radius.DEFAULT_RADIUS, w));
		w.addGameObject(new Food(Location.ORIGIN, Radius.DEFAULT_RADIUS, w));

		assertEquals(1, w.getWorldObjects().size());

		w.addGameObject(
				new Worm(Location.ORIGIN, Direction.DEFAULT_DIRECTION, w, Radius.DEFAULT_RADIUS, Name.DEFAULT_NAME,null));
		w.addGameObject(
				new Worm(Location.ORIGIN, Direction.DEFAULT_DIRECTION, w, Radius.DEFAULT_RADIUS, Name.DEFAULT_NAME,null));
		w.addGameObject(
				new Worm(Location.ORIGIN, Direction.DEFAULT_DIRECTION, w, Radius.DEFAULT_RADIUS, Name.DEFAULT_NAME,null));

		assertEquals(2, w.getWorldObjects().size());
		assertEquals(3, w.getAllObjectsOfType(Worm.class).size());
		assertEquals(3, w.getAllObjectsOfType(Food.class).size());
	}

	@Test
	public void worldGetPartialMapTest() {
		World w = new World(passableMap);
		boolean[][] passableMapTest1 = new boolean[][] { //
				{ false, false, false, false }, //
				{ true, true, true, true }, //
				{ true, true, true, true }, //
				{ false, false, false, false } };

		boolean[][] passableMapTest2 = new boolean[][] { //
				{ false, false }, //
				{ true, true } };

		boolean[][] passableMapTest3 = new boolean[][] { //
				{ true, true, true, true }, //
				{ true, true, true, true } };

		Rectangle r = new Rectangle(new Location(0, 0), new Location(2, 2));

//		assertTrue(Arrays.deepEquals(r.getArraySetFromSizeAndPassableMap(passableMap), passableMapTest2));
//
//		r = new Rectangle(new Location(0, 0), new Location(4, 4));
//
//		assertTrue(Arrays.deepEquals(r.getArraySetFromSizeAndPassableMap(passableMap), passableMapTest1));
//
//		r = new Rectangle(new Location(-2, -2), new Location(6, 6));
//
//		assertTrue(Arrays.deepEquals(r.getArraySetFromSizeAndPassableMap(passableMap), passableMapTest1));
//
//		r = new Rectangle(new Location(0, 1), new Location(4, 2));
//
//		assertTrue(Arrays.deepEquals(r.getArraySetFromSizeAndPassableMap(passableMap), passableMapTest3));
//
//		r = new Rectangle(new Location(-5, -5), new Location(4, 2));
//
//		assertTrue(Arrays.deepEquals(r.getArraySetFromSizeAndPassableMap(passableMap), new double[0][]));
	}
}
