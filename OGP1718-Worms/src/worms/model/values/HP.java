package worms.model.values;

import be.kuleuven.cs.som.annotate.*;
/**
 * @invar 
 *
 */
@Value
public class HP {
	/**
	 * 
	 * @param minHp
	 * 		  The minimal amount of hitpoints the object can have.
	 * @param maxHp
	 * 		  The maximum amount of hitpoints the object can have.
	 * @post The hitpoints are set to a random number between the minHp and the maxHp. 
	 * 		|let 
	 * 		|	tempHp =
	 * 		|		calculateHp(minhp, maxHp)
	 * 		|in 
	 * 		|	if (tempHp < minHp) 
	 * 		| 		then new.getHp() == minHp
	 * 		| 	else if (tempHp > maxHp)
	 * 		| 		then new.getHp() == maxHp
	 * 		| 	else new.getHp() == tempHp
	 */
	public HP(int minHp, int maxHp){
		int tempHp = calculateHp(minHp, maxHp);
		if (tempHp < minHp) {
			hitpoints = minHp;
		}
		else if(tempHp > maxHp){
			hitpoints = maxHp;
		}
		else {
			hitpoints = tempHp;
		}
		
	}
	
	/**
	 * Returns this HP's hitpoints value.
	 */
	@Basic @Raw @Immutable
	public int getHp() {
		return this.hitpoints;
	}
	/**
	 * 
	 * @param minHp
	 * @param maxHp
	 * @return Returns the random value between the given minimal value and the maximum value.
	 * 			|result == 
	 * 			|		(int)(Math.random()*(maxHp-minHp) + minHp)
	 */
	public int calculateHp(int minHp, int maxHp){
		return (int)(Math.random()*(maxHp-minHp) + minHp);
	}
	
	private final int hitpoints;
	
	/**
	 * Check whether this HP is equal to the given object.
	 * 
	 * @return True if and only if the given object is effective,
	 * 		   if this HP and the given object belong to the same class,
	 * 		   and if this HP's hitpoints and the given object interpreted as 
	 * 		   a HP have equal hitpoints. 
	 * 		  | result == 
	 * 		  |   ((obj != null)
	 * 		  |  && (this.getClass() == obj.getClass())
	 * 		  |  && this.getHp() == (otherHP.getHp())
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		HP otherHp = (HP) obj;
		return (this.getHp() == (otherHp.getHp()));
	}
	
	/**
	 * returns the hashcode for this HP
	 */
	@Override
	public int hashCode() {
		return ((Integer)getHp()).hashCode();
		}
	
	/**
	 * Returns the textual representation of the HP
	 * @return Returns 'HP:' followed by the hitpoints of this object.
	 * 			|result.equals("HP:" + ((Integer)getHp()).toString())
	 */
	@Override
	public String toString() {
		return "HP:" + ((Integer)getHp()).toString();
	}
}
