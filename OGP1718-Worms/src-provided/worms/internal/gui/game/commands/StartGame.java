package worms.internal.gui.game.commands;

import java.util.Collection;

import worms.facade.IFacade;
import worms.internal.gui.game.PlayGameScreen;
import worms.internal.gui.messages.MessageType;
import worms.model.Worm;

public class StartGame extends InstantaneousCommand {

	public StartGame(IFacade facade, PlayGameScreen screen) {
		super(facade, screen);
	}

	@Override
	protected boolean canStart() {
		Collection<Worm> worms = getScreen().getGameState().getWorms();
		return worms != null && !worms.isEmpty();
	}
	
	@Override
	protected void afterExecutionCancelled() {
		getScreen().addMessage("Cannot start the game without worms", MessageType.ERROR);
	}

	@Override
	protected void doStartExecution() {
		getScreen().gameStarted();
	}

}
