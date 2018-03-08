package worms.model;

import javax.activity.InvalidActivityException;

import be.kuleuven.cs.som.annotate.*;
import worms.exceptions.InvalidLocationException;
import worms.exceptions.InvalidWormNameException;
import worms.exceptions.InvalidRadiusException;

/**
 * A class describing our in-game characters, the worms.
 * 
 * @author Liam, Bernd
 * @Invar worm's location is within bounds.
 * 		| isValidLocation(getLocation())
 * @Invar worm's direction is within the bounds of 0 and 2Pi.
 * 		| getDirection() >= 0 && getDirection() < math.PI * 2
 * @Invar worm's radius must be valid, larger than the minimum radius 
 * 		|isValidRadius(getRadius())
 * @Invar worm's action points may never be less than 0 or larger than the
 *        worm's max AP
 *      | isValidAmountOfActionPoints(getCurrentActionPoints())
 * @Invar worm's max AP is always the worm's mass rounded up
 * 		| hasProperMaxActionPoints()
 * @Invar worm's name must be valid 
 * 		| nameContainsValidCharactersOnly(getName())
 */
public class Worm {

	/**
	 * The constructor for creating a new worm. The worm have it's properties initialized based on:
	 * the given direction, location, radius and name.
	 * When given parameters do not comply to the Worm-class's restrictions an error gets thrown or
	 * an attempt will be made to adjust the given values.
	 * 
	 * @param location
	 * 		A given tuple of coordinates, with at index 0 the supposed x-coordinate and at index 1 the supposed
	 * 		y - coordinate.
	 * @param direction
	 *      The given direction the worm should be facing
	 * @param radius
	 * 	    The given radius for a worm, bigger radius is bigger worm. Minimum
	 *      radius may be in effect.
	 * @param name
	 *      The given name to try to assign to the worm.
	 *      
	 * @post The location of the worm is the same as the given location.
	 * 		|new.getLocation() == location
	 * @post The direction of the worm is the same as the given direction.
	 * 		|new.getDirection() == direction
	 * @post The radius of the worm is the same as the given radius.
	 * 		|new.getRadius() == radius
	 * @post The name of the worm is the same as the given name.
	 * 		|new.getName() == name
	 * 	
	 * @throws InvalidWormNameException
	 * 		The given name is not a valid one for the worm, it contains invalid characters.
	 * 		| !nameContainsValidCharactersOnly(name)
	 * @throws InvalidRadiusException
	 * 		The given radius is not a valid one, the given radius is less than the minimum allowed radius.
	 * 		| !isValidRadius(location)
	 * @throws InvalidLocationException
	 * 		The given location is not a valid one, the location doesn't exist ( is null) or at least one of
	 * 		the coordinates is not a valid one (one of the coordinates is NaN, Not a Number).
	 * 		| !isValidLocation(location)
	 * 
	 * @note All class invariants are satisfied upon instantiating a new Worm. Therefore we don't need the @Raw tag.
	 */
	public Worm(double[] location, double direction, double radius, String name)
			throws InvalidWormNameException, InvalidRadiusException, InvalidLocationException {
		setLocation(location);
		setDirection(direction);
		setRadius(radius);
		setName(name);
		resetActionPoints();
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
	* A double is a primitive type in Java, this means a double is initialized at 0.0 (it's default value)
	* Upon declaration of this array (tuple) it's contents will be a 0.0 at index 0 and 0.0 at index 1.
	* Therefore, the initialized state of 'location' will be a valid position for the class's invariant
	*/
	private double[] location = new double[2];

	/**
	 * returns the worm's current x and y location.
	 */
	@Basic @Raw
	public double[] getLocation() {
		return this.location;
	}

	/**
	 * Sets the direction that the worm should be facing. Direction is presented in
	 * radians from 0..2 * PI the latter not included. 0 is facing right, PI / 4 is
	 * facing up, PI / 2 is facing left, PI = 0 is facing right again.
	 * 
	 * @param direction
	 *            The given direction the worm should be facing
	 * @pre direction has to be bigger than 0 or equal to 0 and smaller than 2PI
	 *      |direction >= 0 && direction < 2*Math.pi
	 * @post given direction equals worm's direction 
	 * 		|new.getDirection() == direction
	 */
	@Raw
	public void setDirection(double direction) {
		assert direction >= 0 && direction < 2 * Math.PI;
		this.direction = direction;
	}
	
	/**
	 * A primitive type double in Java is initialized as 0.0, thus direction is correct for the class invariant.
	 */
	private double direction;

	/**
	 * getDirection returns the worm its direction.
	 */
	@Basic
	@Raw
	public double getDirection() {
		return this.direction;
	}

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
	 */
	public void setRadius(double radius) throws InvalidRadiusException {
		if (!isValidRadius(radius)) {
			throw new InvalidRadiusException(radius);
		}

		this.radius = radius;
		setMass();
	}
	
	/**
	 * We introduce a static final to set the minimum lower bounds where 'every' worm should comply to.
	 */
	private final static double MINIMUM_RADIUS = 0.25;
	
	/**
	* The default initialization would set the radius to 0.0, this however isn't correct
	* according to the rules of the class invariant stating it should be at least equal or bigger
	* to the minimum radius.
	*/
	private double radius = 0.25;
	


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
	public boolean isValidRadius(double radius) {
		if (MINIMUM_RADIUS > radius) {
			return false;
		}

		return MINIMUM_RADIUS <= radius;
	}

	//A given constant value, therefore a static final value.
	private static final double WORM_DENSITY = 1062;

	/**
	 * Calculates and returns the resulting mass of a worm with it's properties
	 * (radius for example).
	 * 
	 * @return calculateMass returns the worms effective mass, calculated using its
	 *       own radius and density.
	 *       |result == 
	 *       |		worm_density * (((double)4 /(double)3) * Math.PI * Math.pow(this.radius, 3))
	 */
	public double calculateMass() {
		return WORM_DENSITY * (((double) 4 / (double) 3) * Math.PI * Math.pow(this.radius, 3));
	}

	/**
	 * Returns the mass for the given worm.
	 */
	@Basic
	public double getMass() {
		return this.mass;
	}

	/**
	 * This function sets the theoretical mass of a worm. Since a worm's maximum
	 * Action Points depends on a worm's weight the worm's max AP will be calculated
	 * and set in this function as well.
	 * 
	 * @post The worm its current mass equals the the newly calculated mass.
	 *         |new.getMass() == this.calculateMass();
	 * @post Whenever the worm's mass gets changed, it's max AP will be changed accordingly
	 *       |new.getMaxActionPoints() == this.calculateMaxActionPoints();
	 *       
	 * @note this.calculateMass() and  this.calculateMaxActionPoints() can reliably be used correctly
	 * 		with the class invariants since the mass is affected by the current radius of the worm.
	 * 		Weight and thus max AP only changes whenever the radius changes. However the radius isn't changed
	 * 		inside this method, thus a worm's radius can be considered constant in and exactly in this function.
	 */
	public void setMass() {
		this.mass = calculateMass();
		this.maxActionPoints = this.calculateMaxActionPoints();
	}

	/**
	 * The default initialization for mass would be 0.0, this however isn't correct according
	 * to our class invariant. We therefore set the initial mass of a worm to be equal to the minimum
	 * size a worm can be (since a worm's radius is initialized to it's minimum lower bound).
	 */
	private double mass =  WORM_DENSITY * (((double) 4 / (double) 3) * Math.PI * Math.pow(MINIMUM_RADIUS, 3));

	/**
	 * Calculates the maximum amount of action points a worm can have at a certain moment with a certain size (radius).
	 * 
	 * @return returns the maximum amount of AP for a worm with given mass.
	 */
	public int calculateMaxActionPoints() {
		return Math.round((float) this.getMass());
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
	 * Checks whether a worm's current max AP is correctly dependent on it's weight.
	 */
	public boolean hasProperMaxActionPoints() {
		return (Math.round((float) this.getMass()) == maxActionPoints);
	}

	/**
	 * Resets the worm's active Action Points to the worm's maximum amount of AP.
	 * 
	 * @post Each time function is called, action points get reset to their maximum
	 *       value.
	 *       |new.getActonPoints() == this.maxActionPoints
	 */
	public void resetActionPoints() {
		this.currentActionPoints = this.maxActionPoints;
	}

	/**
	 * Sets the given amount of action points to the worm. If the given amount is too large,
	 * bigger than the max action points, it's action points will be set to the maximum available.
	 * If the given amount is less than zero, the worm's current amount of action points,
	 * wil be set to zero. In all other cases the worm's current action points will be equal to the given
	 * amount of action points.
	 * 
	 * @param amount
	 *       the new amount a worm's action points should be set to
	 * 
	 * @post If a given amount of action points is possible for the worm, then the
	 *       new AP amount will be assigned to the worm.
	 *       |if(isValidAmountOfActionPoints(amount))
	 *       | then new.getCurrentActionPoints() == amount
	 * 
	 * @post If a given amount of action points is not possible for the worm, if the
	 *       amount is greater than the max AP for the worm it's AP will be set to
	 *       max.
	 *       | if(!isValidAmountOfActionPoints(amount) && amount >this.getMaxActionPoints() )
	 *       | then new.getCurrentActionPoints() == this.getMaxActionPoints()
	 * 
	 * @post If a given amount of action points is not possible for the worm, if the
	 *       amount is less than 0 it's AP will be set to 0. 
	 *       |if(!isValidAmountOfActionPoints(amount) && amount < 0 ) 
	 *       | then new.getCurrentActionPoints() == 0
	 * 
	 */
	public void setActionPoints(int amount) {
		if (isValidAmountOfActionPoints(amount)) {
			this.currentActionPoints = amount;
		} else if (amount > this.getMaxActionPoints()) {
			this.currentActionPoints = this.getMaxActionPoints();
		} else if (amount < 0) {
			this.currentActionPoints = 0;
		}
	}

	/**
	 * Checks whether a given amount of action points is a valid amount for a
	 * certain worm. This should be either greater or equal then zero and lower or
	 * equal to the worm's max AP.
	 * 
	 * @param amount 
	 * 		A given amount of Action Points
	 * @return Is the given amount of AP possible for the worm 
	 * 		| result ==
	 * 		| amount >= 0 && amount <= this.maxActionPoints;
	 */
	public boolean isValidAmountOfActionPoints(int amount) {
		return amount >= 0 && amount <= this.maxActionPoints;
	}

	/**
	 * Returns the currentActionPoints for the current worm.
	 */
	@Basic
	public int getCurrentActionPoints() {
		return currentActionPoints;
	}

	/**
	 * In Java int is primitive data type, it's default initialization is 0. Which is correct
	 * for our class invariant.
	 */
	private int currentActionPoints;
	/**
	 * The default initialization for an int is 0, however another class invariant states the max AP
	 * 	of a worm depends on it's weight. Therefore we initialize it's maxAP based on the weight (calculated before).
	 * Which we know exists since a worm starts with min radius and has it's mass calculated based on that.
	 */
	private int maxActionPoints = Math.round((float) this.getMass());

	/**
	 * Sets the name of the worm. If the given name for a worm is invalid, meaning the name is null,
	 * or the name contains any invalid characters a InvalidWormNameException will be thrown.
	 * 
	 * @param name
	 *      The given name to try to assign to the worm.
	 * @post Given name becomes the worm its name.
	 * 		|new.getName() == name;
	 * @throws InvalidWormNameException whenever character contains an invalid character. 
	 *      |!isValidName(name)
	 */
	@Raw
	public void setName(String name) throws InvalidWormNameException {
		if (!nameContainsValidCharactersOnly(name)) {
			throw new InvalidWormNameException(name);
		}
		this.name = name;
		return;
	}
	
	/**
	 * name is not a primitive data type, therefore it will be initialized to null. Which is not
	 * according to the class invariants. We therefore initialize the worm's name to a 'proper' name.
	 */
	private String name = "Worm";

	/**
	 * Checks whether a character is a valid one for a worm's name.
	 * Currently only letters, whitespaces (blank spaces) and single quotation
	 * marks are allowed, any other character is rejected.
	 * 
	 * @return returns true if given character is a letter
	 * 		| result ==
	 * 		| 	Character.isLetter(c)
	 * @return returns true if given character is a whitespace (blank space)
	 * 		| result ==
	 * 		| 	Character.isWhitespace(c)
	 * @return returns true if given character is a single quotation mark '
	 * 		| result ==
	 * 		| 	c == '\''
	 * @return returns false if given character not a valid character.
	 * 			Not a letter, whitespace or single quotation mark.
	 * 		| result ==
	 * 		| 	(!Character.isLetter(c) && !(c == '\'') && !Character.isLetter(c))
	 */
	public static boolean isValidCharacter(char c) {
		if (Character.isLetter(c)) {
			return true;
		}

		if (Character.isWhitespace(c)) {
			return true;
		}
		if (c == '\'') {
			return true;
		}
		return false;
	}

	/**
	 * Check whether a string name only contains characters that are allowed by the
	 * programmer.
	 * 
	 * @return False if the given name is shorter than 2 characters
	 * 		| result ==
	 * 		|	(name == null)
	 * @return False if the given name is null
	 * 		| result ==
	 * 		|	(name.length() < 2)
	 * @return False if the given name does not start with an uppercase character
	 * 		| result ==
	 * 		|	(!Character.isUpperCase(name.charAt(0))
	 * @return True if all characters in string name are valid. 
	 * 		|	boolean bIsValidName = true
	 * 		|	for (char c :name.toCharArray()) 
	 * 		|		bIsValidName = isValidCharacter(c)
	 * 		|	if bIsValidName
	 * 		|	then	result == true
	 * @return False if a single character in string name is invalid. 
	 * 		|	boolean bIsValidName = false
	 * 		|	for (char c :name.toCharArray()) 
	 * 		|		bIsValidName = isValidCharacter(c)
	 * 		|		if !bIsValidName
	 * 		|		then	result == false
	 */
	public static boolean nameContainsValidCharactersOnly(String name){
		if (name == null)
		{
			return false;
		}
		if(name.length() < 2) {
			return false;
		}
		if(!Character.isUpperCase(name.charAt(0))) {
			return false;
		}
		
		boolean bNameIsValid = true;
		
		for (char c : name.toCharArray()) {
			if (!isValidCharacter(c)) {
				bNameIsValid = false;
			}	
		}
		return bNameIsValid;
	}

	/**
	 * returns the given name of the worm.
	 */
	@Basic @Raw
	public String getName() {
		return name;
	}
	
	public void Move(int nbSteps) throws InvalidLocationException {
		double[] deltaMovement = new double[2];
		deltaMovement[0] = Math.cos(this.getDirection()) * this.getRadius(); 
	    deltaMovement[1] = Math.sin(this.getDirection()) * this.getRadius();

	    double[] tmpLocation = new double[2];
	    tmpLocation[0] = deltaMovement[0] + this.getLocation()[0];
	    tmpLocation[1] = deltaMovement[1] + this.getLocation()[1];
	    
	    
		if(!isValidLocation(tmpLocation)) {
			throw new InvalidLocationException(tmpLocation);
		}
		
		
		
		//Should we check if the movement is possible due to the amount of current action points???
		this.setActionPoints(this.getCurrentActionPoints() - this.getMovementCost(deltaMovement));
		setLocation(tmpLocation);
		
		if(nbSteps>1) {
			this.Move(nbSteps-1);
		}
	}
	
	public int getMovementCost(double[] deltaMovement) {
		int cost = 0;
		cost = (int) Math.ceil(Math.abs(Math.cos(this.getDirection())) + Math.abs(4*Math.sin(this.getDirection())));
		
		return cost;
	}
	
	//ANGLE IS DIFFERENCE BETWEEN NEW AND OLD ANGLE, angle = new - old
	public void Turn(double angle) {
		double newDirection = (angle + getDirection());
		this.setActionPoints(this.getCurrentActionPoints() - this.getTurnCost(angle));
		setDirection(newDirection);
	}
	
	public int getTurnCost(double angle) {
		int cost = 0;
		cost = Math.abs((int)Math.ceil((angle/(2*Math.PI)*60)));
		return cost;
	}
	
	public void Jump() {
		// Checking the worm's direction for jumping.
		if (!(this.getDirection() >= 0 && this.getDirection() <= Math.PI)) {
			throw new RuntimeException();
		}
		
		jumpSpeedMagnitude = (this.jumpForce()/this.getMass())*JUMP_TIME_DELTA;
		if (!isValidJumpSpeedMagnitude(jumpSpeedMagnitude)) {
			throw new IllegalArgumentException();//FIX DEZE SHIT	
		}
		this.CalculateJumpTime();
		
		this.setActionPointsAfterJump();

	}
	
	public void setActionPointsAfterJump() {
		this.setActionPoints(0);
	}
	
	public double jumpForce() {
		return ((float)5*(float)this.getCurrentActionPoints())+(this.getMass()*GRAVITY);
	}
	
	public static boolean isValidJumpSpeedMagnitude(double jumpSpeed) {
		if (jumpSpeed < 0) {
			//error
			return false;
		}
		
		else if (Double.isNaN(jumpSpeed)) {
			//error
			return false;
		}
		
		else if (Double.isInfinite(jumpSpeed)){
			//error
			return false;
		}
		return true;
	}
	
	public double getJumpSpeedMagnitude() {
		return this.jumpSpeedMagnitude;
	}
	
	private static double GRAVITY = 5.0;
	private static double JUMP_TIME_DELTA = 0.5;
	private double jumpSpeedMagnitude = 0;

	public double[] jumpStep(double deltaTime) throws InvalidLocationException{
		//speed in air
		double speedX = this.getJumpSpeedMagnitude()*Math.cos(this.getDirection());
		double speedY = this.getJumpSpeedMagnitude()*Math.sin(this.getDirection());
		//Position in air
		double xPosTime = this.getLocation()[0]+(speedX*deltaTime);
		double yPosTime = this.getLocation()[1]+((speedY*deltaTime) - (((float)1/(float)2)*GRAVITY*Math.pow(deltaTime,2)));
		
		double[] tmpLocation = new double[2];
		tmpLocation[0] = xPosTime;
		tmpLocation[1] = yPosTime;
		if (!isValidLocation(tmpLocation)) {
			throw new InvalidLocationException(tmpLocation);
		}
		return tmpLocation;
	}
	
	public void CalculateJumpTime() {
		double distance = Math.pow(this.getJumpSpeedMagnitude(), 2)*Math.sin(this.getDirection()*((float)2))/GRAVITY;
		double timeInterval = (distance/(this.getJumpSpeedMagnitude()*Math.cos(this.getDirection())));
		setJumpTime(timeInterval);
	}

	public void setJumpTime(double time) {
		this.jumpTime = time;
	}
	
	public double getJumpTime() {
		return jumpTime;
	}

	
	private double jumpTime = 0;



}
