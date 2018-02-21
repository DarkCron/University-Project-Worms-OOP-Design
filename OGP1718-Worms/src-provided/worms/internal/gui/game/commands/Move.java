package worms.internal.gui.game.commands;

import worms.facade.IFacade;
import worms.internal.gui.GUIConstants;
import worms.internal.gui.game.PlayGameScreen;
import worms.internal.gui.game.sprites.WormSprite;
import worms.internal.gui.messages.MessageType;
import worms.model.Worm;
import worms.util.ModelException;

public class Move extends Command {

	private double startX;
	private double startY;

	private double finalX;
	private double finalY;

	private final Worm worm;

	public Move(IFacade facade, Worm worm, PlayGameScreen screen) {
		super(facade, screen);
		this.worm = worm;
	}

	public Worm getWorm() {
		return worm;
	}

	@Override
	protected boolean canStart() {
		return getWorm() != null; 
	}

	private double getDuration() {
		return GUIConstants.MOVE_DURATION;
	}

	@Override
	protected void doUpdate(double dt) {
		WormSprite sprite = getScreen().getWormSprite(getWorm());
		if (sprite != null) {
			sprite.setIsMoving(true);
			if (getElapsedTime() < getDuration()) {
				double t = getElapsedTime() / getDuration();
				t = t * t * (3 - 2 * t); // smooth-step interpolation
				double x = (1.0 - t) * startX + t * finalX;
				double y = (1.0 - t) * startY + t * finalY;
				sprite.setCenterLocation(x, y);
			} else {
				completeExecution();
			}
		} else {
			cancelExecution();
		}
	}
	
	@Override
	protected void afterExecutionCompleted() {
		WormSprite sprite = getScreen().getWormSprite(getWorm());
		if (sprite != null) {
			sprite.setIsMoving(false);
		}
	}

	@Override
	protected void afterExecutionCancelled() {
		WormSprite sprite = getScreen().getWormSprite(getWorm());
		if (sprite != null) {
			sprite.setIsMoving(false);
		}
		getScreen().addMessage("This worm cannot move like that :(",
				MessageType.ERROR);
	}

	@Override
	protected void doStartExecution() {
		try {
			this.startX = getScreen().getScreenX(getObjectX());
			this.startY = getScreen().getScreenY(getObjectY());
			getFacade().move(getWorm(), 1);
			this.finalX = getScreen().getScreenX(getObjectX());
			this.finalY = getScreen().getScreenY(getObjectY());
		} catch (ModelException e) {
			e.printStackTrace();
			cancelExecution();
		}
	}

	protected double getObjectX() {
		return getFacade().getX(getWorm());
	}

	protected double getObjectY() {
		return getFacade().getY(getWorm());
	}
}