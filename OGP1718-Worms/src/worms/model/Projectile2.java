package worms.model;

import worms.exceptions.InvalidLocationException;
import worms.exceptions.InvalidRadiusException;
import worms.model.values.Direction;
import worms.model.values.Location;
import worms.model.values.Radius;

public class Projectile2 extends GameObject {
	
	public enum Projectile_Type{
		RIFLE, BAZOOKA;
	}

	public Projectile2(Location location,Direction direction, Radius radius, World world)
			throws InvalidLocationException, InvalidRadiusException {
		super(location, radius, world);
		this.setDirection(direction);
	}

	@Override
	public void generateMass() {
		//Nothing to do.
	}
	
	private static Location getProjectileSpawnLocation(Worm worm, Radius projectileRadius) {
		double distance = worm.getRadius().getRadius() + projectileRadius.getRadius();
		double x = worm.getX() + Math.cos(worm.getDirection().getAngle())*distance;
		double y = worm.getY() + Math.sin(worm.getDirection().getAngle())*distance;
		Location loc = new Location(x, y);
		return loc;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	private Direction direction;

	public int getHitPoints() {
		return hitPoints;
	}

	public void setHitPoints(int hitPoints) {
		this.hitPoints = hitPoints;
	}
	
	private int hitPoints;
	
	public int getMaxHitPoints() {
		return maxHitPoints;
	}

	public void setMaxHitPoints(int maxHitPoints) {
		this.maxHitPoints = maxHitPoints;
	}
	
	private int maxHitPoints;
	
	public int getActionPointsCost() {
		return actionPointsCost;
	}

	public void setActionPointsCost(int actionPointsCost) {
		this.actionPointsCost = actionPointsCost;
	}

	private int actionPointsCost;
	
	public double getProjectileForce() {
		return projectileForce;
	}

	public void setProjectileForce(double projectileForce) {
		this.projectileForce = projectileForce;
	}

	private double projectileForce;
	
	private static Radius generateRadiusProjectile(int projectileMass) {
		double radius = Math.pow(((double)projectileMass)/(((double)PROJECTILE_DENSITY)*((double)(4/3)*Math.PI)), (double)(1/3));
		return new Radius(radius);
	}
	
	public static Projectile2 createProjectile(Projectile_Type type, Worm worm) {
		if(worm.getWorld() == null) {
			throw new IllegalArgumentException("Worm not in a world.");
		}
		
		Projectile2 projectile = null;
		Radius projectileRadius;
		Location projectileLocation;
		Direction projectileDirection;
		
		if(type == Projectile_Type.RIFLE) {
			projectileRadius = generateRadiusProjectile(RIFLE_PROJECTILE_MASS);
			projectileLocation = getProjectileSpawnLocation(worm,projectileRadius);
			projectileDirection = worm.getDirection(); //TODO to reference or not to reference, that's the question
			projectile = new Projectile2(projectileLocation, projectileDirection, projectileRadius, worm.getWorld()) {
				@Override
				public int generateHP() {
					int randomNumber = (int)(Math.random()*4) + 1; //Random number from 1 - 5
					randomNumber*=2; // random even number between  2 - 10
					return randomNumber;
				}			
				@Override
				public void hit(Worm worm) {
					// TODO Auto-generated method stub
					super.hit(worm);
				}
				@Override
				public boolean isValidHitPoints(int hp) {
					if(hp > this.getMaxHitPoints()) {
						return false;
					}
					if(hp <= 0) {
						return false;
					}
					if(hp % 2 != 0 ) {
						return false;
					}
					return true;
				}
				
				@Override
				public double generateForce(Worm worm) {
					return RIFLE_FORCE;
				}
				
				{
					this.setProjectileForce(this.generateForce(worm));
					this.setMaxHitPoints(RIFLE_BULLET_MAX_HP);
					this.setActionPointsCost(RIFLE_COST);
					this.setMass(RIFLE_PROJECTILE_MASS);
					int randomHP = this.generateHP();
					if(this.isValidHitPoints(randomHP)) {
						this.setHitPoints(randomHP);
					}
					
				}				
			};
		}else if(type == Projectile_Type.BAZOOKA) {
			projectileRadius = generateRadiusProjectile(BAZOOKA_PROJECTILE_MASS);
			projectileLocation = getProjectileSpawnLocation(worm,projectileRadius);
			projectileDirection = worm.getDirection(); //TODO to reference or not to reference, that's the question
			projectile = new Projectile2(projectileLocation, projectileDirection, projectileRadius, worm.getWorld()) {
				@Override
				public int generateHP() {
					int randomNumber = (int)(Math.random()*6) + 1; //Random number from 1 - 7
					if(randomNumber % 2 == 0) {
						randomNumber -= 1;
					}
					return randomNumber;
				}			
				@Override
				public void hit(Worm worm) {
					// TODO Auto-generated method stub
					super.hit(worm);
				}
				@Override
				public boolean isValidHitPoints(int hp) {
					if(hp > this.getMaxHitPoints()) {
						return false;
					}
					if(hp <= 0) {
						return false;
					}
					if(hp % 2 == 0 ) {
						return false;
					}
					return true;
				}
				
				@Override
				public double generateForce(Worm worm) {
					return 2.5d + worm.getCurrentActionPoints() % 8;
				}
				
				{
					this.setProjectileForce(this.generateForce(worm));
					this.setMaxHitPoints(BAZOOKA_BULLET_MAX_HP);
					this.setActionPointsCost(BAZOOKA_COST);
					this.setMass(BAZOOKA_PROJECTILE_MASS);
					int randomHP = this.generateHP();
					if(this.isValidHitPoints(randomHP)) {
						this.setHitPoints(randomHP);
					}
				}				
			};
		}
		
		return projectile;
	}
	
	public double generateForce(Worm worm) {
		throw new UnsupportedOperationException();
	}

	public int generateHP() {
		throw new UnsupportedOperationException();
	}

	public void hit(Worm worm){
		throw new UnsupportedOperationException();
	}

	public boolean isValidHitPoints(int hp){
		throw new UnsupportedOperationException();
	}	
	
	
	private final static int RIFLE_PROJECTILE_MASS = 10;
	private final static int BAZOOKA_PROJECTILE_MASS = 300;
	private final static double RIFLE_FORCE = 1.5;
	private final static int RIFLE_COST = 10;
	private final static int BAZOOKA_COST = 25;
	private final static int PROJECTILE_DENSITY = 7800;
	private final static int RIFLE_BULLET_MAX_HP = 10;
	private final static int BAZOOKA_BULLET_MAX_HP = 7;
}
