package worms.internal.gui.game.commands;

import worms.facade.IFacade;
import worms.internal.gui.GUIConstants;
import worms.internal.gui.game.PlayGameScreen;
import worms.internal.gui.game.sprites.WormSprite;
import worms.internal.gui.messages.MessageType;
import worms.model.Worm;
import worms.util.ModelException;
import worms.util.MustNotImplementException;

public class Move extends Command {

	private double startX;
	private double startY;

	private double finalX;
	private double finalY;

	private boolean isFalling;
	private double fallingStartTime = -1;

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

	protected boolean canFall() {
		try {
			return getFacade().canFall(getWorm());
		} catch (MustNotImplementException e) {
			return false;
		}
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
				fall(dt);
			}
		} else {
			cancelExecution();
		}
	}

	protected boolean isFalling() {
		return isFalling;
	}

	protected void ensureFalling() {
		if (fallingStartTime == -1) {
			fallingStartTime = getElapsedTime();
		}
		isFalling = true;
	}

	protected void fall(double dt) {
		if (!isFalling) {
			startFalling();
		} else {
			updateFalling();
		}
	}

	protected void updateFalling() {
		WormSprite sprite = getScreen().getWormSprite(worm);
		if (sprite != null) {
			double duration = getScreen().screenToWorldDistance(Math.abs(finalY - startY)) / GUIConstants.FALL_VELOCITY;
			double timeElapsedFalling = getElapsedTime() - fallingStartTime;
			if (timeElapsedFalling <= duration) {
				double t = timeElapsedFalling / duration;
				t = t * t;
				double x = (1.0 - t) * startX + t * finalX;
				double y = (1.0 - t) * startY + t * finalY;
				sprite.setCenterLocation(x, y);
			} else {
				sprite.setCenterLocation(finalX, finalY);
				completeExecution();
			}
		} else {
			cancelExecution();
		}
	}

	protected void startFalling() {
		this.startX = getScreen().getScreenX(getObjectX());
		this.startY = getScreen().getScreenY(getObjectY());

		if (canFall()) {
			ensureFalling();
			getFacade().fall(getWorm());
			if (isObjectStillActive()) {
				this.finalX = getScreen().getScreenX(getObjectX());
				this.finalY = getScreen().getScreenY(getObjectY());
			} else {
				this.finalX = startX;
				try {
					this.finalY = getScreen().getScreenY(getObjectY());
				} catch (ModelException e) {
					this.finalY = getScreen().getScreenY(0);
				}
			}
		} else {
			completeExecution();
		}
		WormSprite sprite = getScreen().getWormSprite(worm);
		if (sprite != null) {
			sprite.setCenterLocation(startX, startY);
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
	protected void afterExecutionCancelled(Throwable e) {
		WormSprite sprite = getScreen().getWormSprite(getWorm());
		if (sprite != null) {
			sprite.setIsMoving(false);
		}
		getScreen().addMessage("This worm cannot move like that :(" + (e != null ? "\n" + e.getMessage() : ""),
				MessageType.ERROR);
	}

	@Override
	protected void doStartExecution() {
		try {
			double[] xy = getFacade().getLocation(getWorm());
			this.startX = getScreen().getScreenX(xy[0]);
			this.startY = getScreen().getScreenY(xy[1]);
			getFacade().move(getWorm());
			xy = getFacade().getLocation(getWorm());
			this.finalX = getScreen().getScreenX(xy[0]);
			this.finalY = getScreen().getScreenY(xy[1]);
		} catch (ModelException e) {
			cancelExecution(e);
		}
	}

	protected boolean isObjectStillActive() {
		return !getFacade().isTerminated(getWorm());
	}

	protected double getObjectX() {
		return getFacade().getLocation(getWorm())[0];
	}

	protected double getObjectY() {
		return getFacade().getLocation(getWorm())[1];
	}
}