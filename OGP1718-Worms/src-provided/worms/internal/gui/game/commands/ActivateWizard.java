package worms.internal.gui.game.commands;

import worms.facade.IFacade;
import worms.internal.gui.game.PlayGameScreen;
import worms.internal.gui.messages.MessageType;
import worms.util.ModelException;

public class ActivateWizard extends InstantaneousCommand {

	public ActivateWizard(IFacade facade,
			PlayGameScreen screen) {
		super(facade, screen);
	}

	@Override
	protected boolean canStart() {
		return true;
	}

	@Override
	protected void doStartExecution() {
		try {
			getFacade().castSpell(getWorld());
			getScreen().addMessage("The wizard has done wizardry things!", MessageType.INFO);
		} catch (ModelException e) {
			getScreen().addMessage("The wizard could not cast its spell :(", MessageType.ERROR);
		}
	}
}