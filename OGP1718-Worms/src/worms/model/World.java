package worms.model;


import java.math.BigInteger;
import java.nio.channels.IllegalSelectorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import be.kuleuven.cs.som.annotate.*;
import worms.model.ShapeHelp.BoundaryRectangle;
import worms.model.ShapeHelp.Circle;
import worms.model.ShapeHelp.Rectangle;
import worms.model.values.GameObjectTypeID;
import worms.model.values.HP;
import worms.model.values.Location;
import worms.model.values.Name;
import worms.model.values.Radius;

/**
 * 
 * A class for representing and keeping track of game worlds.
 * 
 * @Invar | hasValidPassableMap()
 * 
 * @Invar | isValidWorldSize(getWorldHeight())
 * 
 * @Invar | isValidWorldSize(getWorldWidth())
 * 
 * @Invar | for each object in getAllGameObjects()
 * 		  |		isPassable(object)
 * 
 * @author Liam, bernd
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
	 * @post |new.passableMap() != null
	 * 
	 * @post |new.getWorldObjects() != null
	 * 
	 * @post |new.getTeams() != null
	 * 
	 * @throws IllegalArgumentException
	 * 		| !isValidPassableMap(passableMap)
	 * 
	 * @note we worked out width and height in a total manner since no specific
	 * 	manner was defined.
	 */
	public World(double width, double height, boolean[][] passableMap) throws IllegalArgumentException{
		if(!isValidPassableMap(passableMap)) {
			throw new IllegalArgumentException("Invalid passableMap upon world creation.");
		}
		
		if(!isValidWorldSize(width)) {
			throw new IllegalArgumentException("IllegalDimension width");
		}
		if(!isValidWorldSize(height)) {
			throw new IllegalArgumentException("IllegalDimension height");
		}
		this.worldWidth = width;
		this.worldHeight = height;	

		this.passableMap = new BoundaryRectangle[passableMap.length][passableMap[0].length];
		this.processMap(passableMap);
		
		this.worldTeams = new HashSet<Team>();
	}
	
	/**
	 * Generates a map representing the (im)passable locations of the gameworld.
	 * 
	 * @param passableMap
	 * 
	 * @post | new.passableMap != null
	 * 		 | for i in [0..passableMap.length[
	 * 		 |		new.passableMap[i] != null
	 * 		 |		for j in [0..passableMap[0].length[
	 * 		 |			new.passableMap[passableMap.length - i - 1][j].isPassable() == passableMap[passableMap.length - i - 1][j]
	 * 		 |			new.passableMap[passableMap.length - i - 1][j].getSize().equals( new Location(1, 1))
	 * 		 |			new.passableMap[passableMap.length - i - 1][j].getCenter() == new Location(j,i)
	 * 		 |		
	 */
	private void processMap(boolean[][] passableMap) {
		Location size = new Location(1, 1);
		for (int i = 0; i < passableMap.length; i++) {
			for (int j = 0; j < passableMap[0].length; j++) {
				//this.passableMap[passableMap.length - i-1][j] = new BoundaryRectangle(new Location(j,i), size, passableMap[passableMap.length - i - 1][j]);
				this.passableMap[i][j] = new BoundaryRectangle(new Location(j,i), size, passableMap[passableMap.length - i - 1][j]);
			}		
		}
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
	 * @post |new.getTeams() != null
	 * 
	 * @note we worked out width and height in a total manner since no specific
	 * 	manner was defined.
	 */
	public World(boolean[][] passableMap) {
		if(!isValidPassableMap(passableMap)) {
			throw new IllegalArgumentException("Invalid passableMap upon world creation.");
		}
		this.worldWidth = passableMap[0].length;
		this.worldHeight = passableMap.length;	
		this.worldTeams = new HashSet<Team>();
		this.passableMap = new BoundaryRectangle[passableMap.length][passableMap[0].length];
		processMap(passableMap);
	}
	
	/**
	 * Checks whether a given map is valid for any and all worlds.
	 * 
	 * @param passableMap
	 * 
	 * @return | result == !(passableMap==null || (passableMap.length == 0 || passableMap[0].length == 0))
	 *  
	 */
	public static boolean isValidPassableMap(boolean[][] passableMap) {
		if(passableMap==null || (passableMap.length == 0 || passableMap[0].length == 0)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks whether a given map is valid for any and all worlds.
	 * 
	 * @param passableMap
	 * 
	 * @return | result == !(passableMap==null || (passableMap.length == 0 || passableMap[0].length == 0))
	 *  
	 */
	public static boolean isValidPassableMap(BoundaryRectangle[][] passableMap) {
		if(passableMap==null || (passableMap.length == 0 || passableMap[0].length == 0)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks whether this world has a valid passable map.
	 * 
	 * @return | result == (!isValidPassableMap(this.getPassableMap())) && passableMap.length == this.getWorldHeight() && passableMap[0].length == this.getWorldWidth()
	 */
	public boolean hasValidPassableMap() {
		if(isValidPassableMap(this.getPassableMap())) {
			return false;
		}
		return passableMap.length == this.getWorldHeight() && passableMap[0].length == this.getWorldWidth();
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
	 * Checks whether this world fully contains a given rectangle.
	 * 
	 * @param gameObjectBounds
	 * @return  | let worldBounds = Rectangle(new Location(0, 0), new Location(this.getWorldWidth(), this.getWorldHeight())) in
	 * 			| result == worldBounds.fullyContains(gameObjectBounds)
	 */
	public boolean fullyContains(Rectangle gameObjectBounds) {
		Rectangle worldBounds = new Rectangle(new Location(0, 0), new Location(this.getWorldWidth(), this.getWorldHeight()));
		return worldBounds.fullyContains(gameObjectBounds);
	}
	
	/**
	 * Checks whether this world fully contains a given gameObject.
	 * 
	 * @param gameObject
	 * @return  | result == this.fullyContains(gameObject.getSurface().getBoundingRectangle())
	 */
	public boolean fullyContains(GameObject gameObject) {
		return this.fullyContains(gameObject.getSurface().getBoundingRectangle());
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
	 * 
	 * @return | result == !(size == Double.NaN) && size >= 0 
	 */
	public static boolean isValidWorldSize(double size) {
		if(Double.compare(size, Double.NaN) == 0) {
			return false;
		}else if((size < 0 || size == Double.MAX_VALUE)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Gets this world's passableMap.
	 */
	@Basic @Raw
	public BoundaryRectangle[][] getPassableMap(){
		return passableMap.clone();
	}
	
	/**
	 * Represents this world's passableMap
	 */
	private BoundaryRectangle[][] passableMap;
	
	/**
	 * Adds a new gameObject to this world.
	 * 
	 * @param gameObject
	 * 
	 * @throws IllegalArgumentException
	 * 		| (gameObject == null) || 
	 * 		|	(this.isTerminated()) ||
	 * 		|	(this.getIsGameActive() && !(gameObject instanceof Projectile)) ||
	 * 		|	(gameObject.isTerminated()) ||
	 * 		|	(gameObject.getWorld() != this && gameObject.getWorld() != null) ||
	 * 		|	(this.hasGameObject(gameObject)) ||
	 * 		|	
	 * 
	 * @throws IllegalStateException
	 * 		| this.getIsGameActive() ||
	 * 		| 	(!isPassable(gameObject))
	 * 
	 * @post | worldObjects.get(gameObject.getTypeID()).contains(gameObject)
	 * 		 
	 *  @post	| gameObject.getWorld() == this
	 *  
	 *  @post	| if !(gameObject instanceof Projectile):
	 *  		|	new.isAdjacantToImpassableTerrain(gameObject.getLocation(), gameObject.getRadius()) == true
	 *  		| new.isPassable(gameObject) == true
	 */
	public void addGameObject(GameObject gameObject) throws IllegalArgumentException, IllegalStateException{
		if(this.getIsGameActive() && !(gameObject instanceof Projectile)) {
			throw new IllegalStateException("Game is active, objects can't be added.");
		}
		
		if(this.isTerminated()) {
			throw new IllegalArgumentException("This world is terminated.");
		}
		
		if(gameObject == null) {
			throw new IllegalArgumentException("The given gameObject was equal to null.");
		}
		
		
		if(gameObject.isTerminated()) {
			throw new IllegalArgumentException("The given gameObject was terminated.");
		}
		
		if(gameObject.getWorld() != this && gameObject.getWorld() != null) {
			throw new IllegalArgumentException("The given gameObject already existed somewhere else.");
		}
		
		if(this.hasGameObject(gameObject)) { 
			throw new IllegalArgumentException("The given gameObject was already in world.");
		}
		
		if(worldObjectsMapHasGameObjectTypeKey(gameObject.getTypeID())) {
			worldObjects.get(gameObject.getTypeID()).add(gameObject);
		}else {
			worldObjects.put(gameObject.getTypeID(), new HashSet<GameObject>());
			worldObjects.get(gameObject.getTypeID()).add(gameObject);
		}
		
		gameObject.setWorld(this);
		
		if(!isPassable(gameObject)) {
			throw new IllegalStateException("object placed out of world on initialization.");
		}
		
		if(!(gameObject instanceof Projectile)) {
			if(!isAdjacantToImpassableTerrain(gameObject.getLocation(), gameObject.getRadius())) {
				throw new IllegalStateException("object not placed near impassable terrain.");
			}
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
			worldObjects.put(gameObject.getTypeID(), new HashSet<GameObject>());
			//throw new IllegalArgumentException("The given gameObject was not a part of the world.");
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
		
		if(worldObjectsMapHasGameObjectTypeKey(gameObject.getTypeID())) {
			//throw new IllegalArgumentException("The given gameObject was not a part of the world.");
			worldObjects.get(gameObject.getTypeID()).remove(gameObject);
		}
		
		if(!gameObject.isTerminated()) {
			gameObject.terminate();
		}
	
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
	 * 		| !isValidObjectGameObjectClass(classID)
	 */
	@SuppressWarnings("unchecked")
	public <T extends GameObject> ArrayList<T> getAllObjectsOfType(Class<? extends GameObject> classID) throws IllegalArgumentException{
		if(!isValidObjectGameObjectClass(classID)) {
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
	
	/**
	 * Checks whether a given object class is valid as a gameObject class.
	 * 
	 * @param classID
	 * @return | result == (classID != null)
	 */
	public static boolean isValidObjectGameObjectClass(Class<? extends GameObject> classID) {
		return classID != null;
	}
	
	/**
	 * Returns a collection of all gameObjects in this world.
	 * 
	 * @return  | result != null
	 * 			| for each type in worldObjects.keySet()
	 * 			|	for each object in worldObjects.get(type)
	 * 			|		result.contains(object)
	 */
	public Collection<Object>  getAllGameObjects(){
		ArrayList<Object> allObjects = new ArrayList<Object>();
		
		for(GameObjectTypeID key : worldObjects.keySet()) {
			for(GameObject w : worldObjects.get(key)) {
				allObjects.add(w);
			}
		}
		
		return allObjects;
	}
	
	/**
	 * Checks whether a given theoretical circular surface with a given location and radius is adjacent
	 * to impassable terrain. We assume that the given location is passable.
	 * 
	 * @param location
	 * @param radius
	 * 
	 * 
	 * @return  | result == this.isPassable(location,  new Radius(radius.getRadius() *1.1d))
	 * 			|	&& Worm.isValidWorldLocation(location, this)
	 * 			|	&& this.fullyContains(new Circle(location,radius).getBoundingRectangle())
	 */
	public boolean isAdjacantToImpassableTerrain(Location location, Radius radius) {	
	
		if(!Worm.isValidWorldLocation(location, this)) {
			return false;
		}
		if(!this.fullyContains(new Circle(location,radius).getBoundingRectangle())) {
			return false;
		}

		return !this.isPassable(location,  new Radius(radius.getRadius() *1.1d)) && this.isPassable(location, radius);	
	}
	
	
	/**
	 * Passable map represents a fully detailed map, while this world's length and width, refers to relative locations.
	 * We therefore provide a ratio between our world actual and relative length.
	 * 
	 * @return | result == ( (double)(this.getWorldWidth()) / (double)passableMap[0].length)
	 */
	public double getWidthRatio() {
		return ( (double)(this.getWorldWidth()) / (double)passableMap[0].length);
	}
	
	/**
	 * Passable map represents a fully detailed map, while this world's length and width, refers to relative locations.
	 * We therefore provide a ratio between our world actual and relative length.
	 * 
	 * @return | result == ( (double)(this.getWorldHeight()) / (double)passableMap.length)
	 */
	public double getHeightRatio() {
		return ( (double)(this.getWorldHeight()) / (double)passableMap.length);
	}
	
	/**
	 * Checks whether a given relative location is passable in this world.
	 * We first need to convert our given relative location to an actual position in this world.
	 * 
	 * @param location
	 * @return	| let realWorldLoc = getRealWorldLoc(location) in
	 * 			| let heightIndex = passableMap.length - (int)Math.floor(realWorldLoc.getY())-1 in
	 * 			| let widthIndex = (int)Math.floor(realWorldLoc.getX()) in
	 * 			| if heightIndex < 0 then
	 * 			|	heightIndex = 0
	 * 			| else
	 * 			|	heightIndex = passableMap.length-1
	 * 			| if widthIndex < 0 then
	 * 			|	widthIndex = 0
	 * 			| else
	 * 			|	widthIndex = passableMap[0].length-1
	 * 			| result == passableMap[heightIndex][widthIndex].isPassable() || !passableMap[heightIndex][widthIndex].containsPoint(realWorldLoc)
	 */
	public boolean isPassable(Location location) {			
		
			Stream<BoundaryRectangle> stream = Arrays.stream(passableMap).flatMap(x -> Arrays.stream(x));
			Optional<BoundaryRectangle> stuff = stream.filter(tile->!tile.isPassable() && tile.containsPoint(getRealWorldLoc(location))).findAny();
			if(stuff.isPresent()) {
				return false;
			}else {
				return true;
			}
	}
//		
//		Location realWorldLoc = getRealWorldLoc(location);
//		if(realWorldLoc.getX() >= 0 && realWorldLoc.getX() < passableMap[0].length && realWorldLoc.getY() >=0 && realWorldLoc.getY() < passableMap.length) {
//			//System.out.println(realWorldLoc);
//			int heightIndex = passableMap.length - (int)Math.floor(realWorldLoc.getY())-1;
//			if(heightIndex < 0) {
//				heightIndex = 0;
//			}else if(heightIndex >= passableMap.length){
//				heightIndex = passableMap.length-1;
//			}
//			int widthIndex = (int)Math.floor(realWorldLoc.getX());
//			if(widthIndex < 0) {
//				widthIndex = 0;
//			}else if(widthIndex >= passableMap[0].length){
//				widthIndex = passableMap[0].length-1;
//			}
//			
//
//			if(!passableMap[heightIndex][widthIndex].isPassable() && passableMap[heightIndex][widthIndex].containsPoint(realWorldLoc)) {
//				if(isOnEdge(passableMap[heightIndex][widthIndex],realWorldLoc)) {
//					return edgeBorderCheck(heightIndex,widthIndex,realWorldLoc);
//				}else {
//					return false;
//				}
//				
//			}	
//		}
//		
//		return true;

//	
//	//TODO
//	private boolean edgeBorderCheck(int heightIndex, int widthIndex,Location realWorldLoc) {
//		if(passableMap[heightIndex][widthIndex].isOnCornerEdge(realWorldLoc)) {
//			return true;
//		}
//		if(passableMap[heightIndex][widthIndex].isOnUpperEdge(realWorldLoc)) {
//			return heightIndex != passableMap.length -1 &&  passableMap[heightIndex+1][widthIndex].isPassable();
//		}else if(passableMap[heightIndex][widthIndex].isOnLowerEdge(realWorldLoc)) {
//			return heightIndex != 0 &&  passableMap[heightIndex-1][widthIndex].isPassable();
//		}else if(passableMap[heightIndex][widthIndex].isOnLeftEdge(realWorldLoc)) {
//			return widthIndex != 0 &&  passableMap[heightIndex][widthIndex-1].isPassable();
//		}else if(passableMap[heightIndex][widthIndex].isOnRightEdge(realWorldLoc)) {
//			return widthIndex != passableMap[0].length -1 &&  passableMap[heightIndex][widthIndex+1].isPassable();
//		}
//		return false;
//	}

//	//TODO
//	private boolean isOnEdge(BoundaryRectangle boundaryRectangle, Location realWorldLoc) {
//		if(realWorldLoc.getX() == boundaryRectangle.getCenter().getX()) {
//			return true;
//		}
//		else if(realWorldLoc.getX() > boundaryRectangle.getCenter().getX() && boundaryRectangle.getCenter().getX() + boundaryRectangle.getSize().getX() > realWorldLoc.getX()) {
//			if(realWorldLoc.getY() == boundaryRectangle.getCenter().getY()) {
//				return true;
//			}else if(realWorldLoc.getY() == boundaryRectangle.getCenter().getY()+ boundaryRectangle.getSize().getY()) {
//				return true;
//			}else {
//				return false;
//			}
//		}else if(boundaryRectangle.getCenter().getX() + boundaryRectangle.getSize().getX() == realWorldLoc.getX()) {
//			return true;
//		}
//		return false;
//	}

	/**
	 * Returns a real world representation of a relative world position.
	 * @param location
	 * @return | result == Location((location.getX())/this.getWidthRatio(), (location.getY())/this.getHeightRatio())
	 */
	public Location getRealWorldLoc(Location location) {
		return  new Location((location.getX())/this.getWidthRatio(), (location.getY())/this.getHeightRatio());
	}
	
	/**
	 * Checks whether a surface with a given center location and given radius is fully passable for all points of the surface.
	 * 
	 * @param location
	 * @param radius
	 * @return 	| if !Worm.isValidWorldLocation(location, this) then
	 * 			| 	result == false
	 * 			| else
	 * 			| let passableSurface = Circle(location, radius) in
	 * 			|	for double i in [0..passableSurface.getBoundingRectangle().getSize().getX()]
	 * 			|		for double j in [0..passableSurface.getBoundingRectangle().getSize().getY()]
	 * 			|			//This first if checks whether the point we're checking is within the actual circular surface
	 * 			|			if passableSurface.contains(new Location(i+bound.getCenter().getX(),j+bound.getCenter().getY())) then
	 * 			|				//This second if checks whether the point is passable
	 * 			|				return this.isPassable(new Location((i+bound.getCenter().getX()), (j+bound.getCenter().getY())))
	 */
	public boolean isPassable(Location location, Radius radius) {
//		if(!Worm.isValidWorldLocation(location, this)) {
//			return false;
//		}

		
//		if(!furthestPointsPassable(location,radius)) {
//			return false;
//		}
		Circle passableSurface = new Circle(location, radius);
		Rectangle bound = passableSurface.getBoundingRectangle();
		
		Rectangle realStuff = new Rectangle(this.getRealWorldLoc(bound.getCenter()), this.getRealWorldLoc(bound.getSize()));
		ArrayList<BoundaryRectangle> collectionOfStuff = new ArrayList<BoundaryRectangle>();
		for (int x = -2; x < realStuff.getSize().getX()+1;x++) {
			for (int y = -2; y < realStuff.getSize().getY()+1;y++) {
				int xCoord = x + (int)realStuff.getCenter().getX();
				int yCoord = y + (int)realStuff.getCenter().getY();
				if(xCoord >= 0 && xCoord < this.passableMap[0].length) {
					if(yCoord >= 0 && yCoord < this.passableMap.length) {
						collectionOfStuff.add(passableMap[yCoord][xCoord]);
					}
				}
			}
		}
		//Stream<BoundaryRectangle> stream = Arrays.stream(passableMap).flatMap(x -> Arrays.stream(x));
		Stream<BoundaryRectangle> stream = collectionOfStuff.stream();
		Optional<BoundaryRectangle> stuff = stream.filter(tile->!tile.isPassable() && tile.intersects(realStuff)).findAny();
		if(stuff.isPresent()) {
			return false;
		}else {
			return true;
		}
		
//		for (double i = 0; i <= bound.getSize().getX(); i+=0.02) {
//			for (double j = 0; j <= bound.getSize().getY(); j+=0.02) {
//				if(passableSurface.contains(new Location(i+bound.getCenter().getX(),j+bound.getCenter().getY()))) {
//					if(!this.isPassable(new Location((i+bound.getCenter().getX()), (j+bound.getCenter().getY())))) {
//						return false;
//					}
//				}
//			}
//		}	
//		
//		return true;
	}
	
//	private boolean furthestPointsPassable(Location location, Radius radius) {
//		Location upperLimit = new Location(location.getX(),location.getY()+radius.getRadius());
//		Location lowerLimit = new Location(location.getX(),location.getY()-radius.getRadius());
//		Location leftLimit = new Location(location.getX()-radius.getRadius(),location.getY());
//		Location rightLimit = new Location(location.getX()+radius.getRadius(),location.getY());
//		
//		if(!isPassable(upperLimit)) {
//			return false;
//		}
//		if(!isPassable(lowerLimit)) {
//			return false;
//		}
//		if(!isPassable(leftLimit)) {
//			return false;
//		}
//		if(!isPassable(rightLimit)) {
//			return false;
//		}
//		return true;
//	}

	/**
	 * Checks whether a gameObject fully lies within passable terrain.
	 * 
	 * @param gameObject
	 * @return | result == this.isPassable(gameObject.getLocation(),gameObject.getRadius())
	 */
	public boolean isPassable(GameObject gameObject) {
		return this.isPassable(gameObject.getLocation(),gameObject.getRadius());
	}
	
	/**
	 * The worm turn cycle for this game.
	 * 
	 * @post | wormTurnCycle != null
	 * 
	 * @post 	| for each worm in wormTurnCycle
	 * 			|	worm!=null
	 * 
	 * @note we do not create a getter or setter for this variable since the "outside" world
	 * 		should never handle this variable directly.
	 */
	private LinkedList<Worm> wormTurnCycle = new LinkedList<Worm>();
	
	/**
	 * Removes a worm from an active game. The worm has died.
	 * 
	 * @post | new.wormTurnCycle.contains(worm) == false
	 */
	public void removeFromTurnCycle(Worm worm) {
//		try {
			wormTurnCycle.remove(worm);
//		} Can actually never happen 	|
//		catch (Exception e)			   \|/
//		{
//			throw new IllegalArgumentException("Worm does not exist in the wormTurnCycle");
//		}
		
	}
	
	/**
	 * Starts a game in this worm
	 * 
	 * @post 	| if 		this.getAllObjectsOfType(Worm.class).size() != 0
	 * 			| 	new.wormTurnCycle.size() != 0
	 * 			| new.isGameActive() == true
	 */
	public void startGame() {

		this.createTurnCycle();
		this.setGameActive(true);
		
		if(this.wormTurnCycle.size() >= 1) {
			this.wormTurnCycle.getFirst().resetTurn();
		}

	}
	
	/**
	 * Ends the current game in worms, doesn't do anything if no game is currently running.
	 * 
	 * @post 	| new.wormTurnCycle.size() == 0
	 * 			| new.isGameActive() == false
	 */
	public void endGame() {
		this.setGameActive(false);
		this.wormTurnCycle.clear();
	}
	
	/**
	 * Generates a turn cycle for worms when a game starts in this world
	 * 
	 * @post | for each worm in this.getAllObjectsOfType(Worm.class)
	 * 		 |	new.wormTurnCycle.contains(worm)
	 */
	public void createTurnCycle() {
		this.wormTurnCycle.clear();
		ArrayList<Worm> gameWorms = this.getAllObjectsOfType(Worm.class);
		for(Worm worm : gameWorms) {
			this.wormTurnCycle.add(worm);
		}
	}
	
	/**
	 * Returns the currently to be controlled worm.
	 * 
	 * @return 	| if this.wormTurnCycle.isEmpty() then
	 * 			|	result == null
	 * 			| else
	 * 			|	result == this.wormTurnCycle.getFirst()
	 */
	public Worm getFirstPlayerWorm() {
		if(this.wormTurnCycle.isEmpty()) {
			return null;
		}
		return this.wormTurnCycle.getFirst();
	}
	
	/**
	 * Switches and resets the currently controlled worms, only the newly controlled worm is reset.
	 * 
	 * @post 	| if this.wormTurnCycle.size() == 1 then
	 * 			|	new.wormTurnCycle == this.wormTurnCycle.get(0)
	 * 			| else
	 * 			| 	new.wormTurnCycle == this.wormTurnCycle.get(1)
	 * 			|	new.wormTurnCycle.getLast() == this.wormTurnCycle(0)
	 * 			|	new.wormTurnCycle.getFirst().resetTurn()
	 */
	public void endFirstPlayerWormTurn() throws IllegalStateException{
		if(!this.wormTurnCycle.isEmpty()) {
			this.wormTurnCycle.add(wormTurnCycle.getFirst());
			this.wormTurnCycle.remove(0);
			this.wormTurnCycle.getFirst().resetTurn();
			if(this.wormTurnCycle.get(0).getProgram()!=null) {
				this.wormTurnCycle.get(0).getProgram().doStartExecution();
			}
		}else {
			//throw new IllegalStateException("No worms to switch with, in endFirstPlayerWormTurn");
		}

	}
	
	/**
	 * A variable to check whether a game is active in this world.
	 */
	private boolean gameIsActive = false;
	
	/**
	 * Sets the activity of the game in this world
	 */
	@Basic
	public void setGameActive(boolean active) {
		this.gameIsActive = active;
	}
	
	/**
	 * Checks whether a game is active in this world
	 */
	@Basic
	public boolean getIsGameActive() {
		return this.gameIsActive;
	}
	
	/**
	 * Returns this world's teams.
	 */
	@Basic @Raw
	public Set<Team> getTeams(){
		return new HashSet<Team>(this.worldTeams);
	}
	
	/**
	 * Creates and adds a team to this world
	 * 
	 * @param name
	 * 
	 * @post | new.getTeams().contains(team)
	 * 
	 * @post | new.getTeam(name) != null
	 */
	public void createTeam(Name name) {
		Team team = new Team(this, name);
		this.addTeam(team);
	}

	/**
	 * Try to add a team to this world.
	 * 
	 * @param team
	 * 		| The team
	 * 
	 * @throws IllegalArgumentException
	 * 		| (team == null) || (this.getTeams().contains(team)) || (team.isTerminated())
	 * @throws IllegalStateException
	 * 		| worldTeams.size() >= 10
	 * 
	 * @post
	 * 		| new.getTeams().contains(team)
	 */
	public void addTeam(Team team) throws IllegalArgumentException,IllegalStateException{
		if(team == null) {
			throw new IllegalArgumentException("Error in World.addTeam, team is not effective");
		}
		if(this.getTeams().contains(team)) {
			throw new IllegalArgumentException("Error in World.addTeam, team already in world");
		}
		if(team.isTerminated()) {
			throw new IllegalArgumentException("Error in World.addTeam, team argument terminated");
		}
		if(worldTeams.size() >= 10) {
			throw new IllegalStateException("Error in World.addTeam, a game world may contain up to 10 teams.");
		}
		worldTeams.add(team);
	}

	/**
	 * Returns this world's team based on a given name.
	 * 
	 * @param name
	 * @return
	 * 		| for each team in this.getTeams()
	 * 		|	if team.getName().equals(name.getName())
	 * 		|		result == team
	 * 		| result == null
	 */
	public Team getTeam(Name name) {
		for (Team team : this.getTeams()) {
			if(team.getName().equals(name.getName())) {
				return team;
			}
		}
		return null;
	}
	
	/**
	 * This world's set of teams.
	 */
	private final Set<Team> worldTeams;
	
	/**
	 * Variable to keep track whether this world is terminated
	 */
	private boolean isTerminated = false;
	
	/**
	 * Terminate this world
	 * 
	 * @post 	| for each object in new.getAllGameObjects()
	 * 			|	object.isTerminated() == true
	 * 			| new.wormTurnCycle == null
	 * 			| new.isTerminated() == true
	 * 			| new.worldTeams.size() == 0
	 */
	public void terminate() {
		this.isTerminated = true;
		
		for(Object o : this.getAllGameObjects()) {
			if(o instanceof GameObject) {
				((GameObject) o).terminate();
			}
		}
		
		worldTeams.clear();
		
		
		wormTurnCycle.clear();
		wormTurnCycle = null;
	}

	/**
	 * Checks whether this world is terminated
	 * @return | this.isTerminated
	 */
	@Basic
	public boolean isTerminated() {
		return this.isTerminated;
	}
	
	//TODO
	public String getWinner() {
		if(!this.getIsGameActive()) {
			return null;
		}
		//beide opties wormen met en zonder team
		if((this.getTeams().size() >=1) && wormsWithoutTeam()){
			for(Team o : this.getTeams()) {
				if(o.getAlphabeticalListTeamRoster().size() > 0) {
					teamsActive += 1;
				}
				
				if(o.getAlphabeticalListTeamRoster().size() == 0) {
					o.terminate();
				}
			}
			
			if(teamsActive >= 1) {
				return null;
			}
		}
		//ALLEEN TEAMS
		if(this.getTeams().size() >= 1) {
			if(!onlyWormsWithoutTeamInWorld()) {
				for(Team o : this.getTeams()) {
					if(o.getAlphabeticalListTeamRoster().size() > 0) {
						teamsActive += 1;
					}
					
					if(o.getAlphabeticalListTeamRoster().size() == 0) {
						o.terminate();
					}
				}
					
				if (teamsActive == 1) {
					for(Team p : this.getTeams()) {
						if(p.getAlphabeticalListTeamRoster().size() > 0) {
							return p.getName();
						}
					}
				}
				else
				{
					return null;
				}	
			}//blabla
		}
		//ALLEEN TEAMLOZE WORMS
		if (onlyWormsWithoutTeamInWorld()) {
			if(onlyOneWorm()) {
				return this.getFirstPlayerWorm().getName();
			}
			else {
				return null;
			}
		}	
		return null;
	}
		
	/**
	 * Returns true if there are teamless worm's alive in the world
	 * 
	 * @return
	 * 
	 * @return 	| for Worm worm in  this.getAllObjectsOfType(Worm.class):
	 * 			|	if worm.isAlive() && worm.getTeam() == null
	 * 			|		result == true
	 * 			| result == false
	 */
	private boolean wormsWithoutTeam() {
		for (GameObject worm : this.getAllObjectsOfType(Worm.class)) {
			if (worm instanceof Worm){
				if (((Worm) worm).getTeam() == null)
					return true;
			}
		}
		return false;
	}

	public int teamsActive;
	
	public boolean onlyWormsWithoutTeamInWorld() {
		for(GameObject worm : this.getAllObjectsOfType(Worm.class)) {
			if(worm instanceof Worm) {
				if(((Worm) worm).getTeam() != null) {
					return false;
				}
			}
		}
		return true;
	}
	
	//TODO
	public boolean onlyOneWorm() {
		return (this.getAllObjectsOfType(Worm.class).size() == 1);
	}
	//No DOCU required
	public void castSpell() throws IllegalStateException{
		if(this.getAllGameObjects().size() < 2) {
			throw new IllegalStateException();
		}
		
		int randomObjectIdx1 = (int) Math.round(Math.random()*((this.getAllGameObjects().size())-1));
		int randomObjectIdx2 = (int) Math.round(Math.random()*((this.getAllGameObjects().size())-1));
		ArrayList<Object> gameObjects = new ArrayList<Object>(this.getAllGameObjects());
		
		while(randomObjectIdx1 == randomObjectIdx2) {
			randomObjectIdx2 = (int) Math.round(Math.random()*((this.getAllGameObjects().size())-1));
		}
		
		if(gameObjects.get(randomObjectIdx1) instanceof GameObject && gameObjects.get(randomObjectIdx2) instanceof GameObject){
			GameObject object1 = (GameObject) gameObjects.get(randomObjectIdx1);
			GameObject object2 = (GameObject) gameObjects.get(randomObjectIdx2);
			spell(object1,object2);
			
		}
		else {
			throw new IllegalStateException();
		}
	}
	//No DOCU required
	public void spell(GameObject go1, GameObject go2)
	{
		if(go1 instanceof Worm) {
			if(go2 instanceof Worm) {
				spell((Worm)go1,(Worm)go2);
			}else if(go2 instanceof Food) {
				spell((Worm)go1,(Food)go2);
			}else if(go2 instanceof Projectile) {
				spell((Worm)go1,(Projectile)go2);
			}
		}
		if(go1 instanceof Food) {
			if(go2 instanceof Worm) {
				spell((Food)go1,(Worm)go2);
			}else if(go2 instanceof Food) {
				spell((Food)go1,(Food)go2);
			}else if(go2 instanceof Projectile) {
				spell((Food)go1,(Projectile)go2);
			}
		}
		if(go1 instanceof Projectile) {
			if(go2 instanceof Worm) {
				spell((Projectile)go1,(Worm)go2);
			}else if(go2 instanceof Food) {
				spell((Projectile)go1,(Food)go2);
			}else if(go2 instanceof Projectile) {
				spell((Projectile)go1,(Projectile)go2);
			}
		}
	};
	//No DOCU required
	public void spell(Food obj1, Food obj2) {
		//If both objects are portions of food, they will both individually change
		//state, i.e., from healthy to poisoned or vice versa.
		obj1.setPoisoned(!obj1.isPoisoned());
		obj2.setPoisoned(!obj2.isPoisoned());
	}
	//No DOCU required
	public void spell(Worm obj1, Food obj2) {
		obj1.consumesFood(obj2);
	}
	//No DOCU required
	public void spell(Food obj1, Worm obj2) {
		spell(obj2, obj1);
	}
	//No DOCU required
	public void spell(Worm obj1, Worm obj2) {
		if(obj1.getTeam() == obj2.getTeam()) {
			BigInteger two = BigInteger.valueOf(2);
			BigInteger stuff = (obj1.getHitPoints().add(obj2.getHitPoints()).divide(BigInteger.valueOf(2)));
			HP hitPointTmp = new HP((obj1.getHitPoints().add(obj2.getHitPoints()).divide(two)));
			obj1.setHitPoints(new HP(stuff));
			obj2.setHitPoints(new HP(stuff));
		}
		else {
			if(obj1.getRadius().getRadius() < obj2.getRadius().getRadius()) {
				obj1.setActionPoints(obj1.getCurrentActionPoints()+5);
				obj2.setActionPoints(obj2.getCurrentActionPoints()-5);
			}
			else {
				if(obj2.getCurrentActionPoints() < 5) {
					obj1.setActionPoints(obj2.getCurrentActionPoints());
					obj2.setActionPoints(0);
				}
				else {
				obj2.setActionPoints(obj2.getCurrentActionPoints()+5);
				obj1.setActionPoints(obj1.getCurrentActionPoints()-5);
			
				}
			}
		}
	}
	//No DOCU required
	public void spell(Projectile obj1, Worm obj2) {
		BigInteger tmp = BigInteger.valueOf(obj1.getHitPoints());
		HP diminshment = new HP((obj2.getHitPoints().subtract(tmp)));
		obj2.setHitPoints(diminshment);
		
		//newhp projectile
		//obj1.setHitPoints(HELPMEOUTBERND);
	}
	//No DOCU required
	public void spell(Worm obj1, Projectile obj2) {
		spell(obj2, obj1);
	}
	//No DOCU required
	public void spell(Projectile obj1, Projectile obj2) {
		obj1.setHitPoints(obj1.getHitPoints()+2);
		obj2.setHitPoints(obj2.getHitPoints()+2);
	}
	//No DOCU required
	public void spell(Projectile obj1, Food obj2) {
		obj1.terminate();
		obj2.terminate();
	}
	
	//No DOCU required
	public void spell(Food obj1, Projectile obj2) {
		spell(obj2, obj1);
	}
	

	/**
	 * Removes this team from the world. 
	 * 
	 * @param team
	 * 
	 * @post |new.worldTeams.contains(team) == false
	 */
	public void removeTeam(Team team) {
		this.worldTeams.remove(team);
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
	
	private final static double WORM_MINIMUM_RADIUS = 0.25;
	private final static double FOOD_DEFAULT_RADIUS = 0.20;
	private final static double WORM_DENSITY = 1062;
	private final static double FOOD_DENSITY = 150;
	private final static BigInteger WORM_MIN_HP = new BigInteger("1000");
	private final static BigInteger WORM_MAX_HP = new BigInteger("2000");
	
	public static BigInteger getWormMinHP() {
		return WORM_MIN_HP;
	}
	
	public static BigInteger getWormMaxHP() {
		return WORM_MAX_HP;
	}
	
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
	
	public static final double roundingHelper(double d,int precision) {
		return Math.round(d*Math.pow(10, precision))/Math.pow(10, precision);
	}
}

