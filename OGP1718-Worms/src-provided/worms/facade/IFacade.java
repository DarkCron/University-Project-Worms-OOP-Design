package worms.facade;

import java.math.BigInteger;
import java.util.*;
import worms.model.*;
import worms.util.ModelException;
import worms.util.MustNotImplementException;

/**
 * Implement this interface to connect your code to the graphical user interface
 * (GUI).
 * 
 * <ul>
 * <li>For separating the code that you wrote from the code that was provided to
 * you, put <b>ALL your code in the <code>src</code> folder.</b> The code that
 * is provided to you stays in the <code>src-provided</code> folder. If you
 * modify the provided code, you may need to manually merge any future bugfixes
 * and update.</li>
 * 
 * <li>You should at least create the following classes for the code to compile:
 * <ul>
 * <li>a class <code>Worm</code> in the package <code>worms.model</code> for
 * representing a worm</li>
 * <li>a class <code>World</code> in the package <code>worms.model</code> for
 * representing a world</li>
 * <li>a class <code>Food</code> in the package <code>worms.model</code> for
 * representing a food ration</li>
 * <li>a class <code>Team</code> in the package <code>worms.model</code> for
 * representing a team (only for teams of 2 students)</li>
 * <li>a class <code>Facade</code> in the package <code>worms.facade</code> that
 * implements this interface (<code>IFacade</code>).</li>
 * </ul>
 * You may, of course, add additional classes as you see fit.
 * 
 * <li>The header of that Facade class should look as follows:<br>
 * <code>class Facade implements IFacade { ... }</code><br>
 * Consult the <a href=
 * "http://docs.oracle.com/javase/tutorial/java/IandI/createinterface.html">
 * Java tutorial</a> for more information on interfaces, if necessary.</li>
 * 
 * <li>Each method defined in the interface <code>IFacade</code> must be
 * implemented by the class <code>Facade</code>. For example, the implementation
 * of <code>getMass</code> should call a method of the given <code>worm</code> to
 * retrieve its mass.</li>
 * 
 * <li>Your <code>Facade</code> class should offer a default constructor.</li>
 * 
 * <li>Methods in this interface are allowed to throw only
 * <code>ModelException</code>. No other exception types are allowed. This
 * exception can be thrown only if (1) calling the method with the given
 * parameters would violate a precondition, or (2) if the method causes an
 * exception to be thrown in your code (if so, wrap the exception in a
 * <code>ModelException</code>).</li>
 * 
 * <li><b>ModelException should not be used anywhere outside of your Facade
 * implementation.</b></li>
 * 
 * <li>Your Facade implementation should <b>only contain trivial code</b> (for
 * example, calling a method, combining multiple return values into an array,
 * creating @Value instances, catching exceptions and wrapping it in a
 * ModelException). All non-trivial code should be placed in the other classes
 * that you create.</li>
 * 
 * <li>The rules described above and the documentation described below for each
 * method apply only to the class implementing IFacade. Your class for
 * representing worms should follow the rules described in the assignment.</li>
 * 
 * <li>Do not modify the signatures of the methods defined in this
 * interface.</li>
 * 
 * </ul>
 */
public interface IFacade {

	
	/************
	 * WORLD
	 ************/

	/**
	 * Create a new world with given width and given height.
	 *   The passable map is a rectangular matrix indicating which parts of the terrain
	 *   are passable and which parts are impassable.
	 *   This matrix is derived from the transparency of the pixels in the image file
	 *   of the terrain. passableMap[r][c] is true if the location at row r and column c
	 *   is passable, and false if that location is impassable.
	 *   The elements in the first row (row 0) represent the pixels at the top of the
	 *   terrain (i.e., largest y-coordinates).
	 *   The elements in the last row (row passableMap.length-1) represent pixels at the
	 *   bottom of the terrain (smallest y-coordinates).
	 *   The elements in the first column (column 0) represent the pixels at the left
	 *   of the terrain (i.e., smallest x-coordinates).
	 *   The elements in the last column (column passableMap[0].length-1) represent the
	 *   pixels at the right of the terrain (i.e., largest x-coordinates).     
	 */
	public World createWorld(double width, double height,
			boolean[][] passableMap) throws ModelException;
	
