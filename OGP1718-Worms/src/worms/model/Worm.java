package worms.model;
import be.kuleuven.cs.som.annotate.*;
import worms.util.ModelException;

/**
 * A class describing our in-game characters, the worms.
 * 
 * @author Liam, Bernd
 * TODO CODE 
 * @Invar worm's location is within bounds.
 * 			| isValidLocation(getLocation())
 * @Invar worm's direction is within the bounds of 0 and 2Pi.
 * 			| getDirection() >= 0 && getDirection() < math.PI * 2
 * @Invar worm's radius must be valid, larger than the minimum radius
 * 			| isValidRadius(getRadius())
 * @Invar worm's action points may never be less than 0 or larger than the worm's max AP
 * 			| isValidAmountOfActionPoints(getCurrentActionPoints())
 * @Invar ??? worm's mass and it's max action points  
 * @Invar worm's name must be valid
 * 			| nameContainsValidCharactersOnly(getName())
 */			
public class Worm {
	
	@Raw
	public Worm(double[] location, double direction, double radius, String name) throws ModelException{
		setLocation(location);
		setDirection(direction);
		setRadius(radius);
		setName(name);
		resetActionPoints();
	}

	/**
	 * Sets the location of a worm.
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
	 * Checks whether a given location exists and only contains valid coordinates.
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
	
	
	/**
	 * returns the worm's current x and y location.
	 */
	@Basic @Raw
	public double[] getLocation() {
		return this.location;
	}
	
	/**
	 * Sets the direction that the worm should be facing. Direction is presented in radians from 0..2 * PI the latter not included.
	 * 0 is facing right, PI / 4 is facing up, PI / 2 is facing left, PI = 0 is facing right again.
	 * 
	 * @param direction
	 * 		The given direction the worm should be facing
	 * @pre direction has to be bigger than 0 or equal to 0 and smaller than 2PI
	 * 		|direction >= 0 && direction < 2*Math.pi
	 * @post given direction equals worm's direction
	 * 		|new.getDirection() == direction
	 */
	@Raw
	public void setDirection(double direction) {
		assert direction >= 0 && direction <2*Math.PI;
		this.direction = direction;
	}
	
	private double direction;
	
	/**
	 * getDirection returns the worm its direction. 
	 */
	@Basic @Raw
	public double getDirection() {
		return this.direction;
	}
	
	/**
	 * Returns the effective radius of the current worm.
	 */
	@Basic @Raw
	public double getRadius() {
		return this.radius;
	}
	
	/**
	 * Sets the radius of a worm.
	 * 
	 * @param radius
	 * 		The given radius for a worm, bigger radius is bigger worm. Minimum radius may be in effect.
	 * @post Sets the worm its radius to the given radius. 
	 * 		|new.getRadius() == radius
	 * @effect This function also changes the mass and thus max Action Points, since a worm's weight depends on it's size.
	 * 		| setMass()
	 * @throws IllegalArgumentException whenever the radius is out of bounds.
	 *  	|!isValidRadius(radius)
	 */
	@Raw
	public void setRadius(double radius) throws ModelException{
		isValidRadius(radius); // crashes if its not true. So no further code needed. 
		this.radius = radius;
		setMass();
	}
	
	private double radius = -1;
	private final static double minRadius = 0.25;
	
	
	/**
	 * Checks whether a given radius is valid.
	 * 
	 * @param radius
	 * 			The given radius for a worm, bigger radius is bigger worm. Minimum radius may be in effect.
	 * @return True if the given radius is equal to or bigger than the minimum radius.
	 * 			| result == 
	 * 			| minRadius <= radius
	 * @throws IllegalArgumentException whenever the given radius is out of bounds.
	 *			| minRadius > radius
	 */
	public boolean isValidRadius(double radius) throws ModelException{
		if  (minRadius > radius)
			throw new ModelException("Radius out of bounds.");
		return minRadius <= radius;
	}
	
	private static final double worm_density = 1062;
	
	/**
	 * Calculates and returns the resulting mass of a worm with it's properties (radius for example).
	 * 
	 * @post calculateMass returns the worms effective mass, calculated using its own radius and density.
	 * 		|new.getMass() == worm_density * (((double)4 / (double)3)  * Math.PI * Math.pow(this.radius, 3))
	 * @effect?????
	 */
	@Raw
	public double calculateMass() {
		return worm_density * (((double)4 / (double)3)  * Math.PI * Math.pow(this.radius, 3));
	}
	
	/**
	 * Returns the mass for the given worm. 
	 */
	@Basic
	public double getMass() {
		return this.mass;
	}
	
