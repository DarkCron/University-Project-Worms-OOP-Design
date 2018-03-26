package worms.model;

import java.util.ArrayList;
import java.util.List;

import javax.naming.InvalidNameException;

import worms.model.values.Name;
/*
 * 
 * @author Liam
 */
public class Team {
	
	public Team(World world, Name name) throws IllegalArgumentException, InvalidNameException{
		if(!isValidWorld(world)) {
			throw new IllegalArgumentException();
		}
		if(!isValidTeamName(name)) {
			throw new InvalidNameException();
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
	private final Name teamName;
	
	public void addWorm(Worm worm) {
		
	}
	
	public void removeWorm() {
		
	}
	
	public boolean canAddWormToTeam(Worm worm) {
		
		return false;
	}
	
	
	public List<Worm> getAlphabeticalListTeamRoster() {
		return new ArrayList<Worm>(teamRoster);
	}
	
	
}
