package worms.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import worms.exceptions.InvalidLocationException;
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
		
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGOID() {
		World w = new World(4, 4, passableMap);
		Food f = new Food(Location.ORIGIN, Radius.DEFAULT_RADIUS, w);
		assertTrue(GameObjectTypeID.typeExists(f.getClass()));
		Worm wo = new Worm(Location.ORIGIN, Direction.DEFAULT_DIRECTION,  w, Radius.DEFAULT_RADIUS,Name.DEFAULT_NAME);
		assertTrue(GameObjectTypeID.typeExists(wo.getClass()));
	}
	
	@Test
	public void testWorldOutOfBounds() {
		try {
			World w = new World(4, 4, passableMap);
			Worm wo = new Worm(Location.ORIGIN, Direction.DEFAULT_DIRECTION,  w, Radius.DEFAULT_RADIUS,Name.DEFAULT_NAME);
			w.addGameObject(wo);
			assertTrue(w.hasGameObject(wo));
			wo.setLocation(new Location(20,20));
			
			assertTrue(wo.isTerminated());
			assertTrue(wo.getWorld() == null);
			assertFalse(w.hasGameObject(wo)); //worm is out of bounds, therefore not in game anymore
		} catch (Exception e) {
			//assertTrue(e.getClass() == InvalidLocationException.class);
		}
		
	}
	
	@Test
	public void testBaseWorld() {
		World w = new World(4, 4, passableMap);
		assertEquals(true, World.isValidWorldSize(w.getWorldHeight(), 4d));
		assertEquals(true, World.isValidWorldSize(w.getWorldWidth(), 4));
	}
	
	@Test
	public void worldAddGameObjectsTest() {
		World w = new World(4, 4, passableMap);
		w.addGameObject(new Food(Location.ORIGIN, Radius.DEFAULT_RADIUS, w));
		w.addGameObject(new Food(Location.ORIGIN, Radius.DEFAULT_RADIUS, w));
		w.addGameObject(new Food(Location.ORIGIN, Radius.DEFAULT_RADIUS, w));
		
		assertEquals(1, w.getWorldObjects().size());
		
		w.addGameObject(new Worm(Location.ORIGIN, Direction.DEFAULT_DIRECTION,  w, Radius.DEFAULT_RADIUS,Name.DEFAULT_NAME));
		w.addGameObject(new Worm(Location.ORIGIN, Direction.DEFAULT_DIRECTION,  w, Radius.DEFAULT_RADIUS,Name.DEFAULT_NAME));
		w.addGameObject(new Worm(Location.ORIGIN, Direction.DEFAULT_DIRECTION,  w, Radius.DEFAULT_RADIUS,Name.DEFAULT_NAME));
		
		assertEquals(2, w.getWorldObjects().size());
		assertEquals(3, w.getAllObjectsOfType(Worm.class).size());
		assertEquals(3, w.getAllObjectsOfType(Food.class).size());
	}

}
