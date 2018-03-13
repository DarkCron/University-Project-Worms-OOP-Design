package worms.model;

import be.kuleuven.cs.som.annotate.Raw;
import worms.exceptions.InvalidLocationException;
import worms.exceptions.InvalidRadiusException;

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
	 * 		| !isValidRadius(location)
	 * @throws InvalidLocationException
	 * 		The given location is not a valid one, the location doesn't exist ( is null) or at least one of
	 * 		the coordinates is not a valid one (one of the coordinates is NaN, Not a Number).
	 * 		| !isValidLocation(location)
	 */
	@Raw
	public Food(double[] location, double radius) throws InvalidLocationException,InvalidRadiusException {
		super(location, radius);
	}

	@Override
	public void generateMass() {
		this.setMass(calculateMass(WorldConstants.getFoodDensity()));
	}

	@Override
	public boolean isValidRadius(double radius) {
		return radius == WorldConstants.getFoodRadius();
	}
	
	

}
