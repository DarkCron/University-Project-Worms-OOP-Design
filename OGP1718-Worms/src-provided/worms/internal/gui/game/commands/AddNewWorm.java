package worms.internal.gui.game.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

import worms.facade.IFacade;
import worms.internal.gui.GUIUtils;
import worms.internal.gui.WormsGUI;
import worms.internal.gui.game.PlayGameScreen;
import worms.internal.gui.messages.MessageType;
import worms.model.Program;
import worms.model.Team;
import worms.model.Worm;
import worms.programs.IProgramFactory;
import worms.programs.ParseOutcome;
import worms.programs.ProgramParser;
import worms.util.ModelException;

public class AddNewWorm extends InstantaneousCommand {

	private static final double MIN_RADIUS = 0.25;
	private static final double MAX_RADIUS = Math.pow(2, 1.0 / 3.0) * MIN_RADIUS;
	private boolean withProgram;

	public AddNewWorm(IFacade facade, boolean withProgram, PlayGameScreen screen) {
		super(facade, screen);
		this.withProgram = withProgram;
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
			Worm worm = getFacade().createWorm(getWorld(), p, direction, radius, name, team);

			if (withProgram) {
				loadProgram(worm);
			}

		} catch (ModelException e) {
			e.printStackTrace();
			getScreen().addMessage("Could not create worm", MessageType.ERROR);
		}
	}

	private void loadProgram(Worm worm) {
		String programText = readProgramText();
		if (programText != null) {
			IProgramFactory<?, ?, ?, ? extends Program> factory = getFacade().createProgramFactory();
			ProgramParser<?, ?, ?, ? extends Program> parser = ProgramParser.create(factory);
			ParseOutcome<? extends Program> outcome = parser.parseString(programText);

			if (outcome != null) {
				if (outcome.isSuccess()) {
					Program program = outcome.getSuccessValue();
					getFacade().loadProgramOnWorm(worm, program, getScreen().getProgramActionHandler());
				} else {
					List<String> errors = outcome.getFailValue();
					String msg = "Parsing failed\nwith the following errors:\n";
					for (String error : errors) {
						msg += error + "\n";
					}
					cancelExecution();
					getGUI().showError(msg);
					return;
				}
			}
		} else {
			cancelExecution();
			getGUI().showError("Could not parse program");
		}
	}

	private WormsGUI getGUI() {
		return getScreen().getGUI();
	}

	protected String readProgramText() {
		InputStream stream;
		try {
			stream = GUIUtils.openResource(getGUI().getOptions().programFile);
		} catch (IOException e) {
			e.printStackTrace();
			getGUI().showError(e.getMessage());
			return null;
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		StringBuilder programText = new StringBuilder();
		String line;
		try {
			line = reader.readLine();
			while (line != null) {
				programText.append(line);
				programText.append("\n");
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
			cancelExecution(e);
			return null;
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				// I don't care
			}
		}
		return programText.toString();
	}

}