	/**
	 * Terminate the given world.
	 */
	void terminate(World world) throws ModelException;
	
	/**
	 * Check whether the given worls is terminated.
	 */
	boolean isTerminated(World world) throws ModelException;
	
	/**
	 * Return the width of the given world.
	 */
	public double getWorldWidth(World world) throws ModelException;
	
	/**
	 * Return the height of the given world.
	 */
	public double getWorldHeight(World world) throws ModelException;
	
	/**
	 * Check whether the given world is passable at the given location. 
	 *   - The location is an array containing the x-coordinate of the location to
	 *     check followed by the y-coordinate of that location.
	 *   - Locations outside the boundaries of the world are always passable.
	 */
	boolean isPassable(World world, double[] location) throws ModelException;

	/**
	 * Check whether the circular area with given center and given radius
	 * is passable in the given world.
	 *   - The circular area must not lie completely within the given world.
	 */
	boolean isPassable(World world, double[] center, double radius);

	/**
	 * Check whether the circular area with given center and given radius
	 * is adjacent to impassable terrain in the given world.
	 *   - The circular area must not lie completely within the given world.
	 */
	boolean isAdjacent(World world, double[] center, double radius);
	
	/**
	 * Check whether the given world contains the given worm.
	 */
	boolean hasAsWorm(World world, Worm worm) throws ModelException;

	/**
	 * Add the given worm to the given world.
	 */
	void addWorm(World world, Worm worm) throws ModelException;
	
	/**
	 * Remove the given worm from the given world.
	 */
	void removeWorm(World world, Worm worm) throws ModelException;

	/**
	 * Return a list filled with all the worms in the given world.
	 */
	List<Worm> getAllWorms(World world) throws ModelException;
	
	/**
	 * Check whether the given world contains the given food.
	 */
	boolean hasAsFood(World world, Food food) throws ModelException;

	/**
	 * Add the given portion of food to the given world.
	 */
	void addFood(World world, Food food) throws ModelException;
		
	/**
	 * Remove the given portion of food from the given world.
	 */
	void removeFood(World world, Food food) throws ModelException;
		
	/**
	 * Return a collection filled with all the worms and all the
	 * portions of food in the given world.
	 */
	Collection<Object> getAllItems(World world) throws ModelException;

	/**
	 * Return a set of all the team in the given world.
	 */
	Set<Team> getAllTeams(World world) throws ModelException;
		
	/**
	 * Check whether the given world has an active game.
	 */
	boolean hasActiveGame(World world) throws ModelException;

	/**
	 * Return the active worm in the given world.
	 *   - The active worm is the worm whose turn it is to perform
	 *     player-controlled actions.
	 */
	Worm getActiveWorm(World world) throws ModelException;
	
	/**
	 * Start a new game in the given world.
	 */
	void startGame(World world) throws ModelException;
	
	/**
	 * Finish the current game, if any, in the given world.
	 */
	void finishGame(World world) throws ModelException;

	/**
	 * Activate the next worm in the given world.
	 */
	void activateNextWorm(World world) throws ModelException;

	/**
	 * Return the name of a single worm if that worm is the winner, or the name
	 * of a team if that team is the winner and the team still has several members.
	 */
	String getWinner(World world);


	/************
	 * WORM
	 ************/
	
	/**
	 * Create and return a new worm that is positioned at the given location in
	 * the given world, that looks in the given direction, that has the given radius
	 * and the given name, and that is a member of the given team.
	 *   - If the given world is not effective, the new worm is simply positioned
	 *     at the given location.
	 *   - If the given team is not effective, the new worm is not part of any team.
	 *     The location is an array containing the x-coordinate of the location of
	 *     the new worm followed by the y-coordinate of that location.
	 */
	Worm createWorm(World world, double[] location, double direction, double radius,
			String name, Team team) throws ModelException;
	
