package worms.model.ShapeHelp;

import be.kuleuven.cs.som.annotate.*;
import worms.model.values.Location;

public class BoundaryRectangle extends Rectangle {

	/**
	 * 
	 * @param center
	 * @param size
	 * @param passable
	 * 
	 * @see super
	 * 
	 * @post | new.isPassable() == passable
	 * 
	 * @throws IllegalArgumentException
	 */
	public BoundaryRectangle(Location center, Location size, boolean passable) throws IllegalArgumentException {
		super(center, size);
		this.passable = passable;
	}

	/**
	 * Returns whether this rectangle represents a passable spot.
	 */
	@Basic @Immutable @Raw
	public boolean isPassable() {
		return this.passable;
	}
	private final boolean passable;
}
