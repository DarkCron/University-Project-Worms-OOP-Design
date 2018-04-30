package worms.model;

import worms.exceptions.InvalidLocationException;
import worms.exceptions.InvalidRadiusException;
import worms.model.values.Direction;
import worms.model.values.Location;
import worms.model.values.Radius;

public class Projectile extends GameObject {
	
	public enum Projectile_Type{
		RIFLE, BAZOOKA;
	}

	public Projectile(Location location,Direction direction, Radius radius, World world)
			throws InvalidLocationException, InvalidRadiusException {
		super(location, radius, world);
		this.setDirection(direction);
	}

	@Override
	public void generateMass() {
		//Nothing to do.
	}
	
	public Location getProjectileSpawnLocation(Worm worm) {
		// TODO Auto-generated method stub
		return null;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	private Direction direction;

	private Radius getRadius(int projectileMass) {
		double radius = Math.pow(((double)projectileMass)/(((double)PROJECTILE_DENSITY)*((double)(4/3)*Math.PI)), (double)(1/3));
		return new Radius(radius);
	}
	
	public double getProjectileForce() {
		return projectileForce;
	}

	public void setProjectileForce(double projectileForce) {
		this.projectileForce = projectileForce;
	}

	private double projectileForce;
	
	public int getHitPoints() {
		return hitPoints;
	}

	public void setHitPoints(int hitPoints) {
		this.hitPoints = hitPoints;
	}
	
	private int hitPoints;
	
	
	@FunctionalInterface
	public interface CreateProjectile{
		Projectile create(Worm w);
	}
	
	private final int RIFLE_PROJECTILE_MASS = 10;
	private final int BAZOOKA_PROJECTILE_MASS = 300;
	private final double RIFLE_FORCE = 1.5;
	private final int RIFLE_COST = 10;
	private final int BAZOOKA_COST = 25;
	private final int PROJECTILE_DENSITY = 7800;
	
	CreateProjectile RIFLE = (worm) -> {
		if(worm.getWorld() == null) {
			throw new IllegalArgumentException("Worm not in a world.");
		}
		Location projectileLocation = getProjectileSpawnLocation(worm);
		Direction projectileDirection = worm.getDirection(); //TODO to reference or not to reference, that's the question
		Radius projectileRadius = getRadius(RIFLE_PROJECTILE_MASS);
		Projectile bullet = new Projectile(projectileLocation,projectileDirection , projectileRadius, worm.getWorld());
		bullet.setProjectileForce(RIFLE_FORCE);
		int randomNumber = (int)(Math.random()*4) + 1; //Random number from 1 - 5
		randomNumber*=2; // random even number between  2 - 10
		bullet.setHitPoints(randomNumber);
		bullet.setMass(RIFLE_PROJECTILE_MASS);
		return bullet;
	};

	CreateProjectile BAZOOKA = (worm) -> {
		if(worm.getWorld() == null) {
			throw new IllegalArgumentException("Worm not in a world.");
		}
		Location projectileLocation = getProjectileSpawnLocation(worm);
		Direction projectileDirection = worm.getDirection(); //TODO to reference or not to reference, that's the question
		Radius projectileRadius = getRadius(BAZOOKA_PROJECTILE_MASS);
		Projectile bullet = new Projectile(projectileLocation,projectileDirection , projectileRadius, worm.getWorld());
		double bazookaForce = 2.5d + worm.getCurrentActionPoints() % 8;
		bullet.setProjectileForce(bazookaForce);
		int randomNumber = (int)(Math.random()*6) + 1; //Random number from 1 - 7
		if(randomNumber % 2 == 0) {
			randomNumber -= 1;
		}
		bullet.setHitPoints((int)(randomNumber*bazookaForce));
		bullet.setMass(BAZOOKA_PROJECTILE_MASS);
		return bullet;
	};
	
}
