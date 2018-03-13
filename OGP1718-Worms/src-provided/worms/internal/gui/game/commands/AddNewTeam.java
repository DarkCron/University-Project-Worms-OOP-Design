package worms.internal.gui.game.commands;

import worms.facade.IFacade;
import worms.internal.gui.game.PlayGameScreen;
import worms.internal.gui.messages.MessageType;
import worms.model.Team;
import worms.util.ModelException;

public class AddNewTeam extends InstantaneousCommand {

	private final String name;

	public AddNewTeam(IFacade facade, String name, PlayGameScreen screen) {
		super(facade, screen);
		this.name = name;
	}

	@Override
	protected boolean canStart() {
		return true;
	}

	@Override
	protected void doStartExecution() {
		try {
			Team team = getFacade().createTeam(getWorld(), name);
			getScreen().addMessage("Team " + name + " created.", MessageType.NORMAL);
			getScreen().setLastCreatedTeam(team);
		} catch (ModelException e) {
			e.printStackTrace();
			getScreen().addMessage(
					"Could not create team " + name + ": " + e.getMessage(),
					MessageType.ERROR);
		}
	}

	@Override
	protected void afterExecutionCompleted() {
	}

}
