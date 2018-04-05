package worms.model.ShapeHelp;

import worms.model.values.Location;

public class BoundaryRectangle extends Rectangle {

	public BoundaryRectangle(Location center, Location size, boolean passable) throws IllegalArgumentException {
		super(center, size);
		this.passable = passable;
	}

	public boolean isPassable() {
		return this.passable;
	}
	private final boolean passable;
}
