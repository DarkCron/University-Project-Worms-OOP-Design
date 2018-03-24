package worms.model.values;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Raw;
import be.kuleuven.cs.som.annotate.Value;

/**
 * A class for handling Direction for various game objects.
 * 
 * @invar	A Direction's direction must be valid. Between 0 included and 2 * PI
 * 			|isValidDirection(getDirection())
 */
@Value
public class Direction {
	/**
	 * Initializes this new Direction based on a given direction.
	 * 
	 * @param direction
	 * 		The given direction for this Direction.
	 * @pre the given direction must be a valid direction for this Direction.
	 * 		|isValidDirection(direction)
	 * @post The direction of this new Direction is equal to the given direction
	 * 		| new.getAngle() == direction
	 */
	@Raw
	public Direction(double direction){
		assert isValidDirection(direction);
		this.angle = direction;
	}
	
	/**
	 * A default Direction that complies to the class invariants.
	 */
	public final static Direction DEFAULT_DIRECTION = new Direction(0);
	
	/**
	 * Returns this Direction's direction.
	 */
	@Basic @Raw @Immutable
	public double getAngle() {
		return this.angle;
	}
	
	/**
	 * Variable for registering this Direction's direction.
	 */
	private final double angle;
	
	/**
	 * Checks whether the given direction is valid for any Direction.
	 * 
	 * @param direction
	 * 		The given direction to check
	 * @return True if and only if the given direction is larger or equal to 0
	 * 		and less than 2 * PI.
	 * 		|	result ==
	 * 		|		(direction >= 0) && (direction < 2 * Math.PI)
	 * @note we use a static function so that the method can work even if the class invariants aren't met.
	 * 	Such as entering the object in the constructor.
	 */
    public static boolean isValidDirection(double direction) {
        return (direction >= 0) && (direction < 2 * Math.PI);
    }
    
    /**
     * Checks whether this Direction is a valid one.
     * 
     * @return true if and only if this direction is a valid one, equal or greater then 0 and less then 2 * PI.
     * 		| result == 
     * 		|	isValidDirection(this.getDirection())
     */
    public boolean isValid() {
    	return isValidDirection(this.getAngle());
    }
    
	/**
	 * Check whether this Direction is equal to the given object.
	 * 
	 * @return True if and only if the given object is effective,
	 * 		   if this Direction and the given object belong to the same class,
	 * 		   and if this Direction's direction and the given object interpreted as 
	 * 		   a Direction have equal names. 
	 * 		  | result == 
	 * 		  |   ((obj != null)
	 * 		  |  && (this.getClass() == obj.getClass())
	 * 		  |  && this.getDirection() == ((Direction)obj).getDirection())
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Direction otherDirection = (Direction) obj;
		return this.getAngle() == otherDirection.getAngle();
	}
	
	/**
	 * Returns the hashcode for this Direction.
	 */
	@Override
	public int hashCode() {
		return ((Double)getAngle()).hashCode();
		}

	/**
	 * Returns the Direction as a textual representation 
	 * @return A string containing the direction and a dot.
	 * 		  | result.equals(getDirection() + ".")
	 */
	@Override
	public String toString() {
		return getAngle() + ".";
	}
}
