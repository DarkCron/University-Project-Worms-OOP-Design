package worms.model.ShapeHelp;

import be.kuleuven.cs.som.annotate.Basic;
import worms.exceptions.InvalidRadiusException;
import worms.model.GameObject;
import worms.model.World;
import worms.model.values.*;

/**
 * A class for creating and managing circles.
 * 
 * @author bernd
 *
 * @invar | isValidRadius(getRadius())
 * @see super
 */
public class Circle extends Shape {

	/**
	 * Creates a new circle with a given center and radius.
	 * 
	 * @param center
	 * @param radius
	 * 
	 * @post | new.getRadius() == radius
	 * 
	 * @throws IllegalArgumentException
	 * 		| center == null
	 * 
	 * @see super
	 */
	public Circle(Location center, Radius radius) throws IllegalArgumentException,InvalidRadiusException {
		super(center);
		this.setRadius(radius);
	}
	
	/**
	 * Creates a new circle based on a given gameObject.
	 * 
	 * @param center
	 * @param radius
	 * 
	 * @post | new.getRadius() == gameObject.getRadius()
	 * 
	 * @post | new.getCenter() == gameObject.getLocation()
	 * 
	 * @throws IllegalArgumentException
	 * 		| center == null
	 * 
	 * @see super
	 */
	public Circle(GameObject gameObject) {
		super(gameObject.getLocation());
		this.setRadius(gameObject.getRadius());
	}
	
	/**
	 * Sets the given radius to this circle.
	 * 
	 * @param radius
	 * 
	 * @post | new.getRadius() == radius
	 */
	public void setRadius(Radius radius) throws InvalidRadiusException{
		if(!isValidRadius(radius)) {
			throw new InvalidRadiusException(radius);
		}
		this.radius = radius;
	}
	
	/**
	 * Checks whether the given radius is valid for any and all circles.
	 * 
	 * @param r
	 * 
	 * @return | result == r!=null && r.getRadius() > 0
	 */
	public static boolean isValidRadius(Radius r) {
		if(r!=null && r.getRadius() > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns this circle's radius.
	 */
	@Basic
	public Radius getRadius() {
		return this.radius;
	}

	/**
	 * The radius for this circle.
	 */
	private Radius radius = Radius.DEFAULT_RADIUS;
	
	/**
	 * Checks whether a given location (point) lies within this circle.
	 * 
	 * @param point
	 * @return Pythagorean theorem
	 * 			| result == sqrt(square(point.getX() - getCenter().getX()) + square(point.getY() - getCenter().getY())) <= this.getRadius().getRadius()
	 */
	public boolean contains(Location point) {
		double deltaX = point.getX() - getCenter().getX();
		double deltaY = point.getY() - getCenter().getY();
		double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY,2));
		return distance <= this.getRadius().getRadius();
	}
	
	/**
	 * Checks whether a given location (point) lies within this circle.
	 * 
	 * @param point
	 * @return Pythagorean theorem, but applied to 2 circles. So returns true if and only if
	 * 			the distance between the center points of the circles is less or equal to this radius + c's radius
	 * 			| result == abs(sqrt(square(point.getX() - getCenter().getX()) + square(point.getY() - getCenter().getY())))
	 * 			|				 <= (this.getRadius().getRadius() + c.getRadius().getRadius())
	 */
	public boolean contains(Circle c) {
		double deltaX = c.getCenter().getX() - getCenter().getX();
		double deltaY = c.getCenter().getY() - getCenter().getY();
		double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY,2));
		distance = Math.abs(distance);
		return distance <= (this.getRadius().getRadius() + c.getRadius().getRadius());
	}
	
	/**
	 * Checks whether a given circle overlap within this circle.
	 * 
	 * @param point
	 * @return Pythagorean theorem, but applied to 2 circles. So returns true if and only if
	 * 			the distance between the center points of the circles is less  than this radius + c's radius
	 * 			| result == abs(sqrt(square(point.getX() - getCenter().getX()) + square(point.getY() - getCenter().getY())))
	 * 			|				 < (this.getRadius().getRadius() + c.getRadius().getRadius())	 
	 */
	public boolean overlaps(Circle c) {
		double deltaX = c.getCenter().getX() - getCenter().getX();
		double deltaY = c.getCenter().getY() - getCenter().getY();
		double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY,2));
		distance = Math.abs(distance);
		distance = World.roundingHelper(distance, 4);
		double other = World.roundingHelper((this.getRadius().getRadius() + c.getRadius().getRadius()),4);
//		if(other == distance) {
//			return false;
//		}
		return distance < World.roundingHelper((this.getRadius().getRadius() + c.getRadius().getRadius()),4);
	}
	
	/**
	 * Returns the bounding rectangle around this circle, exactly containing it.
	 * 
	 * @return | result == new Rectangle(this.getRadius().getRadius() * 2, this.getRadius().getRadius() * 2)
	 */
	public Rectangle getBoundingRectangle() {
		double length = this.getRadius().getRadius() * 2;
		return new Rectangle(new Location(this.getCenter().getX()-this.getRadius().getRadius(), this.getCenter().getY()-this.getRadius().getRadius()), new Location(length, length));
	}
}