	/**
	 * Terminate the given worm.
	 */
	void terminate(Worm worm) throws ModelException;
	
	/**
	 * Check whether the given worm is terminated.
	 */
	boolean isTerminated(Worm worm) throws ModelException;

	/**
	 * Return the current location of the given worm.
	 *   - The resulting array contains the the x-coordinate of the given worm
	 *     followed by its y-coordinate.
	 */
	double[] getLocation(Worm worm) throws ModelException;

	/**
	 * Return the current orientation of the given worm (in radians).
	 */
	double getOrientation(Worm worm) throws ModelException;

	/**
	 * Return the radius of the given worm.
	 */
	double getRadius(Worm worm) throws ModelException;

	/**
	 * Set the radius of the given worm to the given value.
	 */
	void setRadius(Worm worm, double newRadius) throws ModelException;

	/**
	 * Return the mass of the given worm.
	 */
	double getMass(Worm worm) throws ModelException;

	/**
	 * Return the maximum number of action points of the given worm.
	 */
	long getMaxNbActionPoints(Worm worm) throws ModelException;

	/**
	 * Return the current number of action points of the given worm.
	 */
	long getNbActionPoints(Worm worm) throws ModelException;

	/**
	 * Decrease the current number of action points of the given worm
	 * with the given delta.
	 *   - The given delta may be negative.
	 */
	void decreaseNbActionPoints(Worm worm, long delta) throws ModelException;

	/**
	 * Return the current number of hit points of the given worm.
	 */
	BigInteger getNbHitPoints(Worm worm) throws ModelException;

	/**
	 * Increment the current number of hit points of the given worm
	 * with the given delta.
	 *   - The given delta may be negative.
	 */
	void incrementNbHitPoints(Worm worm, long delta) throws ModelException;

	/**
	 * Return the name the given worm.
	 */
	String getName(Worm worm) throws ModelException;

	/**
	 * Rename the given worm.
	 */
	void rename(Worm worm, String newName) throws ModelException;
	
	/**
	 * Return the world to which this worm belongs
	 */
	World getWorld(Worm worm) throws ModelException;
	

	/**
	 * Turn the given worm by the given angle.
	 */
	void turn(Worm worm, double angle);

	/**
	 * Return the location the farthest away from its current location to which the given 
	 * worm can move in the world in which that worm is positioned, if any, following
	 * the given direction and not exceeding the given maximum distance.
	 *   - The maximum distance must be finite and may not be negative.
	 *   - The given direction must be in the range [0.0 .. PI[.
	 *   - On its road to the resulting location, the given worm will always be
	 *     positioned on passable terrain.
	 *   - The resulting position may be outside the boundaries of the world, if any, in
	 *     which the given worm is located.
	 */
	double[] getFurthestLocationInDirection(Worm worm, double direction, double maxDistance) throws ModelException;

	/**
	 * Move the given worm according to the rules in the assignment.
	 */
	void move(Worm worm) throws ModelException;

	/**
	 * Returns whether the given worm can fall.
	 * 	 - Students working alone on the project must not override this method.
	 */
	default public boolean canFall(Worm worm) throws MustNotImplementException {
		throw new MustNotImplementException();
	}

	/**
	 * Makes the given worm fall down until it rests on impassable terrain again,
	 * or until it leaves the world in which it is in.
	 * 	 - Students working alone on the project must not override this method.
	 */
	default void fall(Worm worm) throws ModelException, MustNotImplementException  {
		throw new MustNotImplementException();
	}
	/**
	 * Return the time needed by the given worm to jump to the nearest position
	 * adjacent to impassable terrain.
	 *   - deltaT determines the resolution to be used in successive steps of the jump.
	 */
	double getJumpTime(Worm worm, double deltaT) throws ModelException;

	/**
	 * Returns the location on the jump trajectory of the given worm
	 * after a time t.
	 *   - The resulting location is an array with two elements,
	 *     with the first element being the x-coordinate and the
	 *     second element the y-coordinate.
	 */
	double[] getJumpStep(Worm worm, double t) throws ModelException;

