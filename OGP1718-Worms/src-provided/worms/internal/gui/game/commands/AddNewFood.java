package worms.internal.gui.game.commands;

import java.util.Random;

import worms.facade.IFacade;
import worms.internal.gui.GUIUtils;
import worms.internal.gui.game.PlayGameScreen;
import worms.internal.gui.messages.MessageType;
import worms.util.ModelException;

public class AddNewFood extends InstantaneousCommand {

	private static final double FOOD_RADIUS = 0.20;

	public AddNewFood(IFacade facade, PlayGameScreen screen) {
		super(facade, screen);
	}

	@Override
	protected boolean canStart() {
		return true;
	}

	@Override
	protected void doStartExecution() {
		try {
			Random random = getScreen().getGameState().getRandom();

			double[] p = GUIUtils.findFreeAdjacentSpot(getFacade(), getWorld(), FOOD_RADIUS, random);

			getFacade().createFood(getWorld(), p);
		} catch (ModelException e) {
			e.printStackTrace();
			getScreen().addMessage("Error while adding new food", MessageType.ERROR);
		}
	}

}
