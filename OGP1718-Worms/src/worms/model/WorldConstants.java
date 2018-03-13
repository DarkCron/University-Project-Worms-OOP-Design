package worms.model;

public class WorldConstants {
	private final static  double WORM_MINIMUM_RADIUS = 0.25;
	private final static  double FOOD_DEFAULT_RADIUS = 0.20;
	private final static double WORM_DENSITY = 1062;
	private final static double FOOD_DENSITY = 150;
	
	public static double getWormDensity() {
		return WORM_DENSITY;
	}
	
	public static double getWormMinimumRadius() {
		return WORM_MINIMUM_RADIUS;
	}
	
	public static double getFoodDensity() {
		return FOOD_DENSITY;
	}
	
	public static double getFoodRadius() {
		return FOOD_DEFAULT_RADIUS;
	}
}
