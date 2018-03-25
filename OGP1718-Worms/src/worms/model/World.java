package worms.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import be.kuleuven.cs.som.annotate.*;
import worms.model.values.GameObjectTypeID;

/**
 * 
 * A class for representing and keeping track of game worlds.
 * 
 * @Invar | isValdiPassableMap(getPassableMap())
 * 
 * @Invar | isValidWorldSize(getWorldHeight(),getPassableMap().length)
 * 
 * @Invar | isValidWorldSize(getWorldWidth(), getPassableMap()[0].length)
 * 
 * @author bernd
 *
 */
public class World {
	
	/**
	 * Creates this new world.
	 * 
	 * @param width
	 * @param height
	 * @param passableMap
	 * 
	 * @post |new.getWorldHeight() == height
	 * 
	 * @post |new.getWorldWidth() == width
	 * 
	 * @post |new.passableMap() == passableMap
	 * 
	 * @post |new.getWorldObjects() != null
	 * 
	 * @note we worked out width and height in a total manner since no specific
	 * 	manner was defined.
	 */
	public World(double width, double height, boolean[][] passableMap) {
		if(isValidPassableMap(passableMap)) {
			throw new IllegalArgumentException("Invalid passableMap upon world creation.");
		}
		
		if(!isValidWorldSize(width,passableMap[0].length)) {
			width = 0;
		}
		if(!isValidWorldSize(height,passableMap.length)) {
			height = 0;
		}
		this.worldWidth = width;
		this.worldHeight = height;	
		this.passableMap = passableMap;
	}
	
	/**
	 * Creates this new world.
	 * 
	 * @param passableMap
	 * 
	 * @post |new.getWorldHeight() == passableMap[0].length
	 * 
	 * @post |new.getWorldWidth() == passableMap.length
	 * 
	 * @post |new.passableMap() == passableMap
	 * 
	 * @post |new.getWorldObjects() != null
	 * 
	 * @note we worked out width and height in a total manner since no specific
	 * 	manner was defined.
	 */
	public World(boolean[][] passableMap) {
		if(isValidPassableMap(passableMap)) {
			throw new IllegalArgumentException("Invalid passableMap upon world creation.");
		}

		this.worldWidth = passableMap.length;
		this.worldHeight = passableMap[0].length;	
		this.passableMap = passableMap;
	}
	
