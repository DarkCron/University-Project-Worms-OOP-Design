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
import worms.model.values.GameObjectTypeID;
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
		worm.turn(angle);
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
		World w;
		try {
			w = new World(width, height, passableMap);
		} catch (Exception e) {
			throw new ModelException(e);
		}
		return w;
	}

	@Override
	public void terminate(World world) throws ModelException {
		world.terminate();
	}

	@Override
	public boolean isTerminated(World world) throws ModelException {
		return world.isTerminated();
	}

	@Override
	public double getWorldWidth(World world) throws ModelException {
		return world.getWorldWidth();
	}

	@Override
	public double getWorldHeight(World world) throws ModelException {
		return world.getWorldHeight();
	}

	@Override
	public boolean isPassable(World world, double[] location) throws ModelException {
		return world.isPassable(new Location(location));
	}

	@Override
	public boolean isPassable(World world, double[] center, double radius) {
		return world.isPassable(new Location(center), new Radius(radius));
	}

	@Override
	public boolean isAdjacent(World world, double[] center, double radius) {
		return world.isAdjacantToImpassableTerrain(new Location(center), new Radius(radius));
	}

	@Override
	public boolean hasAsWorm(World world, Worm worm) throws ModelException {
		return world.hasGameObject(worm);
	}

	@Override
	public void addWorm(World world, Worm worm) throws ModelException {
		world.addGameObject(worm);
	}

	@Override
	public void removeWorm(World world, Worm worm) throws ModelException {
		world.removeGameObject(worm);
	}

	@Override
	public List<Worm> getAllWorms(World world) throws ModelException {
		return world.getAllObjectsOfType(Worm.class);
	}


	@Override
	public boolean hasAsFood(World world, Food food) throws ModelException {
		return world.hasGameObject(food);
	}
	

	
	@Override
	public void addFood(World world, Food food) throws ModelException {
		world.addGameObject(food);
	}

	@Override
	public void removeFood(World world, Food food) throws ModelException {
		world.removeGameObject(food);
	}

	@Override
	public Collection<Object> getAllItems(World world) throws ModelException {
		return world.getAllGameObjects();
	}

	@Override
	public Set<Team> getAllTeams(World world) throws ModelException {
		return world.getTeams();
	}

	@Override
	public boolean hasActiveGame(World world) throws ModelException {
		return world.getIsGameActive();
	}

	@Override
	public Worm getActiveWorm(World world) throws ModelException {
		return world.getFirstPlayerWorm();
	}

	@Override
	public void startGame(World world) throws ModelException {
		world.startGame();
	}

	@Override
	public void finishGame(World world) throws ModelException {
		world.endGame();
	}

	@Override
	public void activateNextWorm(World world) throws ModelException {
		world.endFirstPlayerWormTurn();
	}

	@Override //TODO
	public String getWinner(World world) {
		try {
			return world.getWinner();
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}

	@Override
	public Worm createWorm(World world, double[] location, double direction, double radius, String name, Team team)
			throws ModelException {
		try {
			Worm w = new Worm(new Location(location), new Direction(direction),world, new Radius(radius, World.getWormMinimumRadius()), new Name(name),team);
			this.addWorm(world, w);
			return w;
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}

	@Override
	public void terminate(Worm worm) throws ModelException {
		worm.terminate();
	}

	@Override
	public boolean isTerminated(Worm worm) throws ModelException {
		return worm.isTerminated();
	}

	@Override
	public double[] getLocation(Worm worm) throws ModelException {
		return worm.getLocation().getLocation();
	}

	@Override
	public BigInteger getNbHitPoints(Worm worm) throws ModelException {
		return worm.getHitPoints();
		
	}

	@Override
	public void incrementNbHitPoints(Worm worm, long delta) throws ModelException {
		worm.increaseHitPoints(delta);
	}

	@Override
	public World getWorld(Worm worm) throws ModelException {
		return worm.getWorld();
	}

	@Override
	public double[] getFurthestLocationInDirection(Worm worm, double direction, double maxDistance)
			throws ModelException {
		// TODO Auto-generated method stub
		try {
			return worm.getFurthestLocationInDirection(new Direction(direction), maxDistance).getLocation();
		} catch (Exception e) {
			throw new ModelException(e);
		}

	}

	@Override
	public void move(Worm worm) throws ModelException {
		try {
			worm.move();
		} catch (Exception e) {
			throw new ModelException(e);
		}
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
		try {
			Food f = new Food(new Location(location), new Radius(World.getFoodRadius(),World.getFoodRadius()),world);
			this.addFood(world, f);
			return f;
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}

	@Override
	public void terminate(Food food) throws ModelException {
		try {
			food.terminate();
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}

	@Override
	public boolean isTerminated(Food food) throws ModelException {
		return food.isTerminated();
	}

	@Override
	public double[] getLocation(Food food) throws ModelException {
		return food.getLocation().getLocation();
	}

	@Override
	public double getRadius(Food food) throws ModelException {
		return food.getRadius().getRadius();
	}

	@Override
	public double getMass(Food food) throws ModelException {
		return food.getMass();
	}

	@Override
	public World getWorld(Food food) throws ModelException {
		return food.getWorld();
	}

	@Override
	public Team createTeam(World world, String name) throws ModelException, MustNotImplementException {
		try {
			world.createTeam(new Name(name));
			return world.getTeam(new Name(name));
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}
	
	@Override
	public void terminate(Team team) throws ModelException, MustNotImplementException {
		try {
			team.terminate();
		} catch (Exception e) {
			new ModelException(e);
		}
	}
	
	@Override
	public boolean isTerminated(Team team) throws ModelException, MustNotImplementException {
		return team.isTerminated();
	}
	
	@Override
	public String getName(Team team) throws ModelException, MustNotImplementException {
		try {
			return team.getName();
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}
	
	@Override
	public Team getTeam(Worm worm) throws ModelException {
		return worm.getTeam();
	}
	
	@Override
	public int getNbWormsOfTeam(Team team) throws ModelException, MustNotImplementException {
		return team.getAlphabeticalListTeamRoster().size();
	}
	
	@Override
	public List<Worm> getAllWormsOfTeam(Team team) throws ModelException, MustNotImplementException {
		return team.getAlphabeticalListTeamRoster();
	}
	
	@Override
	public void addWormsToTeam(Team team, Worm... worms) throws ModelException, MustNotImplementException {
		// TODO Auto-generated method stub
		try {
			for (Worm worm : worms) {
				team.addWorm(worm);
			}
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}
	
	@Override
	public void removeWormsFromTeam(Team team, Worm... worms) throws ModelException, MustNotImplementException {
		// TODO Auto-generated method stub
		try {
			for (Worm worm : worms) {
				team.removeWorm(worm);
			}
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}
	
	@Override
	public void mergeTeams(Team recevingTeam, Team supplyingTeam) throws ModelException, MustNotImplementException {
		// TODO Auto-generated method stub
		IFacade.super.mergeTeams(recevingTeam, supplyingTeam);
	}

	@Override
	public void eat(Worm worm) {
		try {
			if(worm!=null) {
				worm.checkForFood();
			}
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}
	
}
