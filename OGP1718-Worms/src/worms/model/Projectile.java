package worms.model;

import java.util.ArrayList;

import worms.exceptions.InvalidLocationException;
import worms.exceptions.InvalidRadiusException;
import worms.model.ShapeHelp.Circle;
import worms.model.interfaces.IJumpable;
import worms.model.values.Direction;
import worms.model.values.Location;
import worms.model.values.Radius;

public class Projectile extends GameObject implements IJumpable {
	
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
		double radius = Math.pow(((double)projectileMass/1000d)/(((double)PROJECTILE_DENSITY)*((double)(4d/3d)*Math.PI)), (double)(1d/3d));
		return new Radius(radius);
	}
	
	public static Projectile createProjectile(Projectile_Type type, Worm worm) {
		if(worm.getWorld() == null) {
			throw new IllegalArgumentException("Worm not in a world.");
		}
		
		Projectile projectile = null;
		Radius projectileRadius;
		Location projectileLocation;
		Direction projectileDirection;
		
		if(type == Projectile_Type.RIFLE) {
			projectileRadius = generateRadiusProjectile(RIFLE_PROJECTILE_MASS);
			projectileLocation = getProjectileSpawnLocation(worm,projectileRadius);
			projectileDirection = worm.getDirection(); //TODO to reference or not to reference, that's the question
			projectile = new Projectile(projectileLocation, projectileDirection, projectileRadius, worm.getWorld()) {
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
			projectile = new Projectile(projectileLocation, projectileDirection, projectileRadius, worm.getWorld()) {
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
					return 2.5d + (worm.getCurrentActionPoints()-BAZOOKA_COST) % 8;
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
	
	public void jump(double timeStep) throws InvalidLocationException,RuntimeException{
//		if(!(this.getDirection().getAngle() >= 0 && this.getDirection().getAngle() <= Math.PI)) {
//			throw new RuntimeException();
//		}

		
		double jumpTime = this.getJumpTime(timeStep);
		Location tmp = new Location(this.jumpStep(jumpTime));
		
		if(!this.getWorld().fullyContains(new Circle(tmp,this.getRadius()).getBoundingRectangle())) {
			throw new InvalidLocationException(tmp);
		}
		
		ArrayList<Worm> hitWorms = new ArrayList<Worm>();
		for (double i = 0.0; i < jumpTime; i+=timeStep) {
			for (Object worm : this.getWorld().getAllObjectsOfType(Worm.class)) {
				if(worm instanceof Worm) {
					if(!hitWorms.contains(worm)&&((Worm) worm).overlapsWith(new Circle(new Location(jumpStep(i)),this.getRadius()))) {
					//	((Worm) worm).overlapsWith(new Circle(new Location(jumpStep(i)),this.getRadius()));
						((Worm) worm).hitByProjectile(this);
						//((Worm) worm).setHitPoints(new HP(BigInteger.valueOf(this.getHitPoints() - this.getHitPoints())));
						hitWorms.add(((Worm) worm));
					}
				}
			}
		}
		
		this.setLocation(tmp);	
		

		if(!hitWorms.isEmpty()) {
			this.terminate();
		}
	}
	
	public double getJumpTime(double deltaT) throws RuntimeException{
		double jumpTime = this.calculateJumpTime();
		
		double jumptime = this.getLastPassableJumpStepTime(jumpTime,deltaT);
//		if(jumptime == 0) { //TODO DOC
//			throw new IllegalArgumentException("Deltatime and jumpTime too short.");
//		}
		
		return jumptime;
	}
	
	public double calculateJumpTime() {
		double speedY = this.getProjectileForce() * Math.sin(this.getDirection().getAngle());
		double time = speedY/World.getGravity();
		
		double distance = Math.pow(this.getProjectileForce(), 2)*Math.sin(this.getDirection().getAngle()*2D)/World.getGravity();
		double timeInterval = (distance/(this.getProjectileForce()*Math.cos(this.getDirection().getAngle())));
		return timeInterval;
	}
	
	private double getLastPassableJumpStepTime(double jumpTime, double deltaT) {
		double lastAdjacentTime = 0.0; //TODO set 0.0
		Location wormLoc = new Location(this.jumpStep(lastAdjacentTime));
		boolean bHitSomething = false;
		while (this.getWorld().isPassable(wormLoc, this.getRadius()) && this.getWorld().fullyContains(new Circle(wormLoc,this.getRadius()).getBoundingRectangle())) {
			if(this.getWorld().isAdjacantToImpassableTerrain(wormLoc, this.getRadius())) {
				break;
			}
			//GHOSTBULLETBUSTERS
			for (Object worm : this.getWorld().getAllObjectsOfType(Worm.class)) {
				if(worm instanceof Worm) {
					if(((Worm) worm).overlapsWith(new Circle(wormLoc,this.getRadius()))) {
						//bHitSomething = true;
						//break;
					}
				}
			}
			if(bHitSomething) {
				lastAdjacentTime+=deltaT;
				break;
			}
			wormLoc = new Location(this.jumpStep(lastAdjacentTime));
			lastAdjacentTime+=deltaT;
		}

		

		return lastAdjacentTime;
	}
	
	public double[] jumpStep(double deltaTime) throws InvalidLocationException,IllegalArgumentException,RuntimeException{
//		if(!(this.getDirection().getAngle() >= 0 && this.getDirection().getAngle() <= Math.PI)) {
//			throw new RuntimeException("The direction of the worm trying to jump is invalid. Not equal or larger than 0 and less than 2*PI." + this.getDirection().toString());
//		}
//		if(this.getProjectileForce() == 8.5d) {
//			this.setProjectileForce(7.5d);
//		}
		double jumpSpeedMagnitude = this.getProjectileForce() / (this.getMass()/1000d);
		jumpSpeedMagnitude*=0.5;
		
		//speed in air
		double speedX = jumpSpeedMagnitude * Math.cos(this.getDirection().getAngle());
		double speedY = jumpSpeedMagnitude * Math.sin(this.getDirection().getAngle());
		//Position in air
		double xPosTime = this.getX()+(speedX*deltaTime);
		double yPosTime = this.getY()+((speedY*deltaTime) - ((1D/2D)*World.getGravity()*Math.pow(deltaTime,2)));
		
		Location tmpLocation = new Location(xPosTime,yPosTime);
		
		return tmpLocation.getLocation();
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
