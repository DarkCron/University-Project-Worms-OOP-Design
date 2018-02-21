package worms.internal.gui.game.commands;

import worms.facade.IFacade;
import worms.internal.gui.GUIUtils;
import worms.internal.gui.game.PlayGameScreen;
import worms.internal.gui.messages.MessageType;
import worms.model.Worm;

public class Turn extends InstantaneousCommand {
	private final Worm worm;
	private final double angle;

	public Turn(IFacade facade, Worm worm, double angle, PlayGameScreen screen) {
		super(facade, screen);
		this.worm = worm;
		this.angle = angle;
	}

	@Override
	protected boolean canStart() {
		return worm != null;
	}
	
	@Override
	protected void afterExecutionCancelled() {
		getScreen().addMessage("This worm cannot perform that turn :(",
				MessageType.ERROR);
	}
	
	@Override
	protected void doStartExecution() {
		double direction = getFacade().getOrientation(worm);
		double angleToTurn = GUIUtils.restrictDirection(direction + angle) - direction;
		getFacade().turn(worm, angleToTurn);
	}
}