package worms.internal.gui.game.commands;

import java.util.Collection;

import worms.facade.IFacade;
import worms.internal.gui.game.PlayGameScreen;
import worms.internal.gui.messages.MessageType;
import worms.model.Worm;
import worms.util.ModelException;

public class StartGame extends InstantaneousCommand {

	public StartGame(IFacade facade, PlayGameScreen screen) {
		super(facade, screen);
	}

	@Override
	protected boolean canStart() {
		Collection<Worm> worms = getFacade().getAllWorms(getWorld());
		return worms != null && !worms.isEmpty();
	}

	@Override
	protected void afterExecutionCancelled(Throwable e) {
		if (e != null) {
			getScreen().addMessage("Cannot start the game: " + e.getMessage(), MessageType.ERROR);
		} else {
			getScreen().addMessage("Cannot start the game without worms", MessageType.ERROR);
		}
	}

	@Override
	protected void doStartExecution() {
		try {
			getScreen().gameStarted();
			getFacade().startGame(getWorld());
			if (!getFacade().hasActiveGame(getWorld())) {
				getScreen().gameFinished();
			}
		} catch (ModelException e) {
			cancelExecution(e);
		}
	}

}
