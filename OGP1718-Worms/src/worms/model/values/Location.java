package worms.model.values;

import be.kuleuven.cs.som.annotate.*;
import worms.exceptions.InvalidLocationException;

/**
 * A class for handling Location of various game objects.
 * 
 * @invar	Returns true if and only if the location only has valid coordinates.
 * 			|isValidLocation(getX(), getY())
 */
@Value 
public class Location {
	/**
	 * Initializes this new Location based on a given location.
	 * 
	 * @param location
	 * 		  The location for this new Location.
	 * @post the location for this new Location, is the same as the given location.
	 * 		 |new.getX() == location[0]
	 * 		 |new.getY() == location[1]
	 * @throws InvalidLocationException()
	 * 		  The given location is not a valid location.
	 * 		 | ! isValidLocation(location)
	 */
	@Raw
	public Location(double[] location) throws InvalidLocationException{
		if (!this.isValidLocation(location)) {
			throw new InvalidLocationException(location);
		}
		this.x = location[0];
		this.y = location[1];
	}
	
	/**
	 * Initializes this new Location based on a given location.
	 * 
	 * @param location
	 * 		  The location for this new Location.
	 * @post the location for this new Location, is the same as the given location.
	 * 		 |new.getX() == x
	 * 		 |new.getY() == y
	 * @throws InvalidLocationException()
	 * 		  The given location is not a valid location.
	 * 		 | ! isValidLocation(x,y)
	 */
	@Raw
	public Location(double x, double y) throws InvalidLocationException{
		if (!this.isValidLocation(x,y)) {
			throw new InvalidLocationException(x,y);
		}
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Origin reference point constant at coordinates {0,0}
	 */
	public static final Location ORIGIN = new Location(0,0);
	
	/**
	 * Returns the x-coordinate of this location.
	 * 
	 */
	@Basic @Raw @Immutable
	public double getX() {
		return this.x;
	}
	
	/**
	 * Returns the y-coordinate of this location.
	 * 
	 */
	@Basic @Raw @Immutable
	public double getY() {
		return this.y;
	}
	
	/**
	 * Returns the distance between this and a given pointlocation.
	 * 
	 * @param point
	 * @return | result == Math.sqrt(Math.pow(this.getX()-point.getX(),2) + Math.pow(this.getY() - point.getY(), 2))
	 */
	public double getDistanceFrom(Location point) {
		return Math.sqrt(Math.pow(this.getX()-point.getX(),2) + Math.pow(this.getY() - point.getY(), 2));
	}
	
	/**
	 * Checks whether a given location exists and only contains valid coordinates.
	 * 
	 * @param location
	 * 		A given set of coordinates, with at index 0 the supposed x-location and at index 1
	 * 		the supposed y-location.
	 * @return True if the location is valid(not null) and both indexes are within
	 *         the given bounds. 
	 *         |result == 
	 *         | (location != null)
	 *         | && isValidLocation(location[0], location[1])
	 */
	public boolean isValidLocation(double[] location) {
		if (location == null) {
			return false;
		}
		if (!this.isValidLocation(location[0], location[1])) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks whether x and y contain valid coordinates.
	 * 
	 * @param x
	 * 		The given x-coordinate.
	 * @param y
	 * 		The given y-coordinate.
	 * @return True if the location is valid(not null) and both indexes are within
	 *         the given bounds. 
	 *         |result == 
	 *         | (x !=Double.NaN && y != Double.NaN)
	 */
	public boolean isValidLocation(double x, double y) {
		if (Double.compare(x, Double.NaN) == 0 || Double.compare(y, Double.NaN) == 0 ) {
			return false;
		}
		return true;
	}
	
	/**
	 * The x coordinate of the location
	 */
	private final double x;
	/**
	 * The y coordinate of the location
	 */
	private final double y;
	
	/**
	 * Returns an array representation of this location
	 * 
	 * @return Returns an array with at index 0 the x-coordinate and at index 1 the y-coordinate
	 * 		| let double[] loc = new double[2] in
	 * 		|	loc[0] = getX();
	 * 		|	loc[1] = getY();
	 * 		|	result.equals(loc)
	 */
	public double[] getLocation() {
		double[] loc = new double[2];
		loc[0] = this.getX();
		loc[1] = this.getY();
		return loc;
	}
	
	/**
	 * Checks whether this Location is a valid one.
	 
	 * @return return true if and only if this location is effective (true since this is an
	 * 			instanced function) and both x and y are valid coordinates.
	 * 			| result == this.isValidLocation(this.getX(), this.getY());
	 * 
	 */
	public boolean isValid() {
		return this.isValidLocation(this.getX(), this.getY());
	}
	
	/**
	 * Check whether this Location is equal to the given object.
	 * 
	 * @return True if and only if the given object is effective,
	 * 		   if this Location and the given object belong to the same class,
	 * 		   and if this Location's location and the given object interpreted as 
	 * 		   a Location have equal coordinates. 
	 * 		  | result == 
	 * 		  |   ((obj != null)
	 * 		  |  && (this.getClass() == obj.getClass())
	 * 		  |  && this.getX() == (otherLocation.getX()) && this.getY() == (otherLocation.getY())
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Location otherLocation = (Location) obj;
		return this.getX() == (otherLocation.getX()) && this.getY() == (otherLocation.getY());
	}
	/**
	 * Returns the hascode of this Location.
	 */
	@Override
	public int hashCode() {
		return ((Double)getX()).hashCode() + ((Double)getY()).hashCode();
		}

	/**
	 * Returns a textual representation of the location.
	 * @return Returns an open accolade followed by the x-coordinate, semicolom, the y-coordinate and a closing accolade as a textual representation.
	 * 		  |result.equals("{" + getX() + ";" + getY() + "}")
	 */
	@Override
	public String toString() {
		return "{" + getX() + ";" + getY() + "}";
	}
}
