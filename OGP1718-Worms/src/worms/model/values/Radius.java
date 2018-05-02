package worms.model.values;

import be.kuleuven.cs.som.annotate.*;
import worms.exceptions.InvalidRadiusException;

/**
 * A class handling Radius for various game objects.
 * 
 * @invar	Returns true if and only if the radius is larger than the minRadius.
 * 			|isValidRadius(getRadius(), getMinRadius())
 */
@Value
public class Radius {
	
	
	/**
	 * Initializes this new Radius based on both a given radius and a minimum allowed min radius.
	 * 
	 * @param radius
	 * 		The given new radius for this Radius
	 * @param minRadius
	 * 		The given min radius for this Radius
	 * 
	 * @post |new.getRadius() == radius
	 * 
	 * @post |new.getMinRadius() == minRadius
	 * 
	 * @throws InvalidRadiusException
	 * 		| !isValidRadius(radius, minRadius)
	 */
	@Raw
	public Radius(double radius, double minRadius) throws InvalidRadiusException{
		if(!isValidRadius(radius, minRadius)) {
			throw new InvalidRadiusException(radius);
		}
		this.radius = radius;
		this.minRadius = minRadius;
	}
	
	/**
	 * Initializes this new Radius based on both a given radius and a minimum allowed min radius.
	 * 
	 * @param radius
	 * 		The given new radius for this Radius
	 * @param minRadius
	 * 		The given min radius for this Radius
	 * 
	 * @post |new.getRadius() == radius
	 * 
	 * @post |new.getMinRadius() == radius
	 * 
	 * @throws InvalidRadiusException
	 * 		| !isValidRadius(radius, minRadius)
	 */
	@Raw
	public Radius(double radius) throws InvalidRadiusException{
		if(!isValidRadius(radius, radius)) {
			throw new InvalidRadiusException(radius);
		}
		this.radius = radius;
		this.minRadius = radius;
	}
	
	/**
	 * A default radius that complies to the class invariants.
	 */
	public static final Radius DEFAULT_RADIUS = new Radius(0.1);
	

	/**
	 * Returns whether a given radius is greater or equal to a given minimum radius.
	 * 
	 * @param radius
	 * 		The given radius to check
	 * @param minRadius
	 * 		The given minimum allowed radius
	 * @return returns true if and only if a given radius is greater or equal to a given minimum radius
	 * 		|	result ==
	 * 		|		(radius >= minRadius)
	 */
	public static boolean isValidRadius(double radius,double minRadius) {

		if(Double.compare(Double.NaN, radius) == 0) {
			return false;
		}else if(Double.compare(Double.NaN, minRadius) == 0) {
			return false;
		}
		
		if (minRadius > radius) {
			return false;
		}
		

		return true;
	}
	
	/**
	 * Returns this Radius's radius
	 */
	@Basic @Raw @Immutable
	public double getRadius() {
		return this.radius;
	}
	
	/**
	 * Returns this Radius's minimum allowed radius
	 */
	@Basic @Raw @Immutable
	public double getMinRadius() {
		return this.minRadius;
	}
	
	private final double radius;
	private final double minRadius;
	
	/**
	 * Checks whether this Radius is a valid one.
	 * 
	 * @return True if and only if the radius is greater or equal to the minRadius
	 * 		|	result ==
	 * 		|		(this.getRadius() >= this.getMinRadius())
	 */
	public boolean isValid() {
		return isValidRadius(this.getRadius(), this.getMinRadius());
	}
	
	/**
	 * Check whether this Radius is equal to the given object.
	 * 
	 * @return True if and only if the given object is effective,
	 * 		   if this Radius and the given object belong to the same class,
	 * 		   and if this Radius's radius and the given object interpreted as 
	 * 		   a Radius have equal radius. 
	 * 		  | result == 
	 * 		  |   ((obj != null)
	 * 		  |  && (this.getClass() == obj.getClass())
	 * 		  |  && (this.getRadius() == otherRadius.getRadius())
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Radius otherRadius = (Radius) obj;
		return (this.getRadius() == otherRadius.getRadius());
	}
	
	/**
	 * Returns the hashCode of this Radius
	 */
	@Override
	public int hashCode() {
		return ((Double)this.getRadius()).hashCode()+((Double)this.getMinRadius()).hashCode();
	}

	/**
	 * Returns a textual representation of the radius.
	 * @return Returns 'Radius:' followed by the radius, a comma, 'min radius: ' and the the minimum allowed radius.
	 * 		  |result.equals("Radius: " + ((Double)this.getRadius()).toString() + ", min radius: "+((Double)this.getMinRadius()).toString())
	 */
	@Override
	public String toString() {
		return "Radius: " + ((Double)this.getRadius()).toString() + ", min radius: "+((Double)this.getMinRadius()).toString() ;
	}
}
