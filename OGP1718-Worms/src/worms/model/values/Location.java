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
		if (!isValidLocation(location)) {
			throw new InvalidLocationException(location);
		}
		this.x = location[0];
		this.y = location[1];
	}
	
	@Basic @Raw @Immutable
	public double getX() {
		return this.x;
	}
	
	@Basic @Raw @Immutable
	public double getY() {
		return this.y;
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
	public static boolean isValidLocation(double[] location) {
		if (location == null) {
			return false;
		}
		if (!isValidLocation(location[0], location[1])) {
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
	public static boolean isValidLocation(double x, double y) {
		if (x == Double.NaN || y == Double.NaN) {
			return false;
		}
		return true;
	}
	
	private final double x;
	private final double y;
	
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
	 * 		  |"{" + getX() + ";" + getY() + "}";
	 */
	@Override
	public String toString() {
		return "{" + getX() + ";" + getY() + "}";
	}
}