	/**
	 * Checks whether a given map is valid for any and all worlds.
	 * 
	 * @param passableMap
	 * 
	 * @return | result == !(passableMap==null || (passableMap.length == 0 || passableMap[0].length == 0))
	 */
	public static boolean isValidPassableMap(boolean[][] passableMap) {
		if(passableMap==null || (passableMap.length == 0 || passableMap[0].length == 0)) {
			return false;
		}
		return false;
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
	
	/**
	 * Represents this world's width
	 */
	private final double worldWidth;
	/**
	 * Represents this world's height
	 */
	private final double worldHeight;
	
	/**
	 * Checks whether a given world's dimension is valid based on a given required length, representing either width or height.
	 * 
	 * @param size
	 * @param requiredLength
	 * 
	 * @return | result == !(size == Double.NaN) && size >= 0 && size == requiredLength
	 */
	public static boolean isValidWorldSize(double size, double requiredLength) {
		if(size == Double.NaN) {
			return false;
		}else if((size < 0 || size == Double.MAX_VALUE)) {
			return false;
		}
		
		if(size!=requiredLength) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Gets this world's passableMap.
	 */
	@Basic @Raw
	public boolean[][] getPassableMap(){
		return passableMap.clone();
	}
	
	/**
	 * Represents this world's passableMap
	 */
	private final boolean[][] passableMap;
	
	/**
	 * Adds a new gameObject to this world.
	 * 
	 * @param gameObject
	 * 
	 * @throws IllegalArgumentException
	 * 		| gameObject == null
	 * 
	 * @post | worldObjects.get(gameObject.getTypeID()).contains(gameObject)
	 */
	public void addGameObject(GameObject gameObject) throws IllegalArgumentException{
		if(gameObject == null) {
			throw new IllegalArgumentException("The given gameObject was equal to null.");
		}
		
		if(worldObjectsMapHasGameObjectTypeKey(gameObject.getTypeID())) {
			worldObjects.get(gameObject.getTypeID()).add(gameObject);
		}else {
			worldObjects.put(gameObject.getTypeID(), new HashSet<GameObject>());
			worldObjects.get(gameObject.getTypeID()).add(gameObject);
		}
	}
	
	/**
	 * Checks whether worldObjects contains a set of the given typeID objects.
	 * 
	 * @param typeID
	 * @return | worldObjects.containsKey(typeID)
	 */
	private boolean worldObjectsMapHasGameObjectTypeKey(GameObjectTypeID typeID) {
		return worldObjects.containsKey(typeID);
	}
	
	/**
	 * Returns this world's worldObjects Map collection.
	 * 
	 * @return | !result.contains(null)
	 * 
	 * @return 	| for each object in worldObjects
	 * 			| 		(result.contains(object) == this.hasGameObject(object))
	 */
	public HashMap<GameObjectTypeID, HashSet<GameObject>> getWorldObjects(){
		return this.worldObjects;
	}
	
	/**
	 * Checks whether this world's worldObjects contains a certain given gameObject.
	 * 
	 * @param gameObject
	 * 
	 * @throws IllegalArgumentException
	 * 		|	gameObject == null || worldObjectsMapHasGameObjectTypeKey(gameObject.getTypeID())
	 * 
	 * @return | result == worldObjects.get(gameObject.getTypeID()).contains(gameObject)
	 */
	public boolean hasGameObject(GameObject gameObject) throws IllegalArgumentException{
		if(gameObject == null) {
			throw new IllegalArgumentException("The given gameObject was equal to null.");
		}
		
		if(!worldObjectsMapHasGameObjectTypeKey(gameObject.getTypeID())) {
			throw new IllegalArgumentException("The given gameObject was not a part of the world.");
		}
		
		return worldObjects.get(gameObject.getTypeID()).contains(gameObject);
	}

	/**
	 * A map containing every gameObject this world contains.
	 * Objects are placed in the map to which their types belong. 
	 * 
	 * @invar | worldObjects!=null
	 * 
	 * @invar 	| for each key in worldObjects
	 * 			|		key != null
	 * 
	 * @invar	| for each key in worldObjects
	 * 			|		key != null
	 * 			|		for each gameObject in worldObjects.get(key)
	 * 			|			gameObject != null
	 * 			|			&& gameObject.getWorld() == this
	 * 			|			&& !gameObject.isTerminated()
	 * 			|			&& gameObject.getTypeID() == key
	 * 			|			&& this.hasGameObject(gameObject)
	 */
	private final HashMap<GameObjectTypeID, HashSet<GameObject>> worldObjects = new HashMap<GameObjectTypeID, HashSet<GameObject>>();
	
	/**
	 * Mutator that removes a given gameObject from worldObjects.
	 * 
	 * @param gameObject
	 * 
	 * @post | !worldObjects.get(gameObject.getTypeID()).contains(gameObject)
	 * 
	 * @throws IllegalArgumentException
	 * 		| gameObject == null
	 * 
	 * @throws IllegalArgumentException
	 * 		| !worldObjectsMapHasGameObjectTypeKey(gameObject.getTypeID())
	 * 
	 * @throws IllegalArgumentException
	 * 		| gameObject.getWorld()!= this
	 */
	public void removeGameObject(GameObject gameObject) throws IllegalArgumentException{
		if(gameObject == null) {
			throw new IllegalArgumentException("The given gameObject was equal to null.");
		}	
		
		if(gameObject.getWorld()!= this) {
			throw new IllegalArgumentException("The given gameObject was not from this world.");
		}
		
		if(!worldObjectsMapHasGameObjectTypeKey(gameObject.getTypeID())) {
			throw new IllegalArgumentException("The given gameObject was not a part of the world.");
		}
		
		worldObjects.get(gameObject.getTypeID()).remove(gameObject);
	}
	
	/**
	 * Returns all world objects of a certain type/class.
	 * 
	 * @param classID
	 * 
	 * @return	| result != null
	 * 
	 * @return	
	 * 			| for each key in worldObjects.keySet()
	 * 			| 		if key.getGameObjectType() == classID
	 * 			|			then
	 * 			|			result.size() == worldObjects.get(key).size()
	 * @return	
	 *			| for each key in worldObjects.keySet()
	 * 			| 		if key.getGameObjectType() == classID
	 * 			|			then
	 * 			|			for each index in 0..worldObjects.get(key).size()-1
	 * 			|				worldObjects.get(key).contains(result[index])
	 * 			|			let clone = worldObjects.get(key).clone()
	 * 			|			in
	 * 			|			clone.removeAll(result) == null
	 * 
	 * @throws IllegalArgumentException
	 * 		| classID == null
	 */
	@SuppressWarnings("unchecked")
	public <T extends GameObject> ArrayList<T> getAllObjectsOfType(Class<? extends GameObject> classID) throws IllegalArgumentException{
		if(classID == null) {
			throw new IllegalArgumentException("Invalid ID given.");
		}
	
		
		ArrayList<T> objectTypeList = new ArrayList<>();
		
		for(GameObjectTypeID key : worldObjects.keySet()) {
			if(key.getGameObjectType() == classID) {
				for(GameObject w : worldObjects.get(key)) {
					objectTypeList.add((T)w);
				}
				break;
			}
		}
		
		return objectTypeList;
	}
	
	public boolean isAdjacantToImpassableTerrain() {
		
		return false;
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
