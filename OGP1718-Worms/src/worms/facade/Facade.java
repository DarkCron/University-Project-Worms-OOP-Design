package worms.facade;

import worms.model.Worm;
import worms.util.ModelException;

public class Facade implements IFacade {

	// Default constructor for FACADE class
	public Facade() {
	}

	@Override
	public Worm createWorm(double[] location, double direction, double radius, String name) throws ModelException {
		Worm w = new Worm(location, direction, radius, name);
		return w;
	}

	@Override
	public void move(Worm worm, int nbSteps) throws ModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void turn(Worm worm, double angle) throws ModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void jump(Worm worm) throws ModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public double getJumpTime(Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double[] getJumpStep(Worm worm, double t) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getX(Worm worm) throws ModelException {
		return worm.getLocation()[0];
	}

	@Override
	public double getY(Worm worm) throws ModelException {
		return worm.getLocation()[1];
	}

	@Override
	public double getOrientation(Worm worm) throws ModelException {
		return worm.getRadius();
	}

	@Override
	public double getRadius(Worm worm) throws ModelException {
		return worm.getRadius();
	}

	@Override
	public void setRadius(Worm worm, double newRadius) throws ModelException {
		worm.setRadius(newRadius);
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
		worm.setName(newName);
	}

	@Override
	public double getMass(Worm worm) throws ModelException {
		return worm.getMass();
	}

}
