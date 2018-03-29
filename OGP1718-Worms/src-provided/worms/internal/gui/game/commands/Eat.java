package worms.internal.gui.game.commands;

import worms.facade.IFacade;
import worms.internal.gui.game.PlayGameScreen;
import worms.internal.gui.messages.MessageType;
import worms.model.Food;
import worms.model.Worm;
import worms.util.ModelException;

public class Eat extends InstantaneousCommand {
	private final Worm worm;

	public Eat(IFacade facade, Worm worm,
			PlayGameScreen screen) {
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
			long nbFoodBefore = getFacade().getAllItems(getWorld()).stream().filter(Food.class::isInstance).count();
			getFacade().eat(worm);
			long nbFoodAfter = getFacade().getAllItems(getWorld()).stream().filter(Food.class::isInstance).count();
			if (nbFoodAfter < nbFoodBefore) {
				getScreen().addMessage("Yummie!", MessageType.INFO);
			}
		} catch (ModelException e) {
			getScreen().addMessage("This worm cannot eat.", MessageType.ERROR);
		}
	}
}