package worms.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
		if(world != null && world.isTerminated()) { //TODO doc
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
	public void addWorm(Worm ... worms) throws IllegalArgumentException {
		for (Worm worm : worms) {
			
			if(worm.isTerminated())
				throw new IllegalArgumentException("Given worm was terminated");
			
			if(Arrays.stream(worms).filter(w->w == worm).collect(Collectors.toList()).size() > 1) {
				throw new IllegalArgumentException();
			}
			
			for (Worm teamWorm : teamRoster) {
				if(!worm.hasCorrectTeamMass(teamWorm)) {
					throw new IllegalArgumentException();
				}
			}
			for (Worm otherWorm : worms) {
				if(otherWorm != worm) {
					if(otherWorm.hasTheSameNameAs(worm)) {
						throw new IllegalArgumentException();
					}
				}
			}
			
			if(worm.getTeam() != null) {//TODO DOC
				throw new IllegalArgumentException("This worm already has a team.");
			}

			if(!canHaveWormInTeam(worm)) {
				throw new IllegalArgumentException("Worm cannot be added to the team.");
			}
		}

		
		
		for (Worm worm : worms) {
			if(teamRoster.isEmpty()) {
				worm.setTeam(this);//TODO DOC
				teamRoster.add(worm);
			}else {
				int index = sortInTeamRoster(worm);
				worm.setTeam(this);//TODO DOC
				teamRoster.add(index, worm);
			}
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
	public void removeWorm(Worm ... worms) {
		for (Worm worm : worms) {
			if(worm==null) {
				throw new IllegalArgumentException("Given worm was null");
			}
			if(worm.getTeam()!=this) {
				throw new IllegalArgumentException("Wrong team");
			}
		}
		
		for (Worm worm : worms) {
			try
			{
				int index = sortInTeamRoster(worm);
				teamRoster.remove(index);
				worm.setTeam(null);
				//teamRoster.remove(sortInTeamRoster(worm));
			}
			catch (Exception e)
			{
				throw new IllegalArgumentException("Given worm was not part of this team");
			}
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
	public void mergeTeams(Team team) throws IllegalArgumentException {
		ArrayList<Worm> teamRosterCopy = new ArrayList<Worm>();		
		if(team == null) {
			throw new IllegalArgumentException("Team to be merged is null");
		}
		if (team.equals(this) != false) {
			throw new IllegalArgumentException("Both teams are the same team. Merging won't be able to.");
		}
		if(team.equals(this) == false) {
			for (Worm worm : team.teamRoster) {
				teamRosterCopy.add(worm);
				
				}
			for (Worm w : teamRosterCopy) {
				if(w == null) {
					return;
				}
				if(w.isTerminated()) {
					return;
				}
			}
			
			for (Worm worm : teamRosterCopy) {
				team.removeWorm(worm);
				
				teamRosterCopy = new ArrayList<Worm>(team.getAlphabeticalListTeamRoster());	
				if(!teamRosterCopy.isEmpty()) {
					Team fromTeam = teamRosterCopy.get(0).getTeam();
					try {
						fromTeam.removeWorm(teamRosterCopy.toArray(new Worm[teamRosterCopy.size()]));
						for (Worm worm1 : teamRosterCopy) {
							worm1.setTeam(null);
						}
						this.addWorm(teamRosterCopy.toArray(new Worm[teamRosterCopy.size()]));
					
					} catch (Exception e) {
						for (Worm worm1 : teamRosterCopy) {
							team.addWorm(teamRosterCopy.toArray(new Worm[teamRosterCopy.size()]));
							worm1.setTeam(fromTeam); //FIx original team
							throw e;
						}
					}

				}
				if (team.equals(this) != false) {
					throw new IllegalArgumentException("Both teams are the same team. Merging won't be able to.");
				}
			}
		}	
//			for (Worm worm : team.teamRoster) {
//				teamRosterCopy.add(worm);
//			}
//			for (Worm w : teamRosterCopy) {
//				if(w == null) {
//					return;
//				}
//				if(w.isTerminated()) {
//					return;
//				}
//			}
//			
//			for (Worm worm : teamRosterCopy) {
//				team.removeWorm(worm);
//				try {
//					this.addWorm(worm);	
//				} catch (Exception e) {
//					for(Worm w : teamRosterCopy) {
//						this.removeWorm(w);
//						team.addWorm(w);
//					}
//					return;
//				}	
//			}
//		}
		
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
		
		teamRoster.clear();
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
