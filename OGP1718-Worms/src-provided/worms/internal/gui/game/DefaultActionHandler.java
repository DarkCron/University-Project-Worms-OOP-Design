package worms.internal.gui.game;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import worms.facade.IFacade;
import worms.internal.gui.GameState;
import worms.internal.gui.game.commands.AddNewFood;
import worms.internal.gui.game.commands.AddNewTeam;
import worms.internal.gui.game.commands.AddNewWorm;
import worms.internal.gui.game.commands.Command;
import worms.internal.gui.game.commands.Jump;
import worms.internal.gui.game.commands.Move;
import worms.internal.gui.game.commands.Rename;
import worms.internal.gui.game.commands.SelectNextWorm;
import worms.internal.gui.game.commands.StartGame;
import worms.internal.gui.game.commands.Turn;
import worms.internal.gui.messages.MessageType;
import worms.model.Worm;

class DefaultActionHandler implements IActionHandler {

	private final PlayGameScreen screen;
	private final boolean userInitiated;

	private final ExecutorService executor = Executors
			.newSingleThreadExecutor();

	public DefaultActionHandler(PlayGameScreen screen, boolean userInitiated) {
		this.screen = screen;
		this.userInitiated = userInitiated;
	}

	public PlayGameScreen getScreen() {
		return screen;
	}

	protected IFacade getFacade() {
		return screen.getFacade();
	}

	protected GameState getGameState() {
		return screen.getGameState();
	}

	@Override
	public boolean turn(Worm worm, double angle) {
		return executeCommand(new Turn(getFacade(), worm, angle, getScreen()));
	}

	@Override
	public boolean move(Worm worm) {
		return executeCommand(new Move(getFacade(), worm, getScreen()));
	}

	@Override
	public boolean jump(Worm worm) {
		return executeCommand(new Jump(getFacade(), worm, getScreen()));
	}

	private boolean executeCommand(final Command cmd) {
		if (userInitiated) {
			executor.execute(new Runnable() {

				@Override
				public void run() {
					getGameState().executeImmediately(cmd);
				}
			});
			return true;
		} else {
			boolean result = getGameState().executeImmediately(cmd);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			return result;
		}
	}

	@Override
	public void print(String message) {
		getScreen().addMessage(message, MessageType.INFO);
	}

	public void changeName(Worm worm, String newName) {
		executeCommand(new Rename(getFacade(), worm, newName, getScreen()));
	}

	public void selectNextWorm() {
		executeCommand(new SelectNextWorm(getFacade(), getScreen()));
	}

	public void startGame() {
		executeCommand(new StartGame(getFacade(), getScreen()));
	}

	public void addNewWorm() {
		executeCommand(new AddNewWorm(getFacade(), getScreen()));
	}

	public void addEmptyTeam(String name) {
		executeCommand(new AddNewTeam(getFacade(), name, getScreen()));
	}

	public void addNewFood() {
		executeCommand(new AddNewFood(getFacade(), getScreen()));
	}

}
