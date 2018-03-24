package worms.internal.gui.game.commands;

import java.util.Random;

import worms.facade.IFacade;
import worms.internal.gui.GUIUtils;
import worms.internal.gui.game.PlayGameScreen;
import worms.internal.gui.messages.MessageType;
import worms.model.Team;
import worms.util.ModelException;

public class AddNewWorm extends InstantaneousCommand {

	private static final double MIN_RADIUS = 0.25;
	private static final double MAX_RADIUS = Math.pow(2, 1.0/3.0) * MIN_RADIUS;
	
	public AddNewWorm(IFacade facade, PlayGameScreen screen) {
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
			int nbWorms = getFacade().getAllWorms(getWorld()).size();
			String name = "Worm " + GUIUtils.numberToName(nbWorms++);
			// ensures minimum radius and team size conditions are always fulfilled
			double radius = MIN_RADIUS + (MAX_RADIUS - MIN_RADIUS) * random.nextDouble();

			double[] p = GUIUtils.findFreeAdjacentSpot(getFacade(), getWorld(), radius, random);

			double direction = random.nextDouble() * 2 * Math.PI;
			Team team = getScreen().getLastCreatedTeam();
			getFacade().createWorm(getWorld(), p, direction, radius, name, team);
		} catch (ModelException e) {
			e.printStackTrace();
			getScreen().addMessage("Could not create worm", MessageType.ERROR);
		}
	}
}