	/**
	 * Make the given worm jump using the given time step.
	 *   - The given time step determines a time interval during which
	 *     you may assume that the worm will not move through a piece
	 *     of impassable terrain.
	 */
	void jump(Worm worm, double timeStep) throws ModelException;

	
	
	/************
	 * FOOD
	 ************/
	
	/**
	 * Create and return a new portion of food that is positioned at the given
	 * location in the given world.
	 *   = If the given world is not effective, the new food is simply positioned
	 *     at the given location.
	 */
	Food createFood(World world, double[] location) throws ModelException;
	
	/**
	 * Terminate the given portion of food.
	 */
	void terminate(Food food) throws ModelException;
	
	/**
	 * Check whether the given portion of food is terminated.
	 */
	boolean isTerminated(Food food) throws ModelException;

	/**
	 * Return the current location of the given portion of food.
	 *   - The resulting array contains the the x-coordinate of the given worm
	 *     followed by its y-coordinate.
	 */
	double[] getLocation(Food food) throws ModelException;

	/**
	 * Return the radius of the given portion of food.
	 */
	double getRadius(Food food) throws ModelException;

	/**
	 * Return the mass of the given portion of food.
	 */
	double getMass(Food food) throws ModelException;

	/**
	 * Return the world to which this portion of food belongs.
	 */
	World getWorld(Food food) throws ModelException;	


	
	/********
	 * TEAM
	 ********/

	/**
	 * Create a new team for the given world with given name and with no members yet.
	 * 	 - Students working alone on the project must not override this method.
	 */
	default Team createTeam(World world, String name)
			throws ModelException, MustNotImplementException  {
		throw new MustNotImplementException();
	}
	
	/**
	 * Terminate the given team.
	 */
	default void terminate(Team team) throws ModelException, MustNotImplementException  {
		throw new MustNotImplementException();
	}
	
	/**
	 * Check whether the given portion of food is terminated.
	 */
	default boolean isTerminated(Team team) throws ModelException, MustNotImplementException  {
		throw new MustNotImplementException();
	}
	
	/**
	 * Return the name of the given team.
	 * 	 - Students working alone on the project must not override this method.
	 */
	default String getName(Team team) throws ModelException, MustNotImplementException  {
		throw new MustNotImplementException();
	}
	

	/**
	 * Return the team to which this worm belongs.
	 * - Students working alone on the project must not override this method.
	 */
	default Team getTeam(Worm worm) throws ModelException {
		throw new MustNotImplementException();
	}
	
	/**
	 * Return the number of worms in the given team.
	 * 	 - Students working alone on the project must not override this method.
	 */
	default int getNbWormsOfTeam(Team team)
			throws ModelException, MustNotImplementException  {
		throw new MustNotImplementException();
	}
	
	/**
	 * Return a list of all the worms in the given team, sorted alphabetically.
	 * This method must run in linear time.
	 * 	 - Students working alone on the project must not override this method.
	 */
	default List<Worm> getAllWormsOfTeam(Team team)
			throws ModelException, MustNotImplementException  {
		throw new MustNotImplementException();
	};
	
	/**
	 * Add the given worms to the given team.
	 * 	 - Students working alone on the project must not override this method.
	 */
	default void addWormsToTeam(Team team, Worm... worms)
			throws ModelException, MustNotImplementException  {
		throw new MustNotImplementException();
	}
	
	/**
	 * Remove the given worms from the given team.
	 * 	 - Students working alone on the project must not override this method.
	 */
	default void removeWormsFromTeam(Team team, Worm... worms)
			throws ModelException, MustNotImplementException  {
		throw new MustNotImplementException();
	}

	/**
	 * Merge the given teams.
	 *   - All the worms of the supplying team are transferred to the receiving team.
	 * 	 - Students working alone on the project must not override this method.
	 */
	default void mergeTeams(Team recevingTeam, Team supplyingTeam)
			throws ModelException, MustNotImplementException  {
		throw new MustNotImplementException();
	}

	
}
