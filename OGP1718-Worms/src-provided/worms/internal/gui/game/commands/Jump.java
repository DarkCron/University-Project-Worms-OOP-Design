package worms.internal.gui.game.commands;

import worms.facade.IFacade;
import worms.internal.gui.game.PlayGameScreen;
import worms.internal.gui.game.sprites.WormSprite;
import worms.internal.gui.messages.MessageType;
import worms.model.Worm;
import worms.util.ModelException;

public class Jump extends Command {
	private boolean hasJumped;
	private final Worm worm;
	private double jumpDuration;

	public Jump(IFacade facade, Worm worm, PlayGameScreen screen) {
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

	@Override
	protected void doStartExecution() {
		try {
			this.jumpDuration = getFacade().getJumpTime(worm);
		} catch (ModelException e) {
			cancelExecution();
		}
	}

	@Override
	protected void afterExecutionCancelled() {
		WormSprite sprite = getScreen().getWormSprite(getWorm());
		if (sprite != null) {
			sprite.setIsJumping(false);
		}
		getScreen().addMessage("This worm cannot jump :(", MessageType.ERROR);
	}
	
	@Override
	protected void afterExecutionCompleted() {
		WormSprite sprite = getScreen().getWormSprite(getWorm());
		if (sprite != null) {
			sprite.setIsJumping(false);
		}
	}

	@Override
	protected void doUpdate(double dt) {
		WormSprite sprite = getScreen().getWormSprite(getWorm());
		if (sprite != null) {
			try {
				sprite.setIsJumping(true);
				if (getElapsedTime() >= jumpDuration) {
					if (!hasJumped) {
						hasJumped = true;
						getFacade()
								.jump(getWorm());
							double x = getFacade().getX(getWorm());
							double y = getFacade().getY(getWorm());
							sprite.setCenterLocation(getScreen().getScreenX(x),
									getScreen().getScreenY(y));
						completeExecution();
					}
				} else {
					double[] xy = getFacade().getJumpStep(getWorm(),
							getElapsedTime());
					sprite.setCenterLocation(getScreen().getScreenX(xy[0]),
							getScreen().getScreenY(xy[1]));
				}
			} catch (ModelException e) {
				e.printStackTrace();
				cancelExecution();
			}
		} else {
			cancelExecution();
		}
	}

}