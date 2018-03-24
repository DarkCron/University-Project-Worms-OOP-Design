package worms.model.ShapeHelp;

import be.kuleuven.cs.som.annotate.*;
import worms.model.values.*;

/**
 * A base class representing a shape.
 * 
 * @author bernd
 *
 *	@Invar | getCenter() != null
 */
public abstract class Shape {
	
	/**
	 * Creates this shape centered at a given location.
	 * 
	 * @param center
	 * @throws IllegalArgumentException
	 * 		| center == null
	 */
	public Shape(Location center) throws IllegalArgumentException{
		if(center==null) {
			throw new IllegalArgumentException("Invalid center for shape.");
		}
		
		this.setCenter(center);
	}
	
	/**
	 * Sets a given center to this shape.
	 * 
	 * @param center
	 * 
	 * @post | new.getCenter == center 
	 */
	public void setCenter(Location center) {
		this.center = center;
	}
	
	/**
	 * Returns this shape's center
	 */
	@Basic
	public Location getCenter() {
		return this.center;
	}
	
	private Location center = Location.ORIGIN;
}
