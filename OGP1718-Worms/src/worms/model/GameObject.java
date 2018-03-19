package worms.model;

import be.kuleuven.cs.som.annotate.*;
import worms.exceptions.InvalidLocationException;
import worms.exceptions.InvalidRadiusException;
import worms.model.values.*;

public abstract class GameObject{
	/**
	 * 
	 * @param location
	 * @param radius
	 * 
	 * @post The location of the gameObject is the same as the given location.
	 * 		| new.location.getX() == location[0]
	 * 		| new.location.getY() == location[1]
	 * @post The radius of the worm is the same as the given radius.
	 * 		|new.radius.getRadius() == radius
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
	public GameObject(double[] location, double radius, double minRadius) throws InvalidLocationException,InvalidRadiusException {
		this.setLocation(location);
		this.setRadius(radius,minRadius);
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
		try {
			this.location = new Location(location);
		} catch (InvalidLocationException e) {
			throw e;
		}
	}

	/**
	 * returns the worm's current x location.
	 */
	@Basic @Raw
	public double getX() {
		return this.location.getX();
	}
	
	/**
	 * returns the worm's current y location.
	 */
	@Basic @Raw
	public double getY() {
		return this.location.getY();
	}
	
	public boolean isValidLocation(Location location) {
		return (location != null) && ();
	}
	
	/**
	 * 
	 */
	private Location location;
	

	/**
	 * Returns the effective radius of the current worm.
	 */
	@Basic
	public double getRadius() {
		return this.getRadius();
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
	public void setRadius(double radius, double minRadius) throws InvalidRadiusException {
		try {
			this.radius = new Radius(radius, minRadius);
		} catch (InvalidRadiusException e) {
			throw e;
		}
		this.generateMass();
	}
	
	/**
	 * 
	 */
	private Radius radius;
	
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
		return density * (((double) 4 / (double) 3) * Math.PI * Math.pow(this.getRadius(), 3));
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
