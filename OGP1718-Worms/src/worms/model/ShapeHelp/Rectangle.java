package worms.model.ShapeHelp;

import worms.model.values.Location;

/**
 * A utility class for managing Rectangle shapes
 * 
 * @author bernd
 *
 * @invar | isValidSize(getSize())
 * 
 * @see super
 */
public class Rectangle extends Shape {

	/**
	 * Creates a new Rectangle with it's center being the TOP LEFT point of the rectangle.
	 * 
	 * @param center
	 * @param size
	 * 
	 * @post | new.getSize() == size
	 * 
	 * @see super
	 * 
	 * @throws IllegalArgumentException
	 * 		| !isValidSize(size)
	 */
	public Rectangle(Location center, Location size) throws IllegalArgumentException {
		super(center);
		if(!isValidSize(size)) {
			throw new IllegalArgumentException("Invalid size for rectangle.");
		}
		this.setSize(size);
	}

	/**
	 * Checks whether the given size is valid for any and all rectangles.
	 * 
	 * @param size
	 * @return | result == size != null && size.getX() != 0 && size.getY() != 0
	 */
	public static boolean isValidSize(Location size) {
		return size != null && size.getX() > 0 && size.getY() > 0;
	}
	
	/**
	 * Sets the size of this rectangle.
	 * 
	 * @param size
	 * 
	 * @post | new.getSize() == size
	 */
	public void setSize(Location size) {
		this.size = size;
	}
	
	/**
	 * Returns this Rectangle's size.
	 */
	public Location getSize() {
		return this.size;
	}
	
	/**
	 * This size, width and length, of this rectangle.
	 */
	private Location size = new Location(1,1);

	/**
	 * Checks whether a given point is contained in this rectangle.
	 * @param loc
	 * @return  | result == 
	 * 			|	loc.getX() >= this.getCenter().getX()
	 * 			|	&& loc.getX() <= this.getCenter().getX() + this.getSize().getX()
	 * 			|	&& loc.getY() >= this.getCenter().getY()
	 * 			|	&& loc.getY() <= this.getCenter().getY() + this.getSize().getY()
	 */
	public boolean containsPoint(Location loc) {
		if(loc.getX() >= this.getCenter().getX() && loc.getX() <= this.getCenter().getX() + this.getSize().getX()) {
			if(loc.getY() >= this.getCenter().getY() && loc.getY() <= this.getCenter().getY() + this.getSize().getY()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether this rectangle fully contains a given rectangle,
	 * !!! assumes this rectangle has a positive or zero point location.
	 * 
	 * @param boundingRectangle
	 * @return 	| result == 
	 * 			|	boundingRectangle.getCenter().getX()>=0
	 *			|	&& boundingRectangle.getCenter().getY() >= 0
	 *			|	&& (this.getCenter().getX()+this.getSize().getX()) >= (boundingRectangle.getCenter().getX() + boundingRectangle.getSize().getX())
	 *			|	&& (this.getCenter().getY()+this.getSize().getY()) >= (boundingRectangle.getCenter().getY() + boundingRectangle.getSize().getY());
	 */
	public boolean fullyContains(Rectangle boundingRectangle) {
		return  boundingRectangle.getCenter().getX()>=0
				&& boundingRectangle.getCenter().getY() >= 0
				&& (this.getCenter().getX()+this.getSize().getX()) >= (boundingRectangle.getCenter().getX() + boundingRectangle.getSize().getX())
				&& (this.getCenter().getY()+this.getSize().getY()) >= (boundingRectangle.getCenter().getY() + boundingRectangle.getSize().getY());
	}
	
	//TODO
	public boolean intersects(Circle c) {
		if(this.fullyContains(c.getBoundingRectangle())) {
			return true;
		}
		for (double x = 0; x < this.getSize().getX(); x+=this.getSize().getY()/10) {
			for (double y = 0;  y < this.getSize().getY(); y+=this.getSize().getY()/10) {
				Location loc = new Location(this.getCenter().getX()+x, this.getCenter().getY()+y);
				if(loc.getDistanceFrom(c.getCenter())<c.getRadius().getRadius()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean intersects(Rectangle r) {
		return this.getCenter().getX() < r.getCenter().getX() + r.getSize().getX() && this.getCenter().getX() + this.getSize().getX() > r.getCenter().getX() &&
				this.getCenter().getY() < r.getCenter().getY() + r.getSize().getY() && this.getCenter().getY() + this.getSize().getY() > r.getCenter().getY();
	}
	
	//TODO
	public boolean isOnUpperEdge(Location p) {
		return p.getY() == this.getCenter().getY();
	}
	
	public boolean isOnLowerEdge(Location p) {
		return p.getY() == this.getCenter().getY() + this.getSize().getY();
	}
	
	public boolean isOnLeftEdge(Location p) {
		return p.getX() == this.getCenter().getX();
	}
	
	public boolean isOnRightEdge(Location p) {
		return p.getX() == this.getCenter().getX() + this.getSize().getX();
	}
	
	public boolean isOnCornerEdge(Location p) {
		return (isOnUpperEdge(p)||isOnLowerEdge(p)) && (isOnLeftEdge(p) || isOnRightEdge(p));
	}
}
