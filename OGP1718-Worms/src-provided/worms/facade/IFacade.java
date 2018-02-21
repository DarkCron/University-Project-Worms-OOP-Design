package worms.facade;

import worms.model.Worm;
import worms.util.ModelException;

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
 * of <code>getX</code> should call a method of the given <code>worm</code> to
 * retrieve its x-coordinate.</li>
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

	/**
	 * Create and return a new worm that is positioned at the given location, looks
	 * in the given direction, has the given radius and the given name.
	 * 
	 * @param coordinates
	 *            An array containing the x-coordinate of the position of the new
	 *            worm followed by the y-coordinate of the position of the new worm
	 *            (in meter)
	 * @param direction
	 *            The direction of the new worm (in radians)
	 * @param radius
	 *            The radius of the new worm (in meter)
	 * @param name
	 *            The name of the new worm
	 */
	Worm createWorm(double[] location, double direction, double radius, String name) throws ModelException;

	/**
	 * Moves the given worm by the given number of steps.
	 */
	void move(Worm worm, int nbSteps) throws ModelException;

	/**
	 * Turns the given worm by the given angle.
	 */
	void turn(Worm worm, double angle) throws ModelException;

	/**
	 * Makes the given worm jump.
	 */
	void jump(Worm worm) throws ModelException;

	/**
	 * Returns the total amount of time (in seconds) that a jump of the given worm
	 * would take.
	 */
	double getJumpTime(Worm worm) throws ModelException;

	/**
	 * Returns the location on the jump trajectory of the given worm after a time t.
	 * 
	 * @return An array with two elements, with the first element being the
	 *         x-coordinate and the second element the y-coordinate.
	 */
	double[] getJumpStep(Worm worm, double t) throws ModelException;

	/**
	 * Returns the x-coordinate of the current location of the given worm.
	 */
	double getX(Worm worm) throws ModelException;

	/**
	 * Returns the y-coordinate of the current location of the given worm.
	 */
	double getY(Worm worm) throws ModelException;

	/**
	 * Returns the current orientation of the given worm (in radians).
	 */
	double getOrientation(Worm worm) throws ModelException;

	/**
	 * Returns the radius of the given worm.
	 */
	double getRadius(Worm worm) throws ModelException;

	/**
	 * Sets the radius of the given worm to the given value.
	 */
	void setRadius(Worm worm, double newRadius) throws ModelException;

	/**
	 * Returns the current number of action points of the given worm.
	 */
	long getNbActionPoints(Worm worm) throws ModelException;

	/**
	 * Decreases the current number of action points of the given worm with the
	 * given delta.
	 */
	void decreaseNbActionPoints(Worm worm, long delta) throws ModelException;

	/**
	 * Returns the maximum number of action points of the given worm.
	 */
	long getMaxNbActionPoints(Worm worm) throws ModelException;

	/**
	 * Returns the name the given worm.
	 */
	String getName(Worm worm) throws ModelException;

	/**
	 * Renames the given worm.
	 */
	void rename(Worm worm, String newName) throws ModelException;

	/**
	 * Returns the mass of the given worm.
	 */
	double getMass(Worm worm) throws ModelException;

}
