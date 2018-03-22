package worms.facade;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import worms.exceptions.InvalidLocationException;
import worms.exceptions.InvalidRadiusException;
import worms.exceptions.InvalidWormNameException;
import worms.model.Food;
import worms.model.Team;
import worms.model.World;
import worms.model.Worm;
import worms.model.values.Direction;
import worms.model.values.Location;
import worms.model.values.Name;
import worms.model.values.Radius;
import worms.util.ModelException;
import worms.util.MustNotImplementException;

public class Facade implements IFacade {

	// Default constructor for FACADE class
	public Facade() {
	}

//	@Override
//	public Worm createWorm(double[] location, double direction, double radius, String name) throws ModelException {
//		Worm w = null;
//		
//		try {
//			w = new Worm(location, direction, radius, name);
//		} catch (InvalidLocationException e) {
//			throw new ModelException(e);
//		} catch (InvalidWormNameException e) {
//			throw new ModelException(e);
//		} catch (InvalidRadiusException e) {
//			throw new ModelException(e);
//		}
//		
//		return w;
//	}

//	@Override
//	public void move(Worm worm, int nbSteps) throws ModelException {
//		try {
//			worm.Move(nbSteps);
//		} catch (InvalidLocationException e) {
//			throw new ModelException(e);
//		}
//	}

	@Override
	public void turn(Worm worm, double angle) throws ModelException {
		worm.turn(new Direction(angle));
	}

//	@Override
//	public void jump(Worm worm) throws ModelException {
//		try {
//			worm.Jump();
//		} catch (InvalidLocationException e) {
//			throw new ModelException(e);
//		}
//	}

//	@Override
//	public double getJumpTime(Worm worm) throws ModelException {
//		return worm.getJumpTime();
//	}

	@Override
	public double[] getJumpStep(Worm worm, double t) throws ModelException {
		try {
			return worm.jumpStep(t);
		} catch (InvalidLocationException e) {
			throw new ModelException(e);
		}
	}

//	@Override
//	public double getX(Worm worm) throws ModelException {
//		return worm.getLocation()[0];
//	}

//	@Override
//	public double getY(Worm worm) throws ModelException {
//		return worm.getLocation()[1];
//	}

	@Override
	public double getOrientation(Worm worm) throws ModelException {
		return worm.getDirection().getAngle();
	}

	@Override
	public double getRadius(Worm worm) throws ModelException {
		return worm.getRadius().getRadius();
	}

	@Override
	public void setRadius(Worm worm, double newRadius) throws ModelException {
		try {
			worm.setRadius(new Radius(newRadius, World.getWormMinimumRadius()));
		} catch (InvalidRadiusException e) {
			throw new ModelException(e);
		}
	}

	@Override
	public long getNbActionPoints(Worm worm) throws ModelException {
		return worm.getCurrentActionPoints();
	}

	@Override
	public void decreaseNbActionPoints(Worm worm, long delta) throws ModelException {
		worm.setActionPoints(worm.getCurrentActionPoints() - (int)Math.ceil(delta));
	}

	@Override
	public long getMaxNbActionPoints(Worm worm) throws ModelException {
		return worm.getMaxActionPoints();
	}

	@Override
	public String getName(Worm worm) throws ModelException {
		return worm.getName();
	}
	
	@Override
	public void rename(Worm worm, String newName) throws ModelException {
		try {
			worm.setName(new Name(newName));
		} catch (InvalidWormNameException e) {
			throw new ModelException(e);
		}
	}

	@Override
	public double getMass(Worm worm) throws ModelException {
		return worm.getMass();
	}

	@Override
	public World createWorld(double width, double height, boolean[][] passableMap) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void terminate(World world) throws ModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isTerminated(World world) throws ModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getWorldWidth(World world) throws ModelException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getWorldHeight(World world) throws ModelException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isPassable(World world, double[] location) throws ModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPassable(World world, double[] center, double radius) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAdjacent(World world, double[] center, double radius) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAsWorm(World world, Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addWorm(World world, Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeWorm(World world, Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Worm> getAllWorms(World world) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasAsFood(World world, Food food) throws ModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addFood(World world, Food food) throws ModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeFood(World world, Food food) throws ModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<Object> getAllItems(World world) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Team> getAllTeams(World world) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasActiveGame(World world) throws ModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Worm getActiveWorm(World world) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startGame(World world) throws ModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finishGame(World world) throws ModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activateNextWorm(World world) throws ModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getWinner(World world) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Worm createWorm(World world, double[] location, double direction, double radius, String name, Team team)
			throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void terminate(Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isTerminated(Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double[] getLocation(Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigInteger getNbHitPoints(Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void incrementNbHitPoints(Worm worm, long delta) throws ModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public World getWorld(Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getFurthestLocationInDirection(Worm worm, double direction, double maxDistance)
			throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void move(Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getJumpTime(Worm worm, double deltaT) throws ModelException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void jump(Worm worm, double timeStep) throws ModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Food createFood(World world, double[] location) throws ModelException {
		Food f;
		try {
			f = new Food(new Location(location), new Radius(World.getFoodRadius(),World.getFoodRadius()));
		} catch (Exception e) {
			throw new ModelException(e);
		}

		return f;
	}

	@Override
	public void terminate(Food food) throws ModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isTerminated(Food food) throws ModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double[] getLocation(Food food) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getRadius(Food food) throws ModelException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMass(Food food) throws ModelException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public World getWorld(Food food) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Team createTeam(World world, String name) throws ModelException, MustNotImplementException {
		// TODO Auto-generated method stub
		return IFacade.super.createTeam(world, name);
	}
	
	@Override
	public void terminate(Team team) throws ModelException, MustNotImplementException {
		// TODO Auto-generated method stub
		IFacade.super.terminate(team);
	}
	
	@Override
	public boolean isTerminated(Team team) throws ModelException, MustNotImplementException {
		// TODO Auto-generated method stub
		return IFacade.super.isTerminated(team);
	}
	
	@Override
	public String getName(Team team) throws ModelException, MustNotImplementException {
		// TODO Auto-generated method stub
		return IFacade.super.getName(team);
	}
	
	@Override
	public Team getTeam(Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		return IFacade.super.getTeam(worm);
	}
	
	@Override
	public int getNbWormsOfTeam(Team team) throws ModelException, MustNotImplementException {
		// TODO Auto-generated method stub
		return IFacade.super.getNbWormsOfTeam(team);
	}
	
	@Override
	public List<Worm> getAllWormsOfTeam(Team team) throws ModelException, MustNotImplementException {
		// TODO Auto-generated method stub
		return IFacade.super.getAllWormsOfTeam(team);
	}
	
	@Override
	public void addWormsToTeam(Team team, Worm... worms) throws ModelException, MustNotImplementException {
		// TODO Auto-generated method stub
		IFacade.super.addWormsToTeam(team, worms);
	}
	
	@Override
	public void removeWormsFromTeam(Team team, Worm... worms) throws ModelException, MustNotImplementException {
		// TODO Auto-generated method stub
		IFacade.super.removeWormsFromTeam(team, worms);
	}
	
	@Override
	public void mergeTeams(Team recevingTeam, Team supplyingTeam) throws ModelException, MustNotImplementException {
		// TODO Auto-generated method stub
		IFacade.super.mergeTeams(recevingTeam, supplyingTeam);
	}
	
}
