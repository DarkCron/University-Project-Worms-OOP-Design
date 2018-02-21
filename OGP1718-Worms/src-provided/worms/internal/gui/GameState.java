package worms.internal.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import worms.facade.IFacade;
import worms.internal.gui.game.commands.Command;
import worms.model.Worm;

public class GameState {

	private final Random random;
	private final IFacade facade;
	private final Collection<Worm> worms = new ArrayList<Worm>();

	private final BlockingQueue<Double> timeDelta = new LinkedBlockingQueue<Double>(
			1);

	private final int width;
	private final int height;

	public GameState(IFacade facade, long randomSeed, int width, int height) {
		this.random = new Random(randomSeed);
		this.facade = facade;
		this.width = width;
		this.height = height;
	}

	private List<String> wormNames = Arrays.asList("Shari", "Shannon",
			"Willard", "Jodi", "Santos", "Ross", "Cora", "Jacob", "Homer",
			"Kara");
	private int nameIndex = 0;
	private Iterator<Worm> selection;
	private Worm selectedWorm;
	
	
	public IFacade getFacade() {
		return facade;
	}

	private void createRandomWorms() {
		double worldWidth = width * GUIConstants.DISPLAY_SCALE;
		double worldHeight = height * GUIConstants.DISPLAY_SCALE;

		for (int i = 0; i < wormNames.size(); i++) {
			String name = wormNames.get(nameIndex++);
			double radius = 0.25 + random.nextDouble() / 4;

			double x = -worldWidth / 2 + radius + random.nextDouble()
					* (worldWidth - 2 * radius);
			double y = -worldHeight / 2 + radius + random.nextDouble()
					* (worldHeight - 2 * radius);
			double direction = random.nextDouble() * 2 * Math.PI;
			Worm worm = facade.createWorm(new double[] {x, y}, direction, radius, name);
			if (worm != null) {
				worms.add(worm);
			} else {
				throw new NullPointerException("Created worm must not be null");
			}
		}
	}
	
	public void evolve(double dt) {
		timeDelta.clear(); // nobody was waiting for the previous tick, so
		// clear it
		timeDelta.offer(dt);
	}
	
	public boolean executeImmediately(Command cmd) {
		cmd.startExecution();
		while (!cmd.isTerminated()) {
			try {
				Double dt = timeDelta.poll(1000 / GUIConstants.FRAMERATE,
						TimeUnit.MILLISECONDS); // blocks, but allows repainting
												// if necessary
				if (dt != null) {
					cmd.update(dt);
				}
				cmd.getScreen().repaint(); // repaint while executing command
											// (which might block GUI thread)
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return cmd.isExecutionCompleted();
	}

	public void startGame() {
		createRandomWorms();
		selectNextWorm();
	}

	public Worm getSelectedWorm() {
		return selectedWorm;
	}

	public void selectNextWorm() {
		if (selection == null || !selection.hasNext()) {
			selection = worms.iterator();
		}
		if (selection.hasNext()) {
			selectWorm(selection.next());
		} else {
			selectWorm(null);
		}
	}

	public void selectWorm(Worm worm) {
		selectedWorm = worm;
	}
	
	public Collection<Worm> getWorms() {
		return Collections.unmodifiableCollection(worms);
	}

	public Random getRandom() {
		return random;
	}

}
