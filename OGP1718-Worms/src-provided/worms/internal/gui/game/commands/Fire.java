package worms.internal.gui.game.commands;

import worms.facade.IFacade;
import worms.internal.gui.GUIConstants;
import worms.internal.gui.game.PlayGameScreen;
import worms.internal.gui.game.sprites.ProjectileSprite;
import worms.internal.gui.messages.MessageType;
import worms.model.Projectile;
import worms.model.Worm;
import worms.util.ModelException;

public class Fire extends Command {
	private final Worm worm;

	private Projectile projectile;
	private double totalDuration;
	private boolean hasJumped;

	public Fire(IFacade facade, Worm worm, PlayGameScreen screen) {
		super(facade, screen);
		this.worm = worm;
	}

	@Override
	protected boolean canStart() {
		return worm != null;
	}

	@Override
	protected void doStartExecution() {
		try {
			projectile = getFacade().fire(worm);
			if (projectile != null) {
				totalDuration = getFacade().getJumpTime(projectile, GUIConstants.JUMP_TIME_STEP);
				ProjectileSprite sprite = new ProjectileSprite(getScreen(), projectile);
				sprite.update();
				getScreen().addSprite(sprite);
			} else {
				cancelExecution();
			}
		} catch (ModelException e) {
			e.printStackTrace();
			cancelExecution();
		}
	}
	
	@Override
	protected void afterExecutionCompleted() {
		super.afterExecutionCompleted();
		ProjectileSprite sprite = getScreen().getSpriteOfTypeFor(ProjectileSprite.class, projectile);
		if (sprite != null) {
			sprite.setDoneMoving();
		}
	}

	@Override
	protected void afterExecutionCancelled(Throwable e) {
		getScreen().addMessage("This worm cannot shoot :(" + (e != null ? "\n" + e.getMessage() : ""),
				MessageType.ERROR);
	}

	@Override
	protected void doUpdate(double dt) {
		try {
			if (getElapsedTime() >= totalDuration) {
				if (!hasJumped) {
					hasJumped = true;
					getFacade().jump(projectile, GUIConstants.JUMP_TIME_STEP);
					completeExecution();
				}
			} else {
				ProjectileSprite sprite = getScreen().getSpriteOfTypeFor(ProjectileSprite.class, projectile);

				double[] xy = getFacade().getJumpStep(projectile, getElapsedTime());

				sprite.setCenterLocation(getScreen().getScreenX(xy[0]), getScreen().getScreenY(xy[1]));
			}
		} catch (ModelException e) {
			e.printStackTrace();
			cancelExecution();
		}
	}
}