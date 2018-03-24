package worms.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import worms.model.ShapeHelp.Circle;
import worms.model.values.Location;
import worms.model.values.Radius;

public class HelperTests {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		Circle c = new Circle(Location.ORIGIN, new Radius(5));
		assertFalse(c.Contains(new Location(50,0)));
		assertFalse(c.Contains(new Location(0,50)));
		assertFalse(c.Contains(new Location(50,50)));
		assertTrue(c.Contains(Location.ORIGIN));
		assertTrue(c.Contains(new Location(0.5, 3)));
		
		c.setCenter(new Location(20,20));
		assertTrue(c.Contains(new Location(22,22)));
	}

}
