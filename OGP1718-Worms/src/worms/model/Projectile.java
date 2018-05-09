package worms.model;

import worms.exceptions.InvalidLocationException;
import worms.exceptions.InvalidRadiusException;
import worms.exceptions.NotEnoughAPException;
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

	private static Radius getRadius(int projectileMass) {
		double radius = Math.pow(((double)projectileMass/1000d)/(((double)PROJECTILE_DENSITY)*((double)(4d/3d)*Math.PI)), (double)(1d/3d));
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

	public void setHitPoints(int hitPoints) throws IllegalArgumentException{
//		if(this.getProjectileHPRequirement().isValidBullet(hitPoints)) {
//			this.hitPoints = hitPoints;
//		}else {
//			throw new IllegalArgumentException();
//		}
		this.hitPoints = hitPoints;
	}
	
	private int hitPoints;
	
	public int getCostAP() {
		return costAP;
	}

	public void setCostAP(int costAP) {
		this.costAP = costAP;
	}
	
	private int costAP;
	
	private int projectileMaxHp;
	

	public int getProjectileMaxHp() {
		return projectileMaxHp;
	}

	public void setProjectileMaxHp(int projectileMaxHp) {
		this.projectileMaxHp = projectileMaxHp;
	}
	
	private BulletHitPointsRequirement projectileHPRequirement;
	
	public BulletHitPointsRequirement getProjectileHPRequirement() {
		return projectileHPRequirement;
	}

	public void setProjectileHPRequirement(BulletHitPointsRequirement projectileHPRequirement) {
		this.projectileHPRequirement = projectileHPRequirement;
	}
	
	
//	public boolean isTerminated() {
//		return isTerminated;
//	}
//
//	public void setTerminated(boolean isTerminated) {
//		this.isTerminated = isTerminated;
//	}
//	
//	public void terminate() {
//		this.setTerminated(true);
//	}
//	
//	boolean isTerminated = false;
	

	
	private final static int RIFLE_PROJECTILE_MASS = 10;
	private final static int BAZOOKA_PROJECTILE_MASS = 300;
	private final static double RIFLE_FORCE = 1.5;
	private final static int RIFLE_COST = 10;
	private final static int BAZOOKA_COST = 25;
	private final static int PROJECTILE_DENSITY = 7800;
	private final static int RIFLE_BULLET_MAX_HP = 10;
	private final static int BAZOOKA_BULLET_MAX_HP = 7;
	
	@FunctionalInterface
	public interface BulletHitPointsRequirement{
		boolean isValidBullet(int hitpoints);
	}
	private final static BulletHitPointsRequirement RIFLE_BULLET_REQ = (hitpoints) ->{
		if(hitpoints < 0) {
			return false;
		}
		if(hitpoints > RIFLE_BULLET_MAX_HP) {
			return false;
		}
		if(hitpoints % 2 != 0) {
			return false;
		}
		return true;
	};	
	private final static BulletHitPointsRequirement BAZOOKA_BULLET_REQ = (hitpoints) ->{
		if(hitpoints < 0) {
			return false;
		}
		if(hitpoints > BAZOOKA_BULLET_MAX_HP) {
			return false;
		}
		if(hitpoints % 2 == 0) {
			return false;
		}
		return true;
	};

	@FunctionalInterface
	public interface CreateProjectile{
		Projectile create(Worm w);
	}
	
	public static CreateProjectile getRifle(Projectile_Type type) throws IllegalArgumentException{
		if(type == Projectile_Type.RIFLE) {
			return RIFLE;
		}else if(type == Projectile_Type.BAZOOKA) {
			return BAZOOKA;
		}
		
		throw new IllegalArgumentException();
	}


	private static CreateProjectile RIFLE = (worm) -> {
		if(worm.getWorld() == null) {
			throw new IllegalArgumentException("Worm not in a world.");
		}
		Radius projectileRadius = getRadius(RIFLE_PROJECTILE_MASS);
		Location projectileLocation = getProjectileSpawnLocation(worm,projectileRadius);
		Direction projectileDirection = worm.getDirection(); //TODO to reference or not to reference, that's the question

		Projectile bullet = new Projectile(projectileLocation,projectileDirection , projectileRadius, worm.getWorld());
		bullet.setProjectileForce(RIFLE_FORCE);
		bullet.setProjectileHPRequirement(RIFLE_BULLET_REQ);
		int randomNumber = (int)(Math.random()*4) + 1; //Random number from 1 - 5
		randomNumber*=2; // random even number between  2 - 10
		bullet.setProjectileMaxHp(RIFLE_BULLET_MAX_HP);
		bullet.setHitPoints(randomNumber);
		bullet.setMass(RIFLE_PROJECTILE_MASS);
		bullet.setCostAP(RIFLE_COST);
		return bullet;
	};

	private static CreateProjectile BAZOOKA = (worm) -> {
		if(worm.getWorld() == null) {
			throw new IllegalArgumentException("Worm not in a world.");
		}
		Radius projectileRadius = getRadius(BAZOOKA_PROJECTILE_MASS);
		Location projectileLocation = getProjectileSpawnLocation(worm,projectileRadius);
		Direction projectileDirection = worm.getDirection(); //TODO to reference or not to reference, that's the question

		Projectile bullet = new Projectile(projectileLocation,projectileDirection , projectileRadius, worm.getWorld());
		double bazookaForce = 2.5d + (worm.getCurrentActionPoints()-BAZOOKA_COST) % 8d;
		bullet.setProjectileForce(bazookaForce);
		bullet.setProjectileHPRequirement(BAZOOKA_BULLET_REQ);
		int randomNumber = (int)(Math.random()*6) + 1; //Random number from 1 - 7
		if(randomNumber % 2 == 0) {
			randomNumber -= 1;
		}
		bullet.setProjectileMaxHp(BAZOOKA_BULLET_MAX_HP);
		bullet.setHitPoints((int)(randomNumber*bazookaForce));
		bullet.setMass(BAZOOKA_PROJECTILE_MASS);
		bullet.setCostAP(BAZOOKA_COST);
		return bullet;
	};
	
	public void jump(double timeStep) throws InvalidLocationException,RuntimeException{
		if(!(this.getDirection().getAngle() >= 0 && this.getDirection().getAngle() <= Math.PI)) {
			throw new RuntimeException();
		}
		double jumpTime = this.getJumpTime(timeStep);
		Location tmp = new Location(this.jumpStep(jumpTime));
		
		if(!isValidLocation(tmp,this.getWorld())) {
			throw new InvalidLocationException(tmp);
		}
		
		this.setLocation(tmp);				
	}
	
	public double getJumpTime(double deltaT) throws RuntimeException{
		double jumpTime = this.calculateJumpTime();
		
		double jumptime = this.getLastPassableJumpStepTime(jumpTime,deltaT);
		if(jumptime == 0) { //TODO DOC
			throw new IllegalArgumentException("Deltatime and jumpTime too short.");
		}
		
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
		double lastAdjacentTime = 0.0;
		for (double i = deltaT; i < jumpTime; i+=deltaT) {
			Location wormLoc = new Location(this.jumpStep(i));
			if(!this.getWorld().isPassable(wormLoc, this.getRadius())) { //TODO Doc;
				return i;
			}
		}
		

		return lastAdjacentTime;
	}
	
	public double[] jumpStep(double deltaTime) throws InvalidLocationException,IllegalArgumentException,RuntimeException{
		if(!(this.getDirection().getAngle() >= 0 && this.getDirection().getAngle() <= Math.PI)) {
			throw new RuntimeException("The direction of the worm trying to jump is invalid. Not equal or larger than 0 and less than 2*PI." + this.getDirection().toString());
		}
//		if(this.getProjectileForce() == 8.5d) {
//			this.setProjectileForce(7.5d);
//		}
		double jumpSpeedMagnitude = this.getProjectileForce() / (this.getMass()/1000d);
		jumpSpeedMagnitude*=0.5;
		
		if(jumpSpeedMagnitude < 0) {
			throw new RuntimeException("The jumpSpeedMagnitude of the worm tring to jump is invalid. Less than 0 "+jumpSpeedMagnitude);
		}
		
		//speed in air
		double speedX = jumpSpeedMagnitude * Math.cos(this.getDirection().getAngle());
		double speedY = jumpSpeedMagnitude * Math.sin(this.getDirection().getAngle());
		//Position in air
		double xPosTime = this.getX()+(speedX*deltaTime);
		double yPosTime = this.getY()+((speedY*deltaTime) - ((1D/2D)*World.getGravity()*Math.pow(deltaTime,2)));
		
		Location tmpLocation = new Location(xPosTime,yPosTime);
		
		return tmpLocation.getLocation();
	}

}
