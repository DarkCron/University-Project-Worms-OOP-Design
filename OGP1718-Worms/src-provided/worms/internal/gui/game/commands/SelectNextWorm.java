package worms.internal.gui.game.commands;

import worms.facade.IFacade;
import worms.internal.gui.game.PlayGameScreen;
import worms.internal.gui.messages.MessageType;
import worms.util.ModelException;

public class SelectNextWorm extends InstantaneousCommand {

	public SelectNextWorm(IFacade facade, PlayGameScreen screen) {
		super(facade, screen);
	}

	@Override
	protected boolean canStart() {
		return true;
	}
	
	@Override
	protected void afterExecutionCancelled(Throwable e) {
		getScreen().addMessage("Cannot activate next worm :(" + (e != null ? "\n" + e.getMessage() : ""), MessageType.ERROR);

	}

	@Override
	protected void doStartExecution() {
		try {
			getFacade().activateNextWorm(getWorld());
		} catch (ModelException e) {
			cancelExecution(e);
		}
	}

}
