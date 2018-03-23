package worms.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import be.kuleuven.cs.som.annotate.*;
import worms.model.values.GameObjectTypeID;

public class World {
	
	public World(double width, double height, boolean[][] passableMap) {
		if(isValidWorldSize(width)) {
			width = 0;
		}
		if(isValidWorldSize(height)) {
			height = 0;
		}
		this.worldWidth = width;
		this.worldHeight = height;	
		this.passableMap = passableMap;
		this.worldObjects = new HashMap<GameObjectTypeID, HashSet<GameObject>>() ;
	}

	/**
	 * Returns this world's height.
	 */
	@Basic @Raw
	public double getWorldHeight() {
		return this.worldHeight;
	}
	
	/**
	 * Returns this world's width.
	 */
	@Basic @Raw
	public double getWorldWidth() {
		return this.worldWidth;
	}
	
	private final double worldWidth;
	private final double worldHeight;
	

	public boolean isValidWorldSize(double size) {
		if(size == Double.NaN) {
			return false;
		}else if(!(size >= 0 && size <= Double.MAX_VALUE)) {
			return false;
		}
		
		return true;
	}
	
	private boolean[][] passableMap;
	
	public void addGameObject(GameObject gameObject) throws IllegalArgumentException{
		if(gameObject == null) {
			throw new IllegalArgumentException("The given gameObject was equal to null.");
		}
		
		if(worldObjectsMapHasGameObjectTypeKey(gameObject.getTypeID())) {
			worldObjects.get(gameObject.getTypeID()).add(gameObject);
		}else {
			worldObjects.put(gameObject.getTypeID(), new HashSet<GameObject>());
		}
	}
	

	private boolean worldObjectsMapHasGameObjectTypeKey(GameObjectTypeID typeID) {
		return worldObjects.containsKey(typeID);
	}
	
	public HashMap<GameObjectTypeID, HashSet<GameObject>> getWorldObjects(){
		return this.worldObjects;
	}
	
	public boolean hasGameObject(GameObject gameObject) {
		if(gameObject == null) {
			throw new IllegalArgumentException("The given gameObject was equal to null.");
		}
		
		if(!worldObjectsMapHasGameObjectTypeKey(gameObject.getTypeID())) {
			throw new IllegalArgumentException("The given gameObject was not a part of the world.");
		}
		
		return worldObjects.get(gameObject.getTypeID()).contains(gameObject);
	}

	private final HashMap<GameObjectTypeID, HashSet<GameObject>> worldObjects ;
	

	public void removeGameObject(GameObject gameObject) {
		if(gameObject == null) {
			throw new IllegalArgumentException("The given gameObject was equal to null.");
		}	
		
		if(!worldObjectsMapHasGameObjectTypeKey(gameObject.getTypeID())) {
			throw new IllegalArgumentException("The given gameObject was not a part of the world.");
		}
		
		worldObjects.get(gameObject.getTypeID()).remove(gameObject);
	}
	
	/**
	 * A constant, representing a fictitious in game simulation of real life gravity. To
	 * ensure worms fall back to the ground.
	 */
	private final static double GRAVITY = 5.0;
	/**
	 * A constant, the time in which a jump should complete in real time seconds.
	 */
	private final static double JUMP_TIME_DELTA = 0.5;
	
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
	
	public static double getGravity() {
		return GRAVITY;
	}
	
	public static double getJumpTimeDelta() {
		return JUMP_TIME_DELTA;
	}

}
