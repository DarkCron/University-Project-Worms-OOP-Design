package worms.model.values;

import be.kuleuven.cs.som.annotate.*;
import worms.model.GameObject;
import java.util.ArrayList;

/**
 * A utility class for handling different types of GameObjects.
 * 
 * @invar |typeExists(this)
 * 
 * @invar | getID() == getTypeID(getGameObjectType())
 * 
 * @author bernd
 *
 */
@Value
public class GameObjectTypeID {
	
	/**
	 * Creates a new GameObjectTypeID
	 * 
	 * @post |new.getID() == ID
	 * @post |assignedTypesList.contains(new)
	 * 
	 */
	@Raw
	public GameObjectTypeID(Class<? extends GameObject> c) {
		this.GameObjectTypeID = getTypeID(c);
		this.assignedType = c;
		
		if(!typeExists(c)) {
			assignedTypesList.add(this);
		}
	}

	/**
	 * @result	| for  t in assignedTypesList
	 * 			|	if t.getGameObjectType() == c
	 * 			|		then
	 * 			|			result == t.getID()
	 * 			| result == typeCounter
	 */
	private long getTypeID(Class<? extends GameObject> c) {
		for(GameObjectTypeID t : assignedTypesList) {
			if(t.getGameObjectType().equals(c)) {
				return t.getID();
			}
		}
		return typeCounter++;
	}


	/**
	 * Returns the assigned ID.
	 */
	@Immutable @Basic @Raw
	public long getID() {
		return GameObjectTypeID;
	}
	
	/**
	 * Adds a new type to the ID list.
	 * @post | assignedTypesList.contains(ID)
	 * 
	 * @throws RuntimeException
	 * 		| assignedTypesList.contains(ID)
	 */
	public static void addType(GameObjectTypeID ID) throws RuntimeException{
		if(!typeExists(ID)) {
			assignedTypesList.add(ID);
		}else {
			throw new RuntimeException("Tried to add duplicate");
		}
	}
	
	/**
	 * Checks whether a given ID exists in assignedTypesList
	 * 
	 * @return | result == assignedTypesList.contains(ID)
	 */
	public static boolean typeExists(GameObjectTypeID ID) {
		return assignedTypesList.contains(ID);
	}
	
	/**
	 * Checks whether a given ID exists in assignedTypesList
	 * 
	 * @return  | for  t in assignedTypesList
	 * 			|		if(t.getGameObjectType() == type) 
	 *			| 			result == true
	 *			| result == false
	 *		
	 */
	public static boolean typeExists(Class<? extends GameObject> type) {
		for(GameObjectTypeID t : assignedTypesList) {
			if(t.getGameObjectType().equals(type)) {
				return true;
			}
		}
		return false;
	}
	
	public Class<? extends GameObject> getGameObjectType(){
		return assignedType;
	}
	
	/**
	 * A static list that contains every single possible type of GameObject Created during runtime.
	 * Keeps track of a unique ID number and class.
	 * 
	 * @invar | assignedTypesList != null
	 * 
	 * @invar 	| for each typeID in assignedTypesList
	 * 			|		typeID != null
	 * 			|		&& typeExists(typeID)
	 * 			|		&& typeID.getID() == getTypeID(typeID.getGameObjectType())
	 */
	private static final ArrayList<GameObjectTypeID> assignedTypesList = new ArrayList<GameObjectTypeID>();
	private static long typeCounter = 0;
	
	private final long GameObjectTypeID;
	private final Class<? extends GameObject> assignedType;
	
	/**
	 * Check whether this Direction is equal to the given object.
	 * 
	 * @return 
	 * 		  | result == 
	 * 		  |   ((obj != null)
	 * 		  |  && (this.getClass() == obj.getClass())
	 * 		  |  && this.getID() == ((GameObjectTypeID)obj).getID())
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		GameObjectTypeID otherID = (GameObjectTypeID) obj;
		return this.getID() == otherID.getID();
	}
	
	/**
	 * Returns the hashcode for this GameObjectTypeID.
	 */
	@Override
	public int hashCode() {
		return ((Long)getID()).hashCode();
		}

	/**
	 * Returns the Direction as a textual representation 
	 * @return
	 * 			| result.equals(getID() + ".")
	 */
	@Override
	public String toString() {
		return getID() + ".";
	}
}
