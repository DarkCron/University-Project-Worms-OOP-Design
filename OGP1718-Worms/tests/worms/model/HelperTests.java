package worms.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import worms.model.ShapeHelp.Circle;
import worms.model.ShapeHelp.Rectangle;
import worms.model.values.Location;
import worms.model.values.Radius;

public class HelperTests {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void circleTest() {
		Circle c = new Circle(Location.ORIGIN, new Radius(5));
		assertFalse(c.contains(new Location(50,0)));
		assertFalse(c.contains(new Location(0,50)));
		assertFalse(c.contains(new Location(50,50)));
		assertTrue(c.contains(Location.ORIGIN));
		assertTrue(c.contains(new Location(0.5, 3)));
		
		c.setCenter(new Location(20,20));
		assertTrue(c.contains(new Location(22,22)));
		Circle c2 = new Circle(Location.ORIGIN, new Radius(5));
		assertFalse(c.contains(c2));
		c2.setCenter(new Location(17,21));
		assertTrue(c.contains(c2));
		c2.setCenter(new Location(23,21));
		assertTrue(c.contains(c2));
		c2.setCenter(new Location(21,23));
		assertTrue(c.contains(c2));
		c2.setCenter(new Location(21,17));
		assertTrue(c.contains(c2));
		c2.setCenter(new Location(23,23));
		assertTrue(c.contains(c2));
		c2.setCenter(new Location(16,16));
		assertTrue(c.contains(c2));
		c2.setCenter(new Location(20,15));
		assertTrue(c.contains(c2));
		c2.setCenter(new Location(20,9.9));
		assertFalse(c.contains(c2)); //<-----
		c2.setCenter(new Location(20,10));
		assertTrue(c.contains(c2)); // <-----
		
		c2.setRadius(new Radius(3));
		assertFalse(c.contains(c2));
		c2.setCenter(new Location(20,12));
		assertTrue(c.contains(c2));
		c2.setCenter(new Location(20,11.9999));
		assertFalse(c.contains(c2));
	}
	
	@Test
	public void circleOverlapTest() {
		Circle c = new Circle(Location.ORIGIN, new Radius(5));
		Circle c2 = new Circle(new Location(0,10), new Radius(5));
		assertFalse(c.overlaps(c2));
		c2.setCenter(new Location(0,9.9));
		assertTrue(c.overlaps(c2));
		c2.setCenter(new Location(0,-9.9));
		assertTrue(c.overlaps(c2));
	}
	
	@Test
	public void rectangleTest() {
		Rectangle r = new Rectangle(new Location(0,0),new Location(10, 10));
		assertTrue(r.getSize().equals(new Location(10, 10)));
		Circle c = new Circle(Location.ORIGIN, new Radius(5));
		assertTrue(c.getBoundingRectangle().getCenter().equals(new Location(c.getCenter().getX() - c.getRadius().getRadius(), c.getCenter().getY() - c.getRadius().getRadius())) && c.getBoundingRectangle().getSize().equals(r.getSize()));
	}

}
