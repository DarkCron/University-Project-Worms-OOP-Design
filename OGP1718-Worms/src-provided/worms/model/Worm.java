package worms.model;

import be.kuleuven.cs.som.annotate.*;
import worms.exceptions.InvalidLocationException;
import worms.util.ModelException;

/**
 * A class describing our in-game characters, the worms. TODO
 * 
 * @author Liam ,Bernd
 *
 */
public class Worm {
	
	//TODO
	public Worm() {
		
	}
	
	//TODO DEFENSIVELY
	//NOTE this means we need to defensively program Double.isNaN(x) and Double.isNaN(y)
	public void setLocation(double x, double y) {
		if (!isValidDirection(x, y))
			throw new ModelException(new InvalidLocationException(x, y));
		
		location[0] = x;
		location[1] = y;
	}
	
	//TODO
	public static boolean isValidDirection(double x, double y) {
		return true;
	}
		
	private double[] location = new double[2];
	
	//TODO
	@Basic
	public double[] getLocation() {
		return location;
	}
	
	//TODO NOMINALLY, thus @PRE is important, example @pre direction must be between 0 and 2*math.pi (not included)
	public void setDirection(double direction) {
		assert direction > 0 && direction <2*Math.PI;
		
	}
	
	private double direction;
	
	//TODO
	@Basic
	public double getDirection() {
		return direction;
	}
	
	//LBRadius = lowerbound radius, a constant minimum size of the worms =/= actual size of the worm
	//TODO
	@Basic 
	public double getRadius() {
		return radius;
	}
	
	//TODO defensively 
	public void setRadius(double radius) {
		
	}
	
	private double radius = -1;
	private double minRadius = 0.25;
	
	//TODO
	public boolean isValidLBradius(double radius) {
		return minRadius <= radius;
	}
	
	private static double worm_density = 1062;
	
	//TODO
	public double calculateMass() {
		return worm_density * (((double)4 / (double)3)  * Math.PI * Math.pow(radius, 3));
	}
	
	//TODO
	@Basic
	public double getMass() {
		return mass;
	}
	
	
	private double mass;
	
	private String name;
	
	//TODO DEFENSIVELY
	public void setName(String name) {
		
	}
	
	public static boolean NameContainsValidCharactersOnly(String name) {
		return true;
	}
	
	//TODO
	@Basic
	public String getName() {
		return name;
	}
	
	private int maximumActionPoints;
	private int currentActionPoints;
	
	
}
