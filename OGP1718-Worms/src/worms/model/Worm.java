package worms.model;

import java.math.BigInteger;

import be.kuleuven.cs.som.annotate.*;
import worms.exceptions.InvalidLocationException;
import worms.exceptions.InvalidWormNameException;
import worms.exceptions.NotEnoughAPException;
import worms.internal.gui.game.IActionHandler;
import worms.model.Projectile.CreateProjectile;
import worms.model.Projectile.Projectile_Type;
import worms.model.ShapeHelp.Circle;
import worms.model.values.*;
import worms.exceptions.InvalidRadiusException;

/**
 * A class describing our in-game characters, the worms.
 * 
 * @author Liam, Bernd
 * 
 * @Invar worm's direction is within the bounds of 0 and 2Pi.
 * 		| getDirection() >= 0 && getDirection() < math.PI * 2
 * @Invar worm's action points may never be less than 0 or larger than the
 *        worm's max AP
 *      | isValidAmountOfActionPoints(getCurrentActionPoints())
 * @Invar worm's max AP is always the worm's mass rounded up
 * 		| hasProperMaxActionPoints()
 * @Invar worm's name must be valid 
 * 		| isValidName(getName())
 * @Invar worm's jump speed (vector)magnitude 
 * 		| isValidJumpSpeedMagnitude(getJumpSpeedMagnitude())
 * @Invar this worm has a valid team
 * 		| isValidTeam(getTeam(),this)
 * 
 * @see super
 */
public class Worm extends GameObject implements IJumpable{

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
	 * @post The direction of the worm is the same as the given direction.
	 * 		|new.getDirection() == direction
	 * @post The name of the worm is the same as the given name.
	 * 		|new.getName() == name
	 * @post This worm's team is equal to the give team.
	 * 		|new.getTeam() == team
	 * @post This worm's health is between or equal to a worm's min and max HP possible
	 * 		| new.getHitPoints().getHp().compareTo(new BigInteger(World.getWormMinHP().ToString())) >= 0
	 * 		| && new.getHitPoints().getHp().compareTo(new BigInteger(World.getWormMaxHP().ToString())) <= 0
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
	 * 
	 * @note All class invariants are satisfied upon instantiating a new Worm. Therefore we don't need the @Raw tag.
	 */
	public Worm(Location location, Direction direction, World world, Radius radius, Name name, Team team)
			throws InvalidWormNameException, InvalidRadiusException, InvalidLocationException, IllegalArgumentException {
		super(location, radius, world);
		
		if(world != null && !world.fullyContains(new Circle(location, radius).getBoundingRectangle())) { //TODO DOC
			throw new IllegalArgumentException("Worm placed out of world");
		}
		
		if(world != this.getWorld()) {
			this.setWorld(world);
		}
		
		this.setDirection(direction);
		this.setName(name);
		this.resetActionPoints();
		
		this.setHitPoints(new HP(World.getWormMinHP(), World.getWormMaxHP()));
		
		if(!isValidTeam(team, this)) {
			throw new IllegalArgumentException("Invalid team");
		}
		if(team!=null) {//TODO doc
			team.addWorm(this);
		}
		
		if(this.getWorld() != null) { //TODO
			if(!this.getWorld().isPassable(this)) {
				throw new IllegalStateException("Worm placed out of world on initialization.");
			}
			if(!this.getWorld().isAdjacantToImpassableTerrain(location, radius)) {
				throw new IllegalStateException("Worm not placed near impassable terrain.");
			}
		}
	}

	/**
	 * Checks whether any piece of food overlaps with this worm
	 * 
	 * @post This worm eats the first piece of food in it's world that it overlaps with.
	 * 		| for each food in this.getWorld().getAllObjectsOfType(Food.class)
	 * 		| 	if this.overlapsWith(food) then
	 * 		|		new.getRadius().getRadius() == this.getRadius().getRadius()*GROWTH_MODIFIER
	 * 		|		food.isTerminated()
	 */
	public void checkForFood() throws NotEnoughAPException{
		if(!(this.currentActionPoints >= 8)) {//TODO constants
			return;
			//throw new NotEnoughAPException("Not enough AP to eat.");
		}
		
		if(this.getWorld() == null) {
			return;
		}
		
		boolean bMustRecheckFoodDeletion = false;
		for (GameObject o: this.getWorld().getAllObjectsOfType(Food.class)) {
			if(o instanceof Food) {
				if(this.overlapsWith(o)) {
					this.consumesFood((Food)o);
					o.getWorld().removeGameObject(o);
					this.setActionPoints(getCurrentActionPoints()-8);
					bMustRecheckFoodDeletion = true;
					break;
				}
			}
		}
		if(bMustRecheckFoodDeletion == true) {
			//checkForFood();
		}
	}
	
	/**
	 * Consumes a piece of food that the worm sits on.
	 * 
	 * @param o
	 * 		The piece of food that this worm sits on.
	 * 
	 * @post | !new.getWorld().hasGameObject(o)
	 * 
	 * @post | o.isTerminated()
	 * 
	 * @post | new.getRadius().getRadius() == this.getRadius().getRadius() * GROWTH_MODIFIER
	 */
	public void consumesFood(Food o) {//TODO REWORK
		
		
		//Location beforeGrowth = this.getLocation();
		//double maxAllowed = this.getRadius().getRadius()*0.2;
		double modifier = o.isPoisoned() ? GameObject.SHRINK_MODIFIER : GameObject.GROWTH_MODIFIER;
		Location afterGrowth = this.nearestLocationAfterGrowing(modifier);
		//double distanceMod = beforeGrowth.getDistanceFrom(afterGrowth);
		
		if(afterGrowth==null) {
			//this.grow(this.getLocation());
			this.terminate();
		}else {
			this.grow(afterGrowth, modifier);
			//this.setLocation(afterGrowth);
		}


	}
	
	private final static double FALL_DAMAGE_MOD  = -3;
	
	/**
	 * This function makes this worm fall
	 * 
	 * @post This worm either falls or doesn't
	 * 			| this.getLocation().getY() > new.getLocation().getY()
	 * @post This worm ends up in adjacent to impassable terrain or ends up out of this world
	 * 			| isAdjacentToTerrain(new.getLocation(),this.getRadius(),this.getWorld()) || !isValidWorldLocation(this.getLocation(), this.getWorld())
	 * @post This worm's has it's HP substracted by the fall distance * 3
	 * 			| let fallDistance be in
	 * 			| new.getHitPoint() == new BigInteger(((Integer)((int)(fallDistance*FALL_DAMAGE_MOD*-1))).toString())).add(this.getHitPoints())
	 */
	public void fall() throws InvalidLocationException{
		double fallDistance = 0.0;
		double fallDistanceDelta = 0.1d;
		Location wormLoc = this.getLocation();
		while(this.getWorld().isPassable(wormLoc, this.getRadius())) {
			fallDistance+=fallDistanceDelta;
			wormLoc = (new Location(wormLoc.getX(), wormLoc.getY()-fallDistanceDelta));
			if(!isValidWorldLocation(wormLoc, this.getWorld())||!this.getWorld().isPassable(wormLoc, this.getRadius())) {		
				if(fallDistanceDelta < 0.1d / 100d) {
					break;
				}else {
					wormLoc = (new Location(wormLoc.getX(), wormLoc.getY()+fallDistanceDelta));
					fallDistanceDelta/=10d;
				}
			}
		}
		//The last while step caused an impassable location, therefore, add the delta to the location.
		wormLoc = (new Location(wormLoc.getX(), wormLoc.getY()+fallDistanceDelta));
		this.setLocation(wormLoc);

		this.increaseHitPoints(new BigInteger(((Integer)((int)(fallDistance*FALL_DAMAGE_MOD))).toString()));
		
		if(fallDistance>=1) {
			//System.out.println("You fell: "+fallDistance+"m");
			//System.out.println("HP before: "+this.getHitPoints());
			
			if(this.overlapsAnyOtherWorm()) {
				this.fallOnOtherWorms();
			}
			//System.out.println("HP after: "+this.getHitPoints());
		}
		
	}
	
