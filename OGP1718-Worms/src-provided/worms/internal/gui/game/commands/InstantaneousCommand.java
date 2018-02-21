package worms.internal.gui.game.commands;

import worms.facade.IFacade;
import worms.internal.gui.game.PlayGameScreen;

public abstract class InstantaneousCommand extends Command {
	protected InstantaneousCommand(IFacade facade, PlayGameScreen screen) {
		super(facade, screen);
	}
	
	@Override
	protected void afterExecutionStarted() {
		completeExecution();
		getScreen().update();
	}

	@Override
	protected final void doUpdate(double dt) {
	}
}