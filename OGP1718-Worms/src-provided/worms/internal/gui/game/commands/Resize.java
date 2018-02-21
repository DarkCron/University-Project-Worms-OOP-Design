package worms.internal.gui.game.commands;

import worms.facade.IFacade;
import worms.internal.gui.game.PlayGameScreen;
import worms.internal.gui.game.sprites.WormSprite;
import worms.internal.gui.messages.MessageType;
import worms.model.Worm;
import worms.util.ModelException;

public class Resize extends InstantaneousCommand {
	private final Worm worm;
	private final double factor;

	public Resize(IFacade facade, Worm worm, double factor,
			PlayGameScreen screen) {
		super(facade, screen);
		this.worm = worm;
		this.factor = factor;
	}
	
	@Override
	protected boolean canStart() {
		return worm != null;
	}

	@Override
	protected void doStartExecution() {
		try {
			double newRadius = factor * getFacade().getRadius(worm);
			getFacade().setRadius(worm, newRadius);
		} catch (ModelException e) {
			// an invalid radius
			getScreen().addMessage(
					"Cannot " + (factor > 1.0 ? "grow" : "shrink")
							+ " that worm anymore :(", MessageType.ERROR);
		}
		WormSprite sprite = getScreen().getWormSprite(worm);
		sprite.update();
	}
}