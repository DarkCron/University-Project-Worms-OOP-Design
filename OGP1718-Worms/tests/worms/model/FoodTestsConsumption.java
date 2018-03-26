package worms.model;

import static org.junit.Assert.*;

import org.junit.Test;

import worms.model.values.Direction;
import worms.model.values.Location;
import worms.model.values.Name;
import worms.model.values.Radius;

public class FoodTestsConsumption {
	
	private static final double EPS = 1e-4;
	
	@Test
	public void test() {
		World world = new World(passableMapAdjacent);
		world.addGameObject(new Food(new Location(3,3), new Radius(1), world));
		Worm w = new Worm(new Location(0, 0), Direction.DEFAULT_DIRECTION, world, new Radius(1), Name.DEFAULT_NAME);
		world.addGameObject(w);
		assertTrue(world.getAllGameObjects().size() == 2);
		w.setLocation(new Location(2, 3));
		assertFalse(world.getAllGameObjects().size() == 2);
		assertEquals(1.1d, w.getRadius().getRadius(), EPS);
	}

	private boolean[][] passableMapAdjacent = new boolean[][] { //
			{ true, true, true, true }, //
			{ true, true, true, true }, //
			{ true, true, true, true }, //
			{ false, false, false, false } };	
	
}
