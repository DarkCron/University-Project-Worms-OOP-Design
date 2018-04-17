package worms;

import worms.facade.Facade;
import worms.internal.gui.GUIOptions;
import worms.internal.gui.WormsGUI;

public class Worms {

	public static void main(String[] args) {
		new WormsGUI(new Facade(), parseOptions(args)).start();
	}

	private static GUIOptions parseOptions(String[] args) {
		GUIOptions options = new GUIOptions();

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if ("-window".equals(arg)) {
				options.disableFullScreen = true;
			} else if ("-seed".equals(arg)) {
				long randomSeed = Long.parseLong(args[++i]);
				options.randomSeed = randomSeed;
			} else if ("-clickselect".equals(arg)) {
				options.enableClickToSelect = true;
			} else if ("-program".equals(arg)) {
				String program = args[++i];
				options.programFile = program;
			}
		}

		return options;
	}
}
