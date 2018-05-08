package worms.model;

import be.kuleuven.cs.som.annotate.Raw;
import worms.exceptions.InvalidLocationException;
import worms.exceptions.InvalidRadiusException;
import worms.model.values.*;

public class Food extends GameObject{

	/**
	 * 
	 * @param location
	 * @param radius
	 * 
	 * @post The location of the worm is the same as the given location.TODO @effect lookup cursus
	 * 		|new.getLocation() == location
	 * 
	 * @throws InvalidRadiusException
	 * 		The given radius is not a valid one, the given radius is less than the minimum allowed radius.
	 * 		| !isValidRadius(radius)
	 * @throws InvalidLocationException
	 * 		The given location is not a valid one, the location doesn't exist ( is null) or at least one of
	 * 		the coordinates is not a valid one (one of the coordinates is NaN, Not a Number).
	 * 		| !isValidLocation(location,world)
	 * 
	 * @see super
	 */
	@Raw
	public Food(Location location, Radius radius, World world) throws InvalidLocationException,InvalidRadiusException {
		super(location, radius, world);
		
		if(this.getWorld() != null) { //TODO
			if(!this.getWorld().isPassable(this)) {
				throw new IllegalStateException("Worm placed out of world on initialization.");
			}
			if(!this.getWorld().isAdjacantToImpassableTerrain(location, radius)) {
				throw new IllegalStateException("Worm not placed near impassable terrain.");
			}
		}
	}

	@Override //TODO DOCUMENTATION
	public void generateMass() {
		this.setMass(calculateMass(World.getFoodDensity()));
	}
	
	private boolean isPoisoned = false;

	public boolean isPoisoned() {
		return isPoisoned;
	}

	public void setPoisoned(boolean isPoisoned) {
		this.isPoisoned = isPoisoned;
	}
	
	
	
}
