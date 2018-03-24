package worms.model;

import be.kuleuven.cs.som.annotate.*;
import worms.exceptions.InvalidLocationException;
import worms.exceptions.InvalidRadiusException;
import worms.model.values.*;

/**
 * A class describing our in-game characters, the worms.
 * 
 * @author Liam, Bernd
 * 
 * @Invar GameObject's location is valid and within bounds.
 * 		| isValidLocation(getLocation(),getWorld())
 * @Invar worm's radius must be valid, larger than the minimum radius 
 * 		| isValidRadius(getRadius())
 * @Invar | isValidWorld(getWorld())
 */
public abstract class GameObject{
	/**
	 * 
	 * @param location
	 * @param radius
	 * 
	 * @post The location of the gameObject is the same as the given location.
	 * 		| new.getLocation() == location
	 * @post The radius of the worm is the same as the given radius.
	 * 		|new.getRadius() == radius
	 * 
	 * @throws InvalidRadiusException
	 * 		The given radius is not a valid one, the given radius is less than the minimum allowed radius.
	 * 		| !isValidRadius(radius)
	 * @throws InvalidLocationException
	 * 		The given location is not a valid one, the location doesn't exist ( is null) or at least one of
	 * 		the coordinates is not a valid one (one of the coordinates is NaN, Not a Number).
	 * 		| !isValidLocation(location,getWorld())
	 */
	@Raw
	public GameObject(Location location, Radius radius, World world) throws InvalidLocationException,InvalidRadiusException {
		if(!isValidRadius(radius)) {
			throw new InvalidRadiusException(radius);
		}
		this.setRadius(radius);
		
		if(!isValidWorld(world)) {
			throw new IllegalArgumentException("Invalid world.");
		}
		this.setWorld(world);
		
		if(!isValidLocation(location,this.getWorld())) {
			throw new InvalidLocationException(location);
		}
		this.setLocation(location);
		

		
		this.setGameObjectTypeID(new GameObjectTypeID(this.getClass()));
	}
	
	/**
	 * Sets the location of a worm. If the given location is invalid for any worm,
	 * the location will be rejected and a InvalidLocationException will be thrown.
	 * 
	 * @param location
	 * 		A location of class Location. 
	 * 
	 * @post Location of the worm should be the given location. 
	 * 		|new.getLocation() == location
	 * 
	 * @throws InvalidLocationException
	 *        If the given Location is invalid or not effective.
	 *      |!isValidLocation(location,getWorld())
	 * 
	 * @note location's restrictions are handled and checked in it's class. A manual check call can be made using 'x'.isValid()
	 */
	@Raw
	public void setLocation(Location location) throws InvalidLocationException {
		if(!isValidLocation(location,this.getWorld())) {
			throw new InvalidLocationException(location);
		}
		this.location = location;
	}

	/**
	 * returns the gameObjects's current x location.
	 */
	@Basic @Raw
	public double getX() {
		return this.location.getX();
	}
	
	/**
	 * returns the gameObjects's current y location.
	 */
	@Basic @Raw
	public double getY() {
		return this.location.getY();
	}
	
	/**
	 * Return the gameobject's Location
	 * 
	 */
	@Basic @Raw
	public Location getLocation() {
		return this.location;
	}
	
	/**
	 * Checks whether this gameobject's is a valid one.
	 * 
	 * @return Returns true if and only if this GameObject's location is effective and
	 * 			the location is valid.
	 * 			| result == (location != null) && (location.isValid()) && isValidWorldLocation(location,world)
	 */
	public static boolean isValidLocation(Location location, World world) {
		return ((location != null) && location.isValid() && isValidWorldLocation(location,world));
	}
	
	/**
	 * The location of a gameObject in gameSpace coordinates x and y contained in a value object Location.
	 */
	private Location location = Location.ORIGIN;
	

	/**
	 * Returns the effective radius of the current worm.
	 */
	@Basic @Raw
	public Radius getRadius() {
		return this.radius;
	}

