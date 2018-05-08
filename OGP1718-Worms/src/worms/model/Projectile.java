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
		double bazookaForce = 2.5d + worm.getCurrentActionPoints() % 8;
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
	
}