	/**
	 * This function sets the theoretical mass of a worm. Since a worm's maximum Action Points depends on a worm's weight
	 * the worm's max AP will be calculated and set in this function as well. 
	 * 
	 * @post The worm its current mass equals the the newly calculated mass.
	 * 			|new.getMass() == this.calculateMass();
	 * @post Whenever the worm its mass gets changed, also will its maxActionPoints
	 * 			|new.getMaxActionPoints() = this.getMaxActionPoints();
	 */
	public void setMass(){
		this.mass = calculateMass();
		this.maxActionPoints = this.calculateMaxActionPoints();
	}
	
	private double mass;
	
	/**
	 * @return returns the maximum amount of AP for a worm with given mass.
	 */
	public int calculateMaxActionPoints() {
		return Math.round((float)this.getMass());
	}
	
	/**
	 * Returns the maximum amount of Action Points assigned to the worm
	 * 
	 * @return returns the maximum amount of AP assigned to the worm.
	 */
	@Basic
	public int getMaxActionPoints() {
		return this.maxActionPoints;
	}
	
	/**
	 * Resets the worm's active Action Points to the worm's maximum amount of AP.
	 * 
	 * @post Each time function is called, action points get reset tot their maximum value.
	 * 			|new.getActonPoints() = this.maxActionPoints
	 */
	@Raw
	public void resetActionPoints() {
		this.currentActionPoints = this.maxActionPoints;
	}
	
	/**
	 * @param amount the new amount a worm's action points should be set to
	 * 
	 * @post If a given amount of action points is possible for the worm, then the new AP amount will be assigned to the worm.
	 * 		| if(isValidAmountOfActionPoints(amount))
	 * 		|	then new.getCurrentActionPoints() == amount
	 * 
	 * @post If a given amount of action points is not possible for the worm, if the amount is greater than the max AP for the worm it's AP will be set to max.
	 * 		| if(!isValidAmountOfActionPoints(amount) && amount > this.getMaxActionPoints() )
	 * 		|	then new.getCurrentActionPoints() == this.getMaxActionPoints()
	 * 
	 * @post If a given amount of action points is not possible for the worm, if the amount is less than 0 it's AP will be set to 0.
	 * 		| if(!isValidAmountOfActionPoints(amount) && amount < 0 )
	 * 		|	then new.getCurrentActionPoints() == 0
	 * 
	 */
	@Raw
	public void setActionPoints(int amount) {
		if(isValidAmountOfActionPoints(amount)) {
			this.currentActionPoints = amount;
		}else if(amount > this.getMaxActionPoints()) {
			this.currentActionPoints = this.getMaxActionPoints();
		}else if(amount < 0) {
			this.currentActionPoints = 0;
		}
	}
	
	/**
	 * Checks whether a given amount of action points is a valid amount for a certain worm.
	 * This should be either greater or equal then zero and lower or equal to the worm's max AP.
	 * 
	 * @param amount
	 * 			A given amount of Action Points
	 * @return Is the given amount of AP possible for the worm
	 * 			| result ==
	 * 			| 	amount >= 0 && amount <= this.maxActionPoints;
	 */
	public boolean isValidAmountOfActionPoints(int amount) {
		return amount >= 0 && amount <= this.maxActionPoints;
	}
	
	/**
	 * Returns the currentActionPoints for the current worm. 
	 */
	@Raw
	public int getCurrentActionPoints() {
		return currentActionPoints;
	}
	
	private int currentActionPoints;
	private int maxActionPoints;
	
	
	/**
	 * Sets the name of the worm.
	 * 
	 * @post Given name becomes the worm its name.
	 * 			|new.getName() = name;
	 * @throws IllegalArgumentException whenever character contains an invalid character.
	 * 			| !isValidName(name)
	 */
	@Raw
	public void setName(String name) throws ModelException{
		if (!nameContainsValidCharactersOnly(name))
			throw new ModelException("Please enter a name containing only valid characters."); // DO WE KEEP THIS OR DO WE REWRITE THIS LIKE setRadius???
		this.name = name;
		return;
	}
	
	private String name;
	
	/**
	 * @return returns true if given character is a valid character
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
	
	/**
	 * Check whether a string name only contains characters that are allowed by the programmer.
	 * 
	 * @return True if all characters in string name are valid.
	 * 			|for (char c : name.toCharArray())
	 *			|	isValidCharacter(c)
	 * @throws IllegalArgumentException 
	 * 			|name == null
	 * @throws IllegalArgumentException
	 * 			|for (char c : name.toCharArray())
				|	!isValidCharacter(c)
	 */
	public static boolean nameContainsValidCharactersOnly(String name) throws ModelException{
		if (name == null)
			throw new ModelException("Please enter a name containing only valid characters.");
		for (char c : name.toCharArray()) {
			if (!isValidCharacter(c))
				throw new ModelException("Please enter a name containing only valid characters.");
		}
		return true;
	}
	
	/**
	 * returns the given name of the worm.
	 */
	@Basic @Raw
	public String getName() {
		return name;
	}
	
	
	
}