	/**
	 * Sets the radius of a worm. If the radius is invalid, meaning it's smaller than the minimum
	 * allowed radius of a worm, an InvalidRadiusException will be thrown.
	 * 
	 * @param radius
	 *        The given radius for a worm, bigger radius is bigger worm. Minimum
	 *        radius may be in effect. Minimum radius is always packed in with radius.
	 * @post Sets the worm its radius to the given radius.
	 * 		|new.getRadius() == radius
	 * @post TODO --mass
	 * @effect This function also changes the mass and thus max Action Points, since
	 *         a worm's weight depends on it's size.
	 *     | setMass()
	 * @throws InvalidRadiusException
	 *         Whenever the radius is out of bounds or invalid.
	 *     |!isValidRadius(new.getRadius())
	 *     
	 */
	public void setRadius(Radius radius) throws InvalidRadiusException {
		if(!isValidRadius(radius)) {
			throw new InvalidRadiusException(radius);
		}
		this.radius = radius;
		this.generateMass();
	}
	
	/**
	 * Checks whether this gameobject's Radius is a valid one.
	 * 
	 * @return Returns true if and only if this GameObject's radius is effective and
	 * 			the radius is valid.
	 * 			| result == (radius != null) && (radius.isValid())
	 */
	public static boolean isValidRadius(Radius radius) {
		return (radius != null) && (radius.isValid());
	}
	
	
	/**
	 * The radius of a gameObject contained in a value object Radius.
	 * Every radius value comes with it's own minimum radius possible.
	 */
	private Radius radius;
	
	/**
	 * Calculates and returns the resulting mass of a worm with it's properties
	 * (radius for example).
	 * 
	 * @return calculateMass returns the worms effective mass, calculated using its
	 *       own radius and density.
	 *       |result == 
	 *       |		density * (((double)4 /(double)3) * Math.PI * Math.pow(this.getRadius().getRadius(), 3))
	 */
	protected double calculateMass(double density) {
		return density * (((double) 4 / (double) 3) * Math.PI * Math.pow(this.getRadius().getRadius(), 3));
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
	
	public void setGameObjectTypeID(GameObjectTypeID ID) {
		this.goID = ID;
	}
	
	public GameObjectTypeID getTypeID() {
		return this.goID;
	}
	
	private GameObjectTypeID goID;
	
	/**
	 * Sets the world of this gameObject.
	 * 
	 * @param world
	 * 
	 * @post | new.getWorld() == world
	 * 
	 * @throws IllegalArgumentException
	 * 		| !isValidWorld(world)
	 */
	public void setWorld(World world) throws IllegalArgumentException{
		this.fromWorld = world;
	}
	
	/**
	 * Returns the world the GameObject resides in.
	 */
	@Raw @Basic
	public World getWorld() {
		return this.fromWorld;
	}
	
	/**
	 * Checks whether the world is a valid one
	 * @param world
	 * 
	 * @return | result == (world!=null)
	 */
	public static boolean isValidWorld(World world) {
		return world != null;
	}

	/**
	 * Checks whether a location is within a given gameObject's world boundaries.
	 * 
	 * @param location
	 *
	 * @return | result == (location.getX()>= 0 && location.getX() <= this.getWorld().getWorldWidth()) && 
	 * 		   | 	(location.getY()>= 0 && location.getY() <= this.getWorld().getWorldHeight())
	 */
	public static boolean isValidWorldLocation(Location location, World world) {
		if((location.getX()>= 0 && location.getX() <= world.getWorldWidth())
				&& (location.getY()>= 0 && location.getY() <= world.getWorldHeight())) {
			return true;
		}
		return false;
	}
	
	private World fromWorld;
	
	
	public void Terminate() {
		isTerminated = true;
		this.fromWorld = null;
		this.getWorld().removeGameObject(this);
	}
	
	/**
	 * Is the current gameObject terminated.
	 */
	@Basic
	public boolean isTerminated() {
		return this.isTerminated;
	}
	
	boolean isTerminated = false;
}
