package worms.model;

import java.lang.annotation.Annotation;

import be.kuleuven.cs.som.annotate.*;
import worms.exceptions.InvalidLocationException;
import worms.util.ModelException;

/**
 * A class describing our in-game characters, the worms. TODO
 * 
 * @author Liam, Bernd
 * TODO CODE 
 * @Invar worm's location is whithin bounds.
 * 			|
 * @Invar worm's direction is whithin the bounds of 0 and 2Pi.
 * 			|
 */			
public class Worm {
	
	//TODO
	public Worm() {
		
	}
	
	//Done
	/**
	 * 
	 * @param location
	 * 
	 * @post Location of the worm should be the given location.
	 * 		|new.getLocation() == location
	 * 
	 * @throws IllegalArgumentsException, given location doesn't resemble a valid location.
	 * 			|!isValidlocation(location)
	 * 
	 */
	@Raw
	public void setLocation(double[] location) throws ModelException {
		if (!isValidLocation(location))
			throw new ModelException("Invalid location.");
		
		this.location[0] = location[0]; //worms location gets given location
		this.location[1] = location[1];
	}
	
	/**
	 * 
	 * @param location
	 * @return True if the location is valid(not null) and both indexes are within the given bounds.
	 * 			|result ==
	 * 			| location != null
	 * 			| && (location[0] != Double.NaN && location[1] != Double.NaN)
	 */
	public static boolean isValidLocation(double[] location) {
		if (location == null)
			return false;
		if (location[0] == Double.NaN || location[1] == Double.NaN)
			return false;
		return true;
	}
		
	private double[] location = new double[2];
	
	
	/*
	 * returns the worm's current x and y location.
	 */
	@Basic @Raw
	public double[] getLocation() {
		return this.location;
	}
	
	/**
	 * 
	 * @param direction
	 * @pre direction has to be bigger than 0 or equal to 0 and smaller than 2PI
	 * 		|direction >= 0 && direction < 2*Math.pi
	 * @post given direction equals worm's direction
	 * 		|new.getDirection() == direction
	 */
	public void setDirection(double direction) {
		assert direction >= 0 && direction <2*Math.PI;
		this.direction = direction;
	}
	
	private double direction;
	
	/*
	 * getDirection returns the worm its direction. 
	 */
	@Basic @Raw
	public double getDirection() {
		return this.direction;
	}
	
	/*
	 * Returns the effective radius of the current worm.
	 */
	@Basic @Raw
	public double getRadius() {
		return this.radius;
	}
	
	/* 
	 * @param radius
	 * @post Sets the worm its radius to the given radius. 
	 * 		|new.getRadius() == radius
	 * @throws IllegalArgumentException whenever the radius is out of bounds.
	 *  	|!isValidRadius(radius)
	 */
	public void setRadius(double radius) throws ModelException{
		isValidRadius(radius); // crashes if its not true. So no further code needed. 
		this.radius = radius;
	}
	
	private double radius = -1;
	private final static double minRadius = 0.25;
	
	
	/*
	 * @param radius
	 * 
	 * @return True if the given radius is equal to or bigger than the minimum radius.
	 * 			| result == 
	 * 			| minRadius <= radius
	 * @throws IllegalArgumentException whenever the given radius is out of bounds.
	 *			| minRadius > radius
	 */
	public boolean isValidRadius(double radius) throws ModelException{
		if  (this.minRadius > radius)
			throw new ModelException("Radius out of bounds.");
		return this.minRadius <= radius;
	}
	
	private static final double worm_density = 1062;
	
	/*
	 * @post calculateMass returns the worms effective mass, calculated using its own radius and density.
	 * 		|new.getMass() == worm_density * (((double)4 / (double)3)  * Math.PI * Math.pow(this.radius, 3))
	 * @effect?????
	 */
	@Raw
	public double calculateMass() {
		return this.worm_density * (((double)4 / (double)3)  * Math.PI * Math.pow(this.radius, 3));
	}
	
	/*
	 * Returns the mass for the given worm. 
	 */
	@Basic
	public double getMass() {
		return this.mass;
	}
	/*
	 * @post The worm its current mass eaquals the the newly calculated mass.
	 * 			|new.getMass() == this.calculateMass();
	 * @post Whenever the worm its mass gets changed, also will its maxActionPoints
	 * 			|new.getMaxActionPoints() = this.getMaxActionPoints();
	 */
	public void setMass(){
		this.mass = calculateMass();
		this.maxActionPoints = this.getMaxActionPoints();
	}
	
	private double mass;
	
	/*
	 * @return returns the maximum amount of AP for a worm with given mass.
	 */
	public int getMaxActionPoints() {
		return Math.round((float)this.getMass());
	}
	
	/*
	 * @post Each time function is called, action points get reset tot their maximum value.
	 * 			|new.getActonPoints() = this.maxActionPoints
	 */
	public void resetActionPoints() {
		this.currentActionPoints = this.maxActionPoints;
	}
	
	/*
	 * Returns the currentActionPoints for the current worm. 
	 */
	public int getCurrentActionPoints() {
		return currentActionPoints;
	}
	
	private int currentActionPoints;
	private int maxActionPoints;
	
	
	/*
	 * @post Given name becomes the worm its name.
	 * 			|new.getName() = name;
	 * @throws IllegalArgumentException whenever character contains an invalid character.
	 * 			| !isValidName(name)
	 */
	public void setName(String name) throws ModelException{
		if (!NameContainsValidCharactersOnly(name))
			throw new ModelException("Please enter a name containing only valid characters."); // DO WE KEEP THIS OR DO WE REWRITE THIS LIKE setRadius???
		this.name = name;
		return;
	}
	
	private String name;
	
	/*
	 * @return returns true if given character is a letter
	 * 
	 */
	public static boolean isValidCharacter(char c) {
		if (Character.isLetter(c))
			return true;
		if (Character.isWhitespace(c))
			return true;
		if (c == '\'')
			return true;
		return false;
	}
	
	/*
	 * @return True if all characters in string name are valid.
	 * 			|for (char c : name.toCharArray())
	 *			|	isValidCharacter(c)
	 * @throws IllegalArgumentException 
	 * 			|name == null
	 * @throws IllegalArgumentException
	 * 			|for (char c : name.toCharArray())
				|	!isValidCharacter(c)
	 */
	public static boolean NameContainsValidCharactersOnly(String name) throws ModelException{
		if (name == null)
			throw new ModelException("Please enter a name containing only valid characters.");
		for (char c : name.toCharArray()) {
			if (!isValidCharacter(c))
				throw new ModelException("Please enter a name containing only valid characters.");
		}
		return true;
	}
	
	/*
	 * returns the given name
	 */
	@Basic
	public String getName() {
		return name;
	}
	
	
	
}
