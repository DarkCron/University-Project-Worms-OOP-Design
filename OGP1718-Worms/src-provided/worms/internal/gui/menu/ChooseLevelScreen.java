package worms.internal.gui.menu;

import worms.internal.gui.Level;
import worms.internal.gui.WormsGUI;

class ChooseLevelScreen extends AbstractMenuScreen<Level> {

	public ChooseLevelScreen(WormsGUI gui) {
		super(gui);
	}

	@Override
	protected Level[] getChoices() {
		return Level.getAvailableLevels();
	}

	@Override
	protected String getDisplayName(Level level) {
		return level.getName();
	}

	@Override
	protected String getInstructions() {
		return "Choose the level you want to play";
	}

	@Override
	public void screenStarted() {
	}
}
