package worms.model.interfaces;

import worms.exceptions.InvalidLocationException;


public interface IJumpable extends IAction{
	public void jump(double timeStep);
	public double getJumpTime(double deltaT);
	public double[] jumpStep(double deltaTime) throws InvalidLocationException,IllegalArgumentException,RuntimeException;
}
