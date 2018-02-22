package worms.facade;

import worms.model.Worm;
import worms.util.ModelException;

public class Facade implements IFacade{

	@Override
	public Worm createWorm(double[] location, double direction, double radius, String name) throws ModelException {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getY(Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getOrientation(Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getRadius(Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRadius(Worm worm, double newRadius) throws ModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getNbActionPoints(Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void decreaseNbActionPoints(Worm worm, long delta) throws ModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getMaxNbActionPoints(Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName(Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rename(Worm worm, String newName) throws ModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getMass(Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		return 0;
	}

}