	/**
	 * Every worm that this worm hits on hitting the ground has half it's HP substracted and added to this worm.
	 * 
	 * @post | let BigInteger sum in
	 * 		 | for each worm in this.getWorld().getAllObjectsOfType(Worm.class)
	 * 		 |		if 	this.overlapsWith(worm) then
	 * 		 |			 sum.add(((Worm) worm).getHitPoints().add(this.getHitPoints()))
	 * 		 |	new.getHitPoints().equals(sun.add(this.getHitPoints()))
	 */
	private void fallOnOtherWorms() {
		for(GameObject worm: this.getWorld().getAllObjectsOfType(Worm.class)) {
			if(worm instanceof Worm) {
				if(worm != this && this.overlapsWith(worm) ) {
					int prev = (int)(((Worm) worm).getHitPoints().doubleValue() / 2d) ;
					((Worm) worm).setHitPoints(new HP(BigInteger.valueOf(prev)));
					this.setHitPoints(new HP(((Worm) worm).getHitPoints().add(this.getHitPoints())));
				}
			}
		}
	}

	/**
	 * Checks whether this worm falls on any other worm different than this one.
	 * 
	 * @return True if and only if there is another worm in this worm's world that overlaps or contains this worm.
	 * 		| for each worm in this.getWorld().getAllObjectsOfType(Worm.class)
	 * 		|	if this.overlapsWith(worm) then
	 * 		|		result == true
	 * 		| result == true
	 */
	private boolean overlapsAnyOtherWorm() {
		if(this.isTerminated()) {//TODO
			return false;
		}
		for(GameObject worm: this.getWorld().getAllObjectsOfType(Worm.class)) {
			if(worm instanceof Worm) {
				if(this.overlapsWith(worm)) {
					return true;
				}
			}
		}
		return false;
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
	 * @post if the given direction is a valid one the given direction is the new direction.
	 * 		In all other cases the same direction remains.
	 * 		|if isValidDirection(direction)
	 * 		|	then
	 * 		|		new.direction == direction
	 * 		|	else
	 * 		|		new.direction == this.getDirection()
	 */
	@Raw
	public void setDirection(Direction direction) {
		//assert isValidDirection(direction);
		if(!isValidDirection(direction)) { //TODO DOC
			throw new IllegalArgumentException("Illegal direction");
		}
		if(isValidDirection(direction)) {
			this.direction = direction;
		}else {
			this.direction = this.getDirection();
		}
	}
	
	/**
	 * Checks whether a given direction is valid.
	 * 
	 * @param direction
	 * 		The given direction we wish to check.
	 * @return return true if and only if a direction is greater or equal to 0
	 * 		and less than 2 * PI
	 * 		| result ==
	 * 		|		(direction >= 0 && direction < 2 * Math.PI)
	 */
	public static boolean isValidDirection(Direction direction) {
		if (direction.getAngle() >= 0 && direction.getAngle() < 2 * Math.PI) {
			return true;
		}
		return false;
	}
	
	/**
	 * A value object Direction containing a worm's direction.
	 */
	private Direction direction = Direction.DEFAULT_DIRECTION;

	/**
	 * getDirection returns the worm its direction.
	 */
	@Basic
	@Raw
	public Direction getDirection() {
		return this.direction;
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
	@Override
	public void generateMass() {
		this.setMass(calculateMass(World.getWormDensity()));
		this.maxActionPoints = this.calculateMaxActionPoints();
	}


	/**
	 * Calculates the maximum amount of action points a worm can have at a certain moment with a certain size (radius).
	 * 
	 * @return returns the maximum amount of AP for a worm with certain mass.
	 * 		| result == Math.round((float) this.getMass())
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
	 * 
	 * @return Returns true if and only if the worm has a max AP equal to it's rounded weight.
	 * 		| result == (Math.round((float) this.getMass()) == maxActionPoints)
	 */
	public boolean hasProperMaxActionPoints() {
		return (Math.round((float) this.getMass()) == maxActionPoints);
	}

	/**
	 * Resets the worm's active Action Points to the worm's maximum amount of AP.
	 * 
	 * @effect Each time function is called, action points get reset to their maximum
	 *       value.
	 *       |this.setActionPoints(this.getMaxActionPoints())
	 */
	public void resetActionPoints() {
		this.setActionPoints(this.getMaxActionPoints());
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
	 * 		| 	(amount >= 0 && amount <= this.maxActionPoints	)
	 */
	public boolean isValidAmountOfActionPoints(int amount) {
		return amount >= 0 && amount <= this.maxActionPoints && Integer.compare(amount, Integer.MAX_VALUE) != 0 && Integer.compare(amount, Integer.MIN_VALUE) != 0;
	}

	/**
	 * Returns the currentActionPoints for the current worm.
	 */
	@Basic
	public int getCurrentActionPoints() {
		return currentActionPoints;
	}

	/**
	 * Checks whether a action is possible based on a given AP cost.
	 * 
	 * @param deltaPoints
	 * 		A given AP cost for completing a certain action.
	 * @return True if and only if the difference between the worm's current AP and that of the given deltaPoints
	 * 		is equal or greater than 0, meaning that the worm had enough AP to complete the action.
	 * 		| result ==
	 * 		|	((this.getCurrentActionPoints() - deltaPoints) >= 0)
	 */
	public boolean isActionCostPossible(int deltaPoints) {
		return (this.getCurrentActionPoints() - deltaPoints) >= 0;
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
	 * Sets this worm's hitpoints
	 * 
	 * @param hitPoints
	 * 		A given amount of HP
	 * 
	 * @post This worm's hitpoints will be equal to the given amount of hitPoints
	 * 		| new.getHitPoints() == hitPoints
	 */
	@Basic @Raw
	public void setHitPoints(HP hitPoints) {
		this.hitpoints = hitPoints;
		
		if(!this.isAlive()) {
			this.terminate();
		}
	}
	
	/**
	 * Returns this worm's HP
	 */
	@Basic
	public BigInteger getHitPoints() {
		return hitpoints.getHp();
	}
	
	/**
	 * Checks whether this worm is alive, meaning if it's HP is greater than 0.
	 * 
	 * @return returns true if and only if this worm's HP is greater than 0
	 * 		| result == hitpoints.getHp().compareTo(new BigInteger("0")) > 0
	 */
	public boolean isAlive() {
		return hitpoints.getHp().compareTo(new BigInteger("0")) > 0;
	}
	
	/**
	 * Adds the give amount to the worm's current HP
	 * @param amount
	 * 		A given long amount
	 * 
	 * @post | new.getHitPoints() ==  HP(hitpoints.getHp().add(BigInteger.valueOf(amount)))
	 */
	public void increaseHitPoints(long amount) {
		this.setHitPoints( new HP (hitpoints.getHp().add(BigInteger.valueOf(amount))));
	}
	
	/**
	 * Adds the give amount to the worm's current HP
	 * @param amount
	 * 		A given BigInteger amount
	 * 
	 * @post | new.getHitPoints() ==  HP(hitpoints.getHp().add((amount)))
	 */
	public void increaseHitPoints(BigInteger amount) {
		this.setHitPoints( new HP (hitpoints.getHp().add((amount))));
	}
	
	/**
	 * This worm's HP
	 */
	private HP hitpoints;
	
	/**
	 * Sets the name of the worm.
	 * 
	 * @param name
	 *      The given name to assign to the worm.
	 * @post Given name becomes the worm its name.
	 * 		|new.getName() == name;
	 * @throws InvalidWormNameException whenever a given name is invalid or not effective. 
	 *      |!isValidName(name)
	 */
	@Raw
	public void setName(Name name) throws InvalidWormNameException {
		if(!isValidName(name)) {
			throw new InvalidWormNameException(name);
		}
		this.name = name;
	}
	
	/**
	 * A value object Name that holds the name of the worm.
	 */
	private Name name = Name.DEFAULT_NAME;

	/**
	 * Checks whether a given name is valid for any and all worms.
	 * 
	 * @param name
	 * 		the given name to check.
	 * @return True if and only if the name is effective and if the given name is effective.
	 * 		| result ==
	 * 		|		(name!=null) && (name.isValid())
	 */
	public static boolean isValidName(Name name) {
		return (name!=null) && (name.isValid());
	}
	
	/**
	 * Checks whether this worm has the same name as a given worm
	 * 
	 * @param other
	 * 		The other worm to compare names with.
	 * @return Returns true if and only if the other worm is effective and it's name is exactly equal to
	 * 		this worm's name.
	 * 		| result == his.getName().equals(other.getName())
	 */
	public boolean hasTheSameNameAs(Worm other) throws IllegalArgumentException{
		if(other == null) {
			throw new IllegalArgumentException("Error in worm hasTheSameNameAs other was null");
		}
		
		return this.getName().equals(other.getName());
	}
	
	/**
	 * Checks whether a given worm has a correct mass to join a team.
	 * 
	 * @param other
	 * 		The other given worm to check/compares mass with.
	 * @return Returns True if and only if the other worm is effective and it's weight is between or equal to
	 * 		this worm's mass times 2 or divided by 2.
	 * 		| result == (other.getMass() <= 2*this.getMass()) || (other.getMass() >= this.getMass()/2)
	 */
	public boolean hasCorrectTeamMass(Worm other) {
		if (other.getMass() >= 2*this.getMass()) {
			return false;
		}
		if(other.getMass() <= this.getMass()/2) {
			return false;
		}
		return true;
	}
	
	/**
	 * returns the given name of the worm.
	 */
	@Basic @Raw
	public String getName() {
		return name.getName();
	}
	
	/**
	 * Handles movement for a worm, defined by both a worm's radius and a given amount of steps.
	 * Recursively handles a given step amount so that in the final function's call the amount of
	 * given steps is exactly 1.
	 * 
	 * @param nbSteps
	 * 		Amount of steps a worm should take.
	 * @throws InvalidLocationException
	 * 		Should the newly generated position for this worm be invalid, than throw an exception
	 * 		| !isValidWorldLocation(this.getFurthestLocationInDirection(bestMoveAngle,this.getRadius().getRadius()), this.getWorld())
	 * 
	 * @post	A worm attempts to move as far possible in the direction it is facing. If the current direction is not possible
	 * 			the worm will attempt to find a slope that is passable and adjacent to impassable terrain.
	 * 			This worm's action points will be adjusted to the movement cost of that movement, if the cost is higher
	 * 			than the worm's current action points, the worm won't move.
	 * 
	 * 			| new.getLocation() == this.getFurthestLocationInDirection(bestMoveAngle,this.getRadius().getRadius())
	 * 			| let totalMovement = new Location(Math.abs(newLocation.getX() - this.getLocation().getX()),Math.abs(newLocation.getY() - this.getLocation().getY())) in
	 * 			| new.getCurrentActionPoints() >= 0
	 * 			| if this.getMovementCost(totalMovement) >= this.getCurrentActionPoints() then
	 * 			|	new.getCurrentActionPoints() == this.getCurrentActionPoints() - this.getMovementCost(totalMovement)
	 * 			| else
	 * 			|	new.getCurrentActionPoints() == this.getCurrentActionPoints()
	 * 
	 * @note Currently moving although AP == 0 is still possible but can easily be implemented.
	 * 	The assignment didn't explicitly mention this should be a feature in the current implementation.
	 * 
	 */	
	//TODO WORM CAN ONLY MOVE IN DIR 0-PI
	public void move() throws InvalidLocationException,IllegalStateException{
		Direction bestMoveAngle = this.getOptimalMovementAngle();
		Location newLocation = this.getFurthestLocationInDirection(bestMoveAngle,this.getRadius().getRadius());
		newLocation = new Location(World.roundingHelper(newLocation.getX(),3), World.roundingHelper(newLocation.getY(),3));
		
		
		if(!isValidWorldLocation(newLocation, this.getWorld())) {
			throw new InvalidLocationException(newLocation);
		}
		
		
		Location totalMovement = new Location(Math.abs(newLocation.getX() - this.getLocation().getX()),Math.abs(newLocation.getY() - this.getLocation().getY()));
		int movementCost = this.getMovementCost(totalMovement);
	
		
		if(movementCost <= this.getCurrentActionPoints()) {
			this.setLocation(newLocation);
			this.setActionPoints(this.getCurrentActionPoints() - this.getMovementCost(totalMovement));
			
			if(this.overlapsAnyOtherWorm()) {
				this.handleWormMoveCollision();
			}
			
//			fall();
		//	System.out.println("");
		}else {
			throw new IllegalStateException("Not enough AP to move.");
		}

	}
	
	/**
	 * Check and Handle all collisions in moving 2 worms on top of each other.
	 * 
	 * 
	 * @post this worm's HP will be subtracted by a random number N divided by the division of the 
	 * 		radius of the largest worm and the sum of both radiuses together. For any worm this worm overlaps with.
	 * 		| for each worm in this.getWorld().getAllObjectsOfType(Worm.class)
	 * 		| 	if this.overlapsWith(worm)
	 * 		| 		let N = (Math.random()*9) +1 in
	 * 		| 		if this.getRadius().getRadius() > other.getRadius().getRadius() then
	 * 		|			let N1 = N/(this.getRadius().getRadius()/(this.getRadius().getRadius() + other.getRadius().getRadius())) in
	 * 		|			new.getHitPoints() == this.getHitPoints().subtract(BigInteger.valueOf((int)Math.round(N-N1)))
	 * 		| 		    other.getHitPoints() == other.getHitPoints().subtract(BigInteger.valueOf((int)Math.round(N1)))
	 * 		| 		else
	 * 		|			let N1 = N/(other.getRadius().getRadius()/(me.getRadius().getRadius() + other.getRadius().getRadius())) in
	 * 		|			other.getHitPoints() == other.getHitPoints().subtract(BigInteger.valueOf((int)Math.round(N-N1)))
	 * 		| 			new.getHitPoints() == this.getHitPoints().subtract(BigInteger.valueOf((int)Math.round(N1)))
	 */
	private void handleWormMoveCollision() {
		for(GameObject worm: this.getWorld().getAllObjectsOfType(Worm.class)) {
			if(worm instanceof Worm) {
				if(this.overlapsWith(worm)) {
					this.handleMoveCollisionHPCost(this,(Worm)worm);
				}
			}
		}
	}
	
	/**
	 * Handle a collision in moving 2 worms on top of each other.
	 * 
	 * @param me
	 * 		supposed to represent this worm
	 * @param other
	 * 		supposed to represent the other worm
	 * 
	 * @post this worm's HP will be subtracted by a random number N divided by the division of the 
	 * 		radius of the largest worm and the sum of both radiuses together.
	 * 		| let N = (Math.random()*9) +1 in
	 * 		| if me.getRadius().getRadius() > other.getRadius().getRadius() then
	 * 		|	let N1 = N/(me.getRadius().getRadius()/(me.getRadius().getRadius() + other.getRadius().getRadius())) in
	 * 		|	me.getHitPoints() == me.getHitPoints().subtract(BigInteger.valueOf((int)Math.round(N-N1)))
	 * 		|   other.getHitPoints() == other.getHitPoints().subtract(BigInteger.valueOf((int)Math.round(N1)))
	 * 		| else
	 * 		|	let N1 = N/(other.getRadius().getRadius()/(me.getRadius().getRadius() + other.getRadius().getRadius())) in
	 * 		|	other.getHitPoints() == other.getHitPoints().subtract(BigInteger.valueOf((int)Math.round(N-N1)))
	 * 		|   me.getHitPoints() == me.getHitPoints().subtract(BigInteger.valueOf((int)Math.round(N1)))
	 */
	private void handleMoveCollisionHPCost(Worm me, Worm other) {
		double N = (Math.random()*9) +1;
		double N1 = N;
		
		if(me.getRadius().getRadius() > other.getRadius().getRadius()) {
			N1 = N/(me.getRadius().getRadius()/(me.getRadius().getRadius() + other.getRadius().getRadius()));
			me.setHitPoints(new HP(me.getHitPoints().subtract(BigInteger.valueOf((int)Math.round(N-N1)))));
			other.setHitPoints(new HP(other.getHitPoints().subtract(BigInteger.valueOf((int)Math.round(N1)))));
		}else {
			N1 = N/(other.getRadius().getRadius()/(me.getRadius().getRadius() + other.getRadius().getRadius()));
			other.setHitPoints(new HP(other.getHitPoints().subtract(BigInteger.valueOf((int)Math.round(N-N1)))));
			me.setHitPoints(new HP(me.getHitPoints().subtract(BigInteger.valueOf((int)Math.round(N1)))));
		}
	}

	/**
	 * Scan for the best direction to move in. Meaning the best ratio between divergence in direction angle and
	 * distance traveled.
	 * 
	 * @return
	 * 			|	let bestDive = 0
	 * 			|	let bestRatio = 0
	 * 			|	for div in [-0.7875d,0.7875d]:
	 * 			|		let tempDirection = new Direction(this.getDirection().getAngle() + div)
	 * 			|		let tempLocation = getFurthestAdjacentLocationInDirection(tempDirection,this.getRadius().getRadius())
	 * 			|		if tempLocation != null && !this.getLocation().equals(tempLocation) then	
	 * 			|			if Math.sqrt(Math.pow(getX() - tempLocation.getX(),2)+Math.pow(getY() - tempLocation.getY(),2)) >= 0.1 then
	 * 			|				let sampleDiv = this.getDirection().getAngle() >= tempDirection.getAngle() ? this.getDirection().getAngle() - tempDirection.getAngle() : tempDirection.getAngle() - this.getDirection().getAngle()
	 * 			|				let ratio = this.getLocation().getDistanceFrom(tempLocation) / sampleDiv
	 * 			|				if ratio > bestRatio then
	 * 			|					bestDiv = div
	 * 			|					bestRatio = ratio
	 * 			|	result == new Direction(this.getDirection().getAngle() + bestDiv)
	 */
	private Direction getOptimalMovementAngle() {
		double bestDiv = 0.0d;
		double bestRatio = 0.0d;
		boolean bFoundAdjacent = false;
		
//		if(this.canMoveStraight()) {
//			return new Direction(this.getDirection().getAngle() + 0.0);	
//		}
		
		for(double div = -0.7875d; div <= 0.7875d; div += 0.0175) { //TODO constants
			div = World.roundingHelper(div,5);
			Direction tempDirection = new Direction(this.getDirection().getAngle() + div);
			Location tempLocation = getFurthestAdjacentLocationInDirection(tempDirection,this.getRadius().getRadius());
			double distance = Math.sqrt(Math.pow(getX() - tempLocation.getX(),2)+Math.pow(getY() - tempLocation.getY(),2));
			if(tempLocation!=null && !tempLocation.equals(this.getLocation()) && distance >= 0.11) {
				//double sampleDiv = this.getDirection().getAngle() >= tempDirection.getAngle() ? this.getDirection().getAngle() - tempDirection.getAngle() : tempDirection.getAngle() - this.getDirection().getAngle();

				double s = Math.atan2(tempLocation.getY() - this.getLocation().getY(),tempLocation.getX() - this.getLocation().getX());
				if(s<=0) {
					s*=-1;
				}
				double sampleDiv = (this.getDirection().getAngle() - s);
				if(sampleDiv<=0) {
					sampleDiv+=Math.PI*2;
					//sampleDiv = Math.abs(sampleDiv);
				}
				double ratio = distance / sampleDiv;

//				if(isAdjacentToTerrain(tempLocation, getRadius(), getWorld())) {
//					System.out.println(tempLocation + "   distance: "+distance +"    ratio: "+ratio);
//				}

				if(ratio >= bestRatio) {
					bFoundAdjacent = true;
					bestDiv = div;
					bestRatio = ratio;
				}
			}
		}
		if(bestDiv>=-0.02&&bestDiv<=0.02) {
			bestDiv = 0;
		}
		if(!bFoundAdjacent) {
			return this.getDirection();
		}
		
		return new Direction(this.getDirection().getAngle() + bestDiv);
	}


	/**
	 * Checks whether this worm can move exactly in the direction it's facing and if this location is adjacent
	 * to terrain.
	 * 
	 * @return Returns true if and only if the furthest location this worm is facing is adjacent to terrain and 
	 * 			the calculated distance to this location greater than 0.1m .
	 * 			| result ==	
	 * 			|	let distance == Math.sqrt(
	 * 			|		Math.pow(getX() - this.getFurthestLocationInDirection(this.getDirection(),this.getRadius().getRadius()).getX(),2) + 
	 * 			|		Math.pow(getY() - this.getFurthestLocationInDirection(this.getDirection(),this.getRadius().getRadius()).getY(),2) )
	 * 			|	in
	 * 			|		isAdjacentToTerrain(this.getFurthestLocationInDirection(this.getDirection(),this.getRadius().getRadius()), this.getRadius(), this.getWorld()) && distance >= 0.1
	 */
	private boolean canMoveStraight() {
		Location tempLocation = getFurthestLocationInDirection(this.getDirection(),this.getRadius().getRadius());
		double distance = Math.sqrt(Math.pow(getX() - tempLocation.getX(),2)+Math.pow(getY() - tempLocation.getY(),2));
		if(isAdjacentToTerrain(tempLocation, this.getRadius(), this.getWorld()) && distance >= 0.1) {
			return true;
		}
//		if(getWorld().isPassable(tempLocation, this.getRadius()) && distance >= 0.1) {
//			return true;
//		}
		return false;
	}

	/**
	 * Returns a location based on a given distance traveled and a given direction to travel in.
	 * 
	 * @param direction
	 * 		A given direction to travel in
	 * @param distance
	 * 		A given distance this worm is supposed to travel.
	 *  
	 * @return Returns the location based on this worm's current location and a given direction and distance.
	 * 		| result.getX() == Math.cos(direction.getAngle()) * distance + this.getX()
	 * 		| result.getY() == Math.sin(direction.getAngle()) * distance + this.getY()
	 */
	@Model
	private Location getStepDirection(Direction direction, double distance) {
		double[] deltaMovement = new double[2];
		deltaMovement[0] = Math.cos(direction.getAngle()) * distance; 
	    deltaMovement[1] = Math.sin(direction.getAngle()) * distance;
	    deltaMovement[0] += this.getX();
	    deltaMovement[1] += this.getY();
	    return new Location(deltaMovement);
	}
	
	/**
	 * Returns the furthest location possible for a worm in a given direction.
	 * 
	 * @param direction
	 * 		A given direction.
	 * @return Returns a worm location that represents the furthest valid location possible in a given direction.
	 * 		A location is possible if it's passable for this worm's radius and within the world.
	 * 		| let Location finish be this.getLocation() in
	 * 		|	for each step in [0,this.getRadius().getRadius()[ :
	 * 		|		let Location temp be getStepDirection(direction,step) in
	 * 		|			if this.getWorld().isPassable(temp,this.getRadius()) && GameObject.isValidWorldLocation(temp, this.getWorld())) 
	 * 		|				then finish = temp
	 * 		|			else result == temp
	 * 		|	if this.getWorld().isPassable(getStepDirection(direction,this.getRadius().getRadius()),this.getRadius()) 
	 * 		|		then result == getStepDirection(direction,this.getRadius().getRadius())
	 * 		|	else
	 * 		|		result == finish
	 */
	public Location getFurthestLocationInDirection(Direction direction, double distance) {
		Location finish = this.getLocation();
		for(double step = this.getRadius().getRadius() * 0.1d; step <= distance; step+=0.1d) {
			Location temp = getStepDirection(direction,step);
			if(this.getWorld()!=null) { //TODO DOC
				if(this.getWorld().isPassable(temp,this.getRadius()) && GameObject.isValidWorldLocation(temp, this.getWorld())) {
					//if(isAdjacentToTerrain(temp, getRadius(), getWorld())) {
						finish = temp;
					//}
				}else {
					return finish;
				}
			}
		}
		
		//This ensures the final step is checked as well, as in radius is 3.4f step 1,2,3 will be checked in
		//the for loop, this if checks whether 3.4f explicitly is possible.
		Location furthestLocation = getStepDirection(direction,distance); 
		if(this.getWorld() == null ||this.getWorld().isPassable(furthestLocation,this.getRadius())) {
			//if(isAdjacentToTerrain(furthestLocation, getRadius(), getWorld())) {
				return furthestLocation;
			//}
		}
		
		return finish;
	}
	
	public Location getFurthestLocationInDirectionNoMove(Direction direction, double distance) {
		if(!(direction.getAngle() >= 0 && direction.getAngle() <= Math.PI)) {
			throw new IllegalArgumentException();
		}
		if(distance<=0 || Double.isInfinite(distance)) {
			throw new IllegalArgumentException();
		}
		Location finish = this.getLocation();
		for(double step = this.getRadius().getRadius() * 0.1d; step <= distance; step+=0.01d) {
			step = World.roundingHelper(step, 3);
			Location temp = getStepDirection(direction,step);
			if(this.getWorld()!=null) { //TODO DOC
				if(this.getWorld().isPassable(temp,this.getRadius()) ) {
					//if(isAdjacentToTerrain(temp, getRadius(), getWorld())) {
						finish = temp;
					//}
				}else {
					return finish;
				}
			}
		}
		
		//This ensures the final step is checked as well, as in radius is 3.4f step 1,2,3 will be checked in
		//the for loop, this if checks whether 3.4f explicitly is possible.
		Location furthestLocation = getStepDirection(direction,distance); 
		if(this.getWorld() == null ||this.getWorld().isPassable(furthestLocation,this.getRadius())) {
			//if(isAdjacentToTerrain(furthestLocation, getRadius(), getWorld())) {
				return furthestLocation;
			//}
		}
		
		return finish;
	}
	
	/**
	 * Returns the furthest location possible for a worm in a given direction.
	 * 
	 * @param direction
	 * 		A given direction.
	 * @return Returns a worm location that represents the furthest valid location possible in a given direction.
	 * 		A location is possible if it's passable for this worm's radius and within the world.
	 * 		| let Location finish be this.getLocation() in
	 * 		|	for each step in [0,this.getRadius().getRadius()[ :
	 * 		|		let Location temp be getStepDirection(direction,step) in
	 * 		|			if this.getWorld().isPassable(temp,this.getRadius()) && GameObject.isValidWorldLocation(temp, this.getWorld())) 
	 * 		|				then finish = temp
	 * 		|			else result == temp
	 * 		|	if this.getWorld().isPassable(getStepDirection(direction,this.getRadius().getRadius()),this.getRadius()) 
	 * 		|		then result == getStepDirection(direction,this.getRadius().getRadius())
	 * 		|	else
	 * 		|		result == finish
	 */
	public Location getFurthestAdjacentLocationInDirection(Direction direction, double distance) {
		Location finish = this.getLocation();
		for(double step = 0.0; step <= distance; step+=0.1) { //start at 0.0
			Location temp = getStepDirection(direction,step);
//			if(temp.getDistanceFrom(this.getLocation()) <= this.getRadius().getRadius()*.2) {
//				continue;
//			}
			if(step <= this.getRadius().getRadius()*.2) {
				continue;
			}
			if(this.getWorld().isPassable(temp,this.getRadius())&& GameObject.isValidWorldLocation(temp, this.getWorld())) {
				if( isAdjacentToTerrain(temp, this.getRadius(), this.getWorld()) ) {
					finish = temp;
				}
			}else {
				return finish;
			}
		}	
		
		Location temp = getStepDirection(direction,distance); //Try the furthest possible distance
		if(this.getWorld().isPassable(temp,this.getRadius())&& GameObject.isValidWorldLocation(temp, this.getWorld())) {
			if( isAdjacentToTerrain(temp, this.getRadius(), this.getWorld()) ) {
				return temp;
			}
		}
		
		return finish;
		
	}
	
	/**
	 * Returns the cost in AP of a worm based on it's current direction for moving.
	 * An AP cost is always handled as an integer. The cost is therefore rounded up.
	 * 
	 * @parameter movement
	 * 		Actual movement in meters being done
	 * 
	 * @return returns the movement cost for a single step for a worm. As an integer.
	 * 		| result ==
	 * 		|	(int) Math.ceil(Math.abs(Math.cos(this.getDirection()))*movement.getX() + Math.abs(4*Math.sin(this.getDirection())*movement.getY()))
	 */
	public int getMovementCost(Location movement) {
		int cost = 0;
		cost = (int) (Math.ceil(Math.abs(Math.cos(this.getDirection().getAngle()))*movement.getX()) + Math.ceil(Math.abs(4*Math.sin(this.getDirection().getAngle())*movement.getY())));
		
		return cost;
	}
	
	/**
	 * Changes the direction of the current worm based on a given difference between the new and old direction.
	 * This function also handles the cost of changing the given angle amount.
	 * 
	 * @param angle
	 * 		| A given angle representing the difference between the new angle and previous angle of the worm.
	 * 
	 * @pre the worm's current direction is a valid angle between 0 or 2*PI radians, 0 included.
	 * 		| isValidDirection(this.getDirection())
	 * @pre the sum between the worm's current direction and the given angle
	 * 		is a valid angle between 0 or 2*PI radians, 0 included.
	 * 		| isValidDirection(this.getDirection().getAngle() + angle)
	 * @pre the cost for turning the given angle should be possible.
	 * 		| this.isActionCostPossible(this.getTurnCost(angle))
	 * 
	 * @effect AP cost of changing direction is handled in setDirection
	 * 		| setDirection(new Direction(this.getDirection().getAngle() + angle.getAngle()))
	 * 
	 * @post The worm's AP shall be reduced by the turnCost amount of the angle,
	 * 		if the cost is higher than the worm's AP a cost of 0 will be applied (no cost).
	 * 		|if isActionCostPossible(this.getTurnCost(angle))
	 * 		|	then
	 * 		|		new.getCurrentActionPoints() == this.getCurrentActionPoints() - this.getTurnCost(angle)
	 * 		|	else
	 * 		|		new.getCurrentActionPoints() == this.getCurrentActionPoints()
	 * @post The worm shall turn the given angle, unless the new direction is invalid or the cost
	 * 		of turning is higher than the worm's AP. In those cases a worm doesn't turn.
	 * 		| if isValidDirection(newDirection)
	 * 		|	then
	 * 		|		new.getDirection() == new Direction(this.getDirection().getAngle() + angle.getAngle())
	 * 		|	else
	 * 		|		new.getDirection() == this.getDirection()
	 * 
	 * @note Currently turning altough AP == 0 is still possible but can easily be implemented.
	 * 	The assignment didn't explicitly mention this should be a feature in the current implementation.
	 */
	public void turn(double angle) throws RuntimeException {
		assert isValidDirection(this.getDirection());
		//assert isValidDirection(new Direction(this.getDirection().getAngle() + angle));
		if(!isValidDirection(new Direction(this.getDirection().getAngle() + angle))) {
			throw new RuntimeException();
		}
		//assert this.isActionCostPossible(this.getTurnCost(angle));
		if(!this.isActionCostPossible(this.getTurnCost(angle))) {
			throw new NotEnoughAPException("Not enough AP to turn");
		}
		
		
		Direction newDirection = new Direction(this.getDirection().getAngle() + angle);
		
		if(!isValidDirection(newDirection)) {
			newDirection = this.getDirection();
			angle = 0;
		}
		if(!isActionCostPossible(this.getTurnCost(angle))) {
			newDirection = this.getDirection();
			angle = 0;
		}
		this.setDirection(newDirection);
		this.setActionPoints(this.getCurrentActionPoints() - this.getTurnCost(angle));
	}
	
	/**
	 * This function returns the AP cost to complete the rotation of a certain angle.
	 * 
	 * @param angle
	 * 		A given angle in radians
	 * @return Returns the cost of the rotationary angle based on a math formula.
	 * 		| result ==
	 * 		|	Math.abs((int)Math.ceil((angle/(2*Math.PI)*60)))
	 */
	public int getTurnCost(double angle) {
		int cost = 0;
		cost = Math.abs((int)Math.ceil((angle/(2*Math.PI)*60)));
		return cost;
	}
	
	/**
	 * Makes the worm jump if it's got a valid jumpSpeedMagnitude, bigger than 0 and a valid less than infinity
	 * magnitude.
	 * 
	 * @param timeStep
	 * 		The resolution of the time between the 'steps' of a jump
	 * 
	 * @post The worm's new x coordinate will be equal to that of it's original location + the jumpdistance
	 *		Or the worm's current jumpSpeedMagnitude is zero or invalid.
	 * 		| new.getLocation().equals(Location(this.jumpStep(this.getJumpTime(timeStep))))
	 * @post This worm's AP will be set to 0 after jumping
	 * 		| new.getCurrentActionPoints() == 0
	 * @post this worm's HP will be equal or less than before the jump
	 * 		| new.getHitPoints().compareTo(this.getHitPoints()) <= 0
	 * 
	 * @throws RuntimeException
	 * 		|this.getCurrentActionPoints() == 0 ||
	 * 		|!isValidJumpSpeedMagnitude(this.getJumpSpeedMagnitude()) ||
	 * 		|!isValidJumpTime(this.getJumpTime(timeStep))
	 * 
	 * @throws InvalidLocationException
	 * 		|!isValidLocation(Location(this.jumpStep(this.getJumpTime(timeStep))),this.getWorld())
	 * 
	 * @note We currently disabled being able to jump when AP == 0.
	 */
	public void jump(double timeStep) throws InvalidLocationException,RuntimeException{
		if(!(this.getDirection().getAngle() >= 0 && this.getDirection().getAngle() <= Math.PI)) {
			throw new RuntimeException();
		}
		
		if(this.getCurrentActionPoints() == 0) {
			throw new NotEnoughAPException("No AP left to jump.");
		}
		
		double jumpSpeedMagnitude = this.getJumpSpeedMagnitude();
		
		if(!isValidJumpSpeedMagnitude(jumpSpeedMagnitude)) {
			throw new RuntimeException(""+jumpSpeedMagnitude);
		}
		
		double jumpTime = this.getJumpTime(timeStep);
		
		if(!isValidJumpTime(jumpTime)) {
			throw new RuntimeException(""+jumpTime);
		}
		
		Location locationBeforeJump = this.getLocation();
		Location tmp = new Location(this.jumpStep(jumpTime));
		
		if(!isValidLocation(tmp,this.getWorld())) {
			throw new InvalidLocationException(tmp);
		}
		
		this.setLocation(tmp);				
		this.setActionPointsAfterJump();	
		
		if(isAdjacentToTerrain(this) && !locationBeforeJump.equals(this.getLocation())) {
			this.checkForWormOverlapsAfterJump();
		}
	}

	/**
	 * Checks and fights the worms this overlaps with after a jump.
	 * 
	 * @post A collision happens between two worms after a jump if this worm (partially) overlaps any other worm
	 * 		in this world. If a collision happens, a coin toss (50-50 chance) will determine which worm takes damage
	 * 		based on the radius of the attacker and defender.
	 * 		|for each worm in this.getWorld().getAllObjectsOfType(Worm.class)
	 * 		|	if worm != this && worm.overlapsWith(this) then
	 * 		|		let attacker = this
	 * 		|		let defender = (Worm)worm
	 * 		|		if((int)Math.round(Math.random()) == 1) then
	 * 		|			attacker = (Worm)worm
	 * 		|			defender = this
	 * 		|		defender.getHitPoints().intValue() == 
	 * 		|			(defender.getHitPoints().intValue() - 10* ((int)Math.floor(attacker.getRadius().getRadius() / defender.getRadius().getRadius())))
	 * 		|			|| 0
	 */
	private void checkForWormOverlapsAfterJump() {
		for (GameObject worm : this.getWorld().getAllObjectsOfType(Worm.class)) {
			if(worm instanceof Worm && worm != this) {
				if(worm.overlapsWith(this)) {
					int coinValue = (int)Math.round(Math.random());
					Worm attacker = this;
					Worm defender = (Worm)worm;
					if(coinValue == 1) {
						attacker = (Worm)worm;
						defender = this;
					}
					int damage = 10* ((int)Math.floor(attacker.getRadius().getRadius() / defender.getRadius().getRadius()));
					defender.increaseHitPoints(damage * -1);
					
				}
			}
		}
	}

	/**
	 * Sets the worm's current AP to a certain default amount after jumping.
	 * 
	 * @effect The worm's AP will be set to a default amount
	 * 		| this.setActionPoints(0)
	 */
	public void setActionPointsAfterJump() {
		this.setActionPoints(0);
	}
	
	/**
	 * Checks whether a given velocity magnitude is a valid one.
	 * 
	 * @param jumpSpeed
	 * 		The given jumpSpeed to check.
	 * @return True when the given magnitude is larger than 0, not infinite and a valid number.
	 * 		| result ==
	 * 		|	 ((jumpSpeed > 0) && !Double.isNaN(jumpSpeed) && !Double.isInfinite(jumpSpeed))
	 */
	public static boolean isValidJumpSpeedMagnitude(double jumpSpeed) {
		if (jumpSpeed < 0) {
			return false;
		}
		else if (Double.isNaN(jumpSpeed)) {
			return false;
		}
		else if (Double.isInfinite(jumpSpeed)){
			return false;
		}
		return true;
	}
	
	public static boolean isValidJumpTime(double jumpSpeed) {
		if (jumpSpeed < 0) {
			return false;
		}
		else if (Double.isNaN(jumpSpeed)) {
			return false;
		}
		else if (Double.isInfinite(jumpSpeed)){
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the worm's current jump velocity magnitude. as v0
	 * 
	 * @result |((5D*this.getCurrentActionPoints())+(this.getMass()*World.getGravity())/this.getMass())*World.getJumpTimeDelta()
	 */
	public double getJumpSpeedMagnitude() {
		double jumpForce = (5D*this.getCurrentActionPoints())+(this.getMass()*World.getGravity());
		return (jumpForce/this.getMass())*World.getJumpTimeDelta();
	}

	/**
	 * This function returns a theoretical position for a worm performing a jump after a
	 * given amount of jumptime has passed.
	 * 
	 * @param deltaTime
	 * 		A given time representation based on the total jump time for a worm.
	 * @return Returns a theoretical position for a worm performing a jump after a certain given time has passed.
	 * 		| let double[] tmpLocation = new double[2]
	 * 		|		in
	 * 		| tmpLocation[0] = this.getX() + (this.getJumpSpeedMagnitude()*Math.cos(this.getDirection())*deltaTime)
	 * 		| tmpLocation[1] = this.getY() + ((this.getJumpSpeedMagnitude()*Math.sin(this.getDirection())*deltaTime) - (((float)1/(float)2)*GRAVITY*Math.pow(deltaTime,2)))
	 * 		| result == tmpLocation
	 * @throws InvalidLocationException
	 * 		This function returns an exception if a calculated position is an invalid one for a worm.
	 * 		| !isValidLocation(tmpLocation)
	 */
	public double[] jumpStep(double deltaTime) throws InvalidLocationException,IllegalArgumentException,RuntimeException{
		if(this.getCurrentActionPoints() == 0) {
			throw new NotEnoughAPException("");
		}
		
		if(!isValidJumpTime(deltaTime)) {
			throw new IllegalArgumentException(((Double)deltaTime).toString());
		}
		
		if(!(this.getDirection().getAngle() >= 0 && this.getDirection().getAngle() <= Math.PI)) {
			throw new RuntimeException("The direction of the worm trying to jump is invalid. Not equal or larger than 0 and less than 2*PI." + this.getDirection().toString());
		}
		
		double jumpSpeedMagnitude = this.getJumpSpeedMagnitude();
		if(jumpSpeedMagnitude < 0) {
			throw new RuntimeException("The jumpSpeedMagnitude of the worm tring to jump is invalid. Less than 0 "+jumpSpeedMagnitude);
		}
		
		//speed in air
		double speedX = jumpSpeedMagnitude * Math.cos(this.getDirection().getAngle());
		double speedY = jumpSpeedMagnitude * Math.sin(this.getDirection().getAngle());
		//Position in air
		double xPosTime = this.getX()+(speedX*deltaTime);
		double yPosTime = this.getY()+((speedY*deltaTime) - ((1D/2D)*World.getGravity()*Math.pow(deltaTime,2)));
		
		Location tmpLocation = new Location(xPosTime,yPosTime);
//		if(tmpLocation.getY() > this.getY()) {
//			tmpLocation = new Location(xPosTime,this.getY());
//		}
		
		if(!isValidLocation(tmpLocation,this.getWorld())) {
			throw new InvalidLocationException(tmpLocation);
		}
		

		
		return tmpLocation.getLocation();
	}
	
	/**
	 * Calculates and returns this worm's total theoretical air time.
	 * 
	 * @return 	| let distance = Math.pow(this.getJumpSpeedMagnitude(), 2)*Math.sin(this.getDirection().getAngle()*2D)/World.getGravity() in
	 * 			| result == (distance/(this.getJumpSpeedMagnitude()*Math.cos(this.getDirection().getAngle())))
	 */
	public double calculateJumpTime() {
//		double speedY = this.getJumpSpeedMagnitude() * Math.sin(this.getDirection().getAngle());
//		double time = speedY/World.getGravity();
//		
		double distance = Math.pow(this.getJumpSpeedMagnitude(), 2)*Math.sin(this.getDirection().getAngle()*2D)/World.getGravity();
		double timeInterval = (distance/(this.getJumpSpeedMagnitude()*Math.cos(this.getDirection().getAngle())));
		return timeInterval;
	}

	/**
	 * Calculates and returns this worm actual exact air time for a jump based on valid step locations during a jump.
	 * A time resolution per step in the jump is given as deltaT.
	 * Any step deltaT within a jump must be valid and within the current world to be valid.
	 * 
	 * @param deltaT
	 * 		Time resolution for a step in a jump.
	 * @return
	 * 		| result == this.getLastPassableJumpStepTime(this.calculateJumpTime(),deltaT)
	 * 
	 * @throws RuntimeException
	 * 		| !isValidJumpTime(this.calculateJumpTime())
	 */
	public double getJumpTime(double deltaT) throws RuntimeException{
		if(this.currentActionPoints == 0) {
			return 0;
		}
		
		double jumpTime = this.calculateJumpTime();
		if(!isValidJumpTime(jumpTime)) {
			throw new RuntimeException("Invalid jumptime calculated. "+jumpTime);
		}
		
		double jumptime = this.getLastPassableJumpStepTime(jumpTime,deltaT);
		if(jumptime == 0) { //TODO DOC
			throw new IllegalArgumentException("Deltatime and jumpTime too short.");
		}
		
		return jumptime;
	}
	
	/**
	 * Based on a total jumptime and a deltaT step time return the total actual air time of this worm
	 * based on whether it's jumplocation per step is passable or not.
	 * 
	 * @param jumpTime
	 * 		Total theoretical possible jumptime.
	 * @param deltaT
	 * 		Time per step in the jump
	 * 
	 * @return Return the exact amount of jumptime of a worm for a valid jump in it's current direction.
	 * 		A jumpstep is valid if the location on certain time is passable for this worm.
	 * 			| for i in [0,jumptime[
	 * 			|	if !this.getWorld().isPassable(Location(this.jumpStep(i)), this.getRadius()) then
	 * 			|		result == i - deltaT
	 * 			| if !this.getWorld().isPassable(Location(this.jumpStep(jumpTime)), this.getRadius()) then
	 * 			|	result == jumpTime-deltaT
	 * 			| else
	 * 			|	result == jumpTime
	 * 			
	 */
	private double getLastPassableJumpStepTime(double jumpTime, double deltaT) {
		Location lastAdjacentLocation = null;
		double lastAdjacentTime = 0.0;
		for (double i = deltaT; i < jumpTime; i+=deltaT) {
			Location wormLoc = new Location(this.jumpStep(i));
			if(this.getWorld().isAdjacantToImpassableTerrain(wormLoc, this.getRadius())) {
				lastAdjacentLocation = wormLoc;
				lastAdjacentTime = i;
			}
			if(!this.getWorld().isPassable(wormLoc, this.getRadius())) { //TODO Doc
//				if(this.getLocation().getDistanceFrom(wormLoc) < this.getRadius().getRadius()) {
//					return 0; //THIS WILL CAUSE A CONTROLLED EXCEPTION
//				}
				//return i - deltaT;
				if(lastAdjacentLocation!=null && this.getLocation().getDistanceFrom(lastAdjacentLocation) < this.getRadius().getRadius()) {
					return 0; //THIS WILL CAUSE A CONTROLLED EXCEPTION
				}
				//This means the latest jumpstep is invalid, therefore the one step before that was.
				return lastAdjacentTime;
			}
		}
		
		//This checks the very last moment of the jump, assuming all other steps were valid.
//		Location wormLoc = new Location(this.jumpStep(jumpTime));
//		if(!this.getWorld().isPassable(wormLoc, this.getRadius())) {
//			return jumpTime-deltaT;
//		}
		//return jumpTime;
		return lastAdjacentTime;
	}

	/**
	 * Resets this worm's turn.
	 * 
	 * @post | new.getCurrentActionPoint() == this.getMaxActionPoints()
	 * 
	 * @post | new.getHitPoints().equals(new BigInteger("10").add(this.getHitPoints()))
	 */
	public void resetTurn() {
		this.resetActionPoints();
		this.increaseHitPoints(10);
	}

	/**
	 * Checks whether this worm must end it's turn
	 * 
	 * @return | (result == this.getHitPoints().compareTo(new BigInteger ("0"))== 0) || (this.getCurrentActionPoints() == 0)
	 */
	public boolean mustEndTurn() {
		if(this.getHitPoints().compareTo(new BigInteger ("0"))== 0) {
			return true;
		}
		if(this.getCurrentActionPoints() == 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks whether this worm can move after consuming a piece of food in a valid directions.
	 * 
	 * @return
	 */
	public boolean canRepositionAfterFoodConsumption() {
		return true;
	}
	
	/**
	 * Sets the team for this worm
	 * 
	 * @post this worm's team shall be equal to the given team.
	 * 		| new.getTeam() == team
	 */
	@Basic @Raw
	public void setTeam(Team team) {
		this.team = team;
	}
	
	/**
	 * Returns this worm's team
	 */
	@Basic @Raw
	public Team getTeam() {
		return this.team;
	}
	
	/**
	 * Checks whether the given team is valid for any and all worms
	 * 
	 * @param team
	 * 		A given team the given worm is supposed to be part of.
	 * @param worm
	 * 		A given worm.
	 * @return | result == (team == null) || team.canAddWormToTeam(worm)
	 */
	public static boolean isValidTeam(Team team, Worm worm) {
		return  team == null || team.canHaveWormInTeam(worm);
	}
	
	/**
	 * The team this worm is part of.
	 */
	private Team team;
	
	@Override
	public void terminate() {
		if(this.getTeam() != null) {
			this.getTeam().removeWorm(this);
			this.setTeam(null);
		}
		
		if(this.getWorld() != null) {
			this.getWorld().removeFromTurnCycle(this);	
		}
		
		super.terminate();
	}

	private Program loadedScript = null;
	
	public void assignProgram(Program program, IActionHandler handler) throws IllegalStateException{
		if(this.getWorld()!= null) {
			if(this.getWorld().getIsGameActive()) {
				throw new IllegalStateException("Tried to add program during game.");
			}
		}
		this.setProgram(program);
		if(program != null && program.getProgramHolder() != this) {
			program.setProgramHolder(this);
			program.setActionHandler(handler);
		}
	}
	
	public void setProgram(Program program) {
		this.loadedScript = program;
	}
	
	public Program getProgram() {
		return this.loadedScript;
	}

	public Projectile fire() throws IllegalStateException{
		if(this.getWorld() == null) {
			throw new IllegalStateException();
		}
		for (GameObject bullet : this.getWorld().getAllObjectsOfType(Projectile.class)) {
			if(bullet instanceof Projectile) {
				if(bullet.overlapsWith(this)) {
					this.hitByProjectile((Projectile)bullet);
					bullet.terminate();
					return null;
				}
			}
		}
		
		int amountOfGuns = Projectile.Projectile_Type.values().length;
		int randomGun = (int)(Math.random() * amountOfGuns);
		CreateProjectile gun = Projectile.getRifle(Projectile_Type.values()[randomGun]); //Chooses a random enum value
		try {
			Projectile firedProjectile = gun.create(this);
			if(firedProjectile.getCostAP() <= this.getCurrentActionPoints()) {
				this.getWorld().addGameObject(firedProjectile);
				this.setActionPoints(this.getCurrentActionPoints() - firedProjectile.getCostAP());
			}else {
				firedProjectile = null;
			}
			return firedProjectile;
		} catch (Exception e) {
			return null;
		}

	}

	public void hitByProjectile(Projectile projectile) {
		this.setHitPoints(new HP(this.getHitPoints().subtract(BigInteger.valueOf(projectile.getHitPoints()))));
	}
}

interface IAction{
	
}

interface IJumpable extends IAction{
	public void jump(double timeStep);
	public double getJumpTime(double deltaT);
	public double[] jumpStep(double deltaTime) throws InvalidLocationException,IllegalArgumentException,RuntimeException;
}



