package worms.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import worms.model.values.Direction;
import worms.model.values.Location;
import worms.model.values.Name;
import worms.model.values.Radius;

public class WorldTest {

	static World w;
	
	@Before
	public void setUp() throws Exception {
		w = new World(0, 0, null);
	}

	@Test
	public void worldAddGameObjectsTest() {
		World w = new World(0, 0, null);
		w.addGameObject(new Food(Location.ORIGIN, Radius.DEFAULT_RADIUS, w));
		w.addGameObject(new Food(Location.ORIGIN, Radius.DEFAULT_RADIUS, w));
		w.addGameObject(new Food(Location.ORIGIN, Radius.DEFAULT_RADIUS, w));
		
		assertEquals(1, w.getWorldObjects().size());
		
		w.addGameObject(new Worm(Location.ORIGIN, Direction.DEFAULT_DIRECTION,  w, Radius.DEFAULT_RADIUS,Name.DEFAULT_NAME));
		w.addGameObject(new Worm(Location.ORIGIN, Direction.DEFAULT_DIRECTION,  w, Radius.DEFAULT_RADIUS,Name.DEFAULT_NAME));
		w.addGameObject(new Worm(Location.ORIGIN, Direction.DEFAULT_DIRECTION,  w, Radius.DEFAULT_RADIUS,Name.DEFAULT_NAME));
		
		assertEquals(2, w.getWorldObjects().size());
	}

}
