package worms.model;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.som.annotate.*;
import worms.model.values.Name;
/**
 * A class for managing teams of worms
 * @author Liam
 * 
 * @Invar | isValidTeamName(getName())
 * 
 * @Invar | canHaveTeamWorms(this)
 */
public class Team {
	
	/**
	 * Creates this team.
	 * 
	 * @param world
	 * @param name
	 * @throws IllegalArgumentException
	 * 		| isValidWorld(world) || isValidTeamName(name)
	 * 
	 * @post | new.teamName = name
	 * 
	 * @post | new.hasWorldAssigned = true;
	 * 
	 * @post | new.fromWorld = world;
	 */
	public Team(World world, Name name) throws IllegalArgumentException{
		if(!isValidWorld(world)) {
			throw new IllegalArgumentException();
		}
		if(!isValidTeamName(name)) {
			throw new IllegalArgumentException();
		}

		this.teamName = name;
		this.hasWorldAssigned = true;
		this.fromWorld = world;
	}
	
	/**
	 * Returns whether this team has a world assigned.
	 * 
	 */
	@Basic @Immutable
	public boolean hasWorldAssigned() {
		return this.hasWorldAssigned;
	}
	
	/**
	 * Checks whether the given name is valid for any and all team names
	 * @param name
	 * @return | name != null && (name.isValid())
	 */
	public static boolean isValidTeamName(Name name) {
		if(name == null || (!name.isValid())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks whether the given world is valid for any and all teams.
	 * 
	 * @param world
	 * @return | result == !world.isTerminated()
	 */
	public static boolean isValidWorld(World world) {
		if(world.isTerminated()) {
			return false;
		}
		return true;
	}
	
	/**
	 * A variable to check whether this team had it's world assigned to.
	 */
	private final boolean hasWorldAssigned;
	/**
	 * All the living worms in this team.
	 */
	private ArrayList <Worm> teamRoster = new ArrayList<Worm>();
	/**
	 * The world, if any, this team is created in.
	 */
	private final World fromWorld;
	
	/**
	 * Returns this team's name.
	 */
	@Basic @Raw @Immutable
	public String getName() {
		return teamName.getName();
	}
	
	/**
	 * This team's name
	 */
	private final Name teamName;
	
	/**
	 * Add the given worm to this team //TODO Liam
	 * @param worm
	 * @throws IllegalArgumentException
	 */
	public void addWorm(Worm worm) throws IllegalArgumentException {
		if(worm==null) {
			throw new IllegalArgumentException("Given worm was null");
		}
		
		for (Worm o : teamRoster) {
			if(!canHaveWormInTeam(o)) {
				throw new IllegalArgumentException("Worm cannot be added to the team.");
			}
		}
		
		teamRoster.add(worm);
		
		//Alphabetical
	}
	
	/**
	 * Remove a given worm from this team
	 * 
	 * @param worm
	 * 
	 * @post | !teamRoster.contains(worm)
	 */
	public void removeWorm(Worm worm) {
		if(worm==null) {
			throw new IllegalArgumentException("Given worm was null");
		}
		
		if(!teamRoster.contains(worm)) { //TODO replace this with the logarithmic search
			throw new IllegalArgumentException("Given worm was not part of this team");
		}
		
		teamRoster.remove(worm);
	}
	
	/**
	 * Checks whether all worms currently in this team are valid within this team.
	 * 
	 * @param team
	 * @return	| for each worm in team.getAlphabeticalListTeamRoster()
	 * 			|	if ! team.canHaveWormInTeam(worm) then
	 * 			|		result == false
	 * 			| result == true
	 */
	public static boolean canHaveTeamWorms(Team team) {
		for (Worm worm : team.getAlphabeticalListTeamRoster()) {
			if(!team.canHaveWormInTeam(worm)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks whether the given worm can be added to this team.
	 * @param other
	 * @return	| for each worm in teamRoster
	 * 			|	if other != worm then
	 * 			|		if !worm.hasCorrectTeamMass(other)  then
	 * 			|			result == false
	 * 			|		if other.hasTheSameNameAs(worm) then
	 * 			|			result == false
	 * 			| result == true
	 */
	public boolean canHaveWormInTeam(Worm other) {
		for (Worm worm : teamRoster) {
			if(other != worm) {
				if(!worm.hasCorrectTeamMass(other)) {
					return false;
				}
				if(other.hasTheSameNameAs(worm)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Returns this team's worms in alphabetical order
	 */
	@Basic
	public List<Worm> getAlphabeticalListTeamRoster() {
		return new ArrayList<Worm>(teamRoster);
	}

	/**
	 * Terminate this team
	 * 
	 * @post | new.isTerminated() == true
	 */
	public void terminate() {
		this.isTerminated = true;
	}
	
	/**
	 * Checks whether this team is terminated.
	 */
	@Basic
	public boolean isTerminated() {
		return this.isTerminated;
	}
	
	/**
	 * A variable to check whether this team is terminated.
	 */
	private boolean isTerminated = false;
	
	
}
