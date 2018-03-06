package worms.facade;

import worms.exceptions.InvalidLocationException;
import worms.exceptions.InvalidRadiusException;
import worms.exceptions.InvalidWormNameException;
import worms.model.Worm;
import worms.util.ModelException;

public class Facade implements IFacade {

	// Default constructor for FACADE class
	public Facade() {
	}

	@Override
	public Worm createWorm(double[] location, double direction, double radius, String name) throws ModelException {
		Worm w = null;
		
		try {
			w = new Worm(location, direction, radius, name);
		} catch (InvalidLocationException e) {
			throw new ModelException(e);
		} catch (InvalidWormNameException e) {
			throw new ModelException(e);
		} catch (InvalidRadiusException e) {
			throw new ModelException(e);
		}
		
		return w;
	}

	@Override
	public void move(Worm worm, int nbSteps) throws ModelException {
		try {
			worm.Move(nbSteps);
		} catch (InvalidLocationException e) {
			throw new ModelException(e);
		}
	}

	@Override
	public void turn(Worm worm, double angle) throws ModelException {
		worm.Turn(angle);

	}

	@Override
	public void jump(Worm worm) throws ModelException {
		// TODO Auto-generated method stub
		try {
			
		} catch (InvalidLocationException e) {
			throw new ModelException(e);
		}
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
		try {
			worm.setRadius(newRadius);
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
			worm.setName(newName);
		} catch (InvalidLocationException e) {
			throw new ModelException(e);
		}
	}

	@Override
	public double getMass(Worm worm) throws ModelException {
		return worm.getMass();
	}

}
