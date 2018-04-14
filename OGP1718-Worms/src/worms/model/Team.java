package worms.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
	 * Adds the given worm to this team
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
		
		
		if(teamRoster.isEmpty()) {
			teamRoster.add(worm);
		}else {
			int index = sortInTeamRoster(worm);
			teamRoster.add(index, worm);
		}
		
		
	}
	
	public int sortInTeamRoster(Worm worm) {
		int low = 0;
		int high = teamRoster.size() -1;

		while(low <= high) {
			int mid = (low+high)/2;
			if(teamRoster.get(mid).getName().compareTo(worm.getName()) == 0){
				return mid;
			}
			else if(teamRoster.get(mid).getName().compareTo(worm.getName()) < 0){
				low = mid+1;
			}
			else if(teamRoster.get(mid).getName().compareTo(worm.getName()) > 0){
				high = mid -1;
			}

		}
		return low;
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
		try
		{
			teamRoster.remove(sortInTeamRoster(worm));
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Given worm was not part of this team");
		}
		
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
	 * Places all worms from one team to this team.
	 * @param team The team of which all its worms have to merge with this team.
	 */
	public void mergeTeams(Team team) {
		//TODO check nog of team != null, anders exception (defensief)
		//TODO check of het argument team, team.equals(this) == false anders exception
		if (multipleTeamsRunning()) { //Is dit echt nodig?
			for (Team obj : fromWorld.getTeams()) { // Niet nodig, nu wordt per team de code hieronder uitgevoerd (overbodig werk)
				if(obj != null) { //Als we argument team checken of die niet null is, dan is dit in orde
					for (Worm worm : team.teamRoster) {
						this.addWorm(worm);
						team.removeWorm(worm); //Goed :D
					}
				}
			}
		}
	}
	
	/**
	 * Checks whether multiple teams are running in the current world.
	 */
	@Basic
	public boolean multipleTeamsRunning() {
		if(fromWorld.getTeams().size() > 1) {
			return true;
		}
		return false;
	}
	
	/**
	 * Terminate this team
	 * 
	 * @post | new.isTerminated() == true
	 * TODO
	 */
	public void terminate() {
		this.isTerminated = true;
		this.fromWorld.removeTeam(this);
		
		for (Worm worm : getAlphabeticalListTeamRoster()) {
			worm.setTeam(null);
		}
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
