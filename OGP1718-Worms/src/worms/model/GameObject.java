package worms.model;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Raw;
import worms.exceptions.InvalidLocationException;
import worms.exceptions.InvalidRadiusException;

public abstract class GameObject{
	/**
	 * 
	 * @param location
	 * @param radius
	 * 
	 * @post The location of the worm is the same as the given location.
	 * 		|new.getLocation() == location
	 * @post The radius of the worm is the same as the given radius.
	 * 		|new.getRadius() == radius
	 * 
	 * @throws InvalidRadiusException
	 * 		The given radius is not a valid one, the given radius is less than the minimum allowed radius.
	 * 		| !isValidRadius(location)
	 * @throws InvalidLocationException
	 * 		The given location is not a valid one, the location doesn't exist ( is null) or at least one of
	 * 		the coordinates is not a valid one (one of the coordinates is NaN, Not a Number).
	 * 		| !isValidLocation(location)
	 */
	@Raw
	public GameObject(double[] location, double radius) throws InvalidLocationException,InvalidRadiusException {
		this.setLocation(location);
		this.setRadius(radius);
	}
	
	/**
	 * Sets the location of a worm. If the given location is invalid for any worm,
	 * the location doesn't exist (null) or contains invalid coordinates, any coordinate being
	 * NaN (Not a Number), the location will be rejected and a InvalidLocationException will be thrown.
	 * 
	 * @param location
	 * 		A given tuple of coordinates, with at index 0 the supposed x-coordinate and at index 1 the supposed
	 * 		y - coordinate.
	 * 
	 * @post Location of the worm should be the given location. 
	 * 		|new.getLocation() == location
	 * 
	 * @throws InvalidLocationException
	 *         Given location doesn't resemble a valid location.
	 *      |!isValidlocation(location)
	 * 
	 */
	@Raw
	public void setLocation(double[] location) throws InvalidLocationException {
		if (!isValidLocation(location)) {
			throw new InvalidLocationException(location);
		}
		
		this.location[0] = location[0]; // worms location gets given location
		this.location[1] = location[1];
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
	 *         | && (location[0] !=Double.NaN && location[1] != Double.NaN)
	 */
	public static boolean isValidLocation(double[] location) {
		if (location == null) {
			return false;
		}
		if (location[0] == Double.NaN || location[1] == Double.NaN) {
			return false;
		}
		
		return true;
	}

	/**
	 * returns the worm's current x and y location.
	 */
	@Basic @Raw
	public double[] getLocation() {
		return this.location;
	}
	
	/**
	* A double is a primitive type in Java, this means a double is initialized at 0.0 (it's default value)
	* Upon declaration of this array (tuple) it's contents will be a 0.0 at index 0 and 0.0 at index 1.
	* Therefore, the initialized state of 'location' will be a valid position for the class's invariant
	*/
	private double[] location = new double[2];
	

	/**
	 * Returns the effective radius of the current worm.
	 */
	@Basic
	public double getRadius() {
		return this.radius;
	}

	/**
	 * Sets the radius of a worm. If the radius is invalid, meaning it's smaller than the minimum
	 * allowed radius of a worm, an InvalidRadiusException will be thrown.
	 * 
	 * @param radius
	 *        The given radius for a worm, bigger radius is bigger worm. Minimum
	 *        radius may be in effect.
	 * @post Sets the worm its radius to the given radius.
	 * 		|new.getRadius() == radius
	 * @effect This function also changes the mass and thus max Action Points, since
	 *         a worm's weight depends on it's size.
	 *     | setMass()
	 * @throws InvalidRadiusException
	 *         Whenever the radius is out of bounds.
	 *     |!isValidRadius(radius)
	 *     
	 * @note We currently do not change a worm's current AP when we change it's radius (and
	 * 	therefore it's mass). This can be easily implemented later however.
	 */
	public void setRadius(double radius) throws InvalidRadiusException {
		if (!isValidRadius(radius)) {
			throw new InvalidRadiusException(radius);
		}

		this.radius = radius;
		this.generateMass();
	}
	
	/**
	 * Checks whether a given radius is valid.
	 * 
	 * @param radius
	 *      The given radius for a worm, bigger radius is bigger worm. Minimum
	 *      radius may be in effect.
	 * @return True if the given radius is equal to or bigger than the minimum
	 *         radius. 
	 *      | result == 
	 *      | minRadius <= radius
	 * @return False if the given radius is smaller than the minimum radius. 
	 * 		|result == 
	 * 		| minRadius > radius
	 */
	public abstract boolean isValidRadius(double radius);
	
	/**
	* The default initialization would set the radius to 0.0, this however isn't correct
	* according to the rules of the class invariant stating it should be at least equal or bigger
	* to the minimum radius.
	*/
	private double radius =0;
	
	/**
	 * Calculates and returns the resulting mass of a worm with it's properties
	 * (radius for example).
	 * 
	 * @return calculateMass returns the worms effective mass, calculated using its
	 *       own radius and density.
	 *       |result == 
	 *       |		density * (((double)4 /(double)3) * Math.PI * Math.pow(this.radius, 3))
	 */
	protected double calculateMass(double density) {
		return density * (((double) 4 / (double) 3) * Math.PI * Math.pow(this.radius, 3));
	}

	/**
	 * Returns the mass for the given worm.
	 */
	@Basic
	public double getMass() {
		return this.mass;
	}
	
	public void setMass(double mass) {
		this.mass = mass;
	}

	public abstract void generateMass();

	private double mass;
	
	private World fromWorld;
}
