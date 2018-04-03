package worms.model;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.som.annotate.*;
import worms.model.values.Name;
/*
 * 
 * @author Liam
 */
public class Team {
	
	public Team(World world, Name name) throws IllegalArgumentException, IllegalArgumentException{
		if(!isValidWorld(world)) {
			throw new IllegalArgumentException();
		}
		if(!isValidTeamName(name)) {
			throw new IllegalArgumentException();
		}
		setWorld(world);
		this.teamName = name;
	}
	
	public void setWorld(World world) {
		this.fromWorld = world;
	}
	
	public static boolean isValidTeamName(Name name) {
		if(name == null || (!name.isValid())) {
			return false;
		}
		return true;
	}
	
	public static boolean isValidWorld(World world) {
		if(world.isTerminated()) {
			return false;
		}
		return true;
	}
	
	private ArrayList <Worm> teamRoster = new ArrayList<Worm>();
	private World fromWorld;
	
	/**
	 * Returns this team's name.
	 */
	@Basic @Raw
	public String getName() {
		return teamName.getName();
	}
	
	private final Name teamName;
	
	//TODO
	public void addWorm(Worm worm) throws IllegalArgumentException {
		if(worm==null) {
			throw new IllegalArgumentException("Given worm was null");
		}
		
		for (Worm o : teamRoster) {
			if(o.hasTheSameNameAs(worm)) {
				throw new IllegalArgumentException("Worm has the same name as another worm whithin this team.");
			}
			if(!o.hasCorrectTeamMass(worm)) {
				throw new IllegalArgumentException("This worm does not have a valid mass for this team.");
			}
		}
		
		teamRoster.add(worm);
		
		//Alphabetical
	}
	
	//TODO
	public void removeWorm(Worm worm) {
		if(worm==null) {
			throw new IllegalArgumentException("Given worm was null");
		}
		
		if(!teamRoster.contains(worm)) {
			throw new IllegalArgumentException("Given worm was not part of this team");
		}
		
		teamRoster.remove(worm);
	}
	
	public boolean canAddWormToTeam(Worm worm) {
		
		return false;
	}
	
	public boolean canHaveWormInTeam(Worm other) {
		for (Worm worm : teamRoster) {
			if(other != worm) {
				if(!worm.hasCorrectTeamMass(other)) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	public List<Worm> getAlphabeticalListTeamRoster() {
		return new ArrayList<Worm>(teamRoster);
	}

	public void terminate() {
		this.isTerminated = true;
	}
	
	@Basic
	public boolean isTerminated() {
		return this.isTerminated;
	}
	
	private boolean isTerminated = false;
	
	
}
