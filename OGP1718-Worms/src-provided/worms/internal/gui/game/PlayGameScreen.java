package worms.internal.gui.game;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import worms.facade.IFacade;
import worms.internal.gui.GUIConstants;
import worms.internal.gui.GUIUtils;
import worms.internal.gui.GameState;
import worms.internal.gui.InputMode;
import worms.internal.gui.Screen;
import worms.internal.gui.WormsGUI;
import worms.internal.gui.game.modes.DefaultInputMode;
import worms.internal.gui.game.modes.EnteringNameMode;
import worms.internal.gui.game.sprites.WormSprite;
import worms.model.Worm;

public class PlayGameScreen extends Screen {

	final PlayGameScreenPainter painter;
	private final GameState gameState;

	private final Set<Sprite<?>> sprites = new HashSet<Sprite<?>>();
	private final DefaultActionHandler userActionHandler;
	private final IActionHandler programActionHandler;

	public PlayGameScreen(WormsGUI gui, GameState state) {
		super(gui);
		this.gameState = state;
		this.painter = createPainter();
		this.userActionHandler = createUserActionHandler();
		this.programActionHandler = createProgramActionHandler();
	}

	protected DefaultActionHandler createUserActionHandler() {
		return new DefaultActionHandler(this, true);
	}

	protected IActionHandler createProgramActionHandler() {
		return new DefaultActionHandler(this, false);
	}

	@Override
	protected InputMode<PlayGameScreen> createDefaultInputMode() {
		return new DefaultInputMode(this, null);
	}

	@Override
	public void screenStarted() {
		runGameLoop();
		userActionHandler.startGame();
	}

	final AtomicLong lastUpdateTimestamp = new AtomicLong();

	final TimerTask gameLoop = new TimerTask() {

		@Override
		public void run() {
			long now = System.currentTimeMillis();
			long delta = now - lastUpdateTimestamp.getAndSet(now);
			double dt = delta / 1000.0 * GUIConstants.TIME_SCALE;
			gameState.evolve(dt);
			repaint();
		}
	};

	private void runGameLoop() {
		Timer timer = new Timer();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				gameLoop.cancel();
				e.printStackTrace();
				getGUI().showError(
						e.getClass().getName() + ": " + e.getMessage());
			}
		});
		lastUpdateTimestamp.set(System.currentTimeMillis());
		timer.scheduleAtFixedRate(gameLoop, 0, 1000 / GUIConstants.FRAMERATE);
	}

	public synchronized void update() {
		addNewSprites();
		for (Sprite<?> sprite : sprites) {
			sprite.update();
		}
	}
	
	protected void addNewSprites() {
		addNewWormSprites();
	}

	private void addNewWormSprites() {
		Collection<Worm> worms = getGameState().getWorms();
		if (worms != null) {
			for (Worm worm : worms) {
				WormSprite sprite = getWormSprite(worm);
				if (sprite == null) {
					createWormSprite(worm);
				}
			}
		}
	}

	private void createWormSprite(Worm worm) {
		WormSprite sprite = new WormSprite(this, worm);
		addSprite(sprite);
	}

	public GameState getGameState() {
		return gameState;
	}

	public IFacade getFacade() {
		return getGameState().getFacade();
	}

	protected PlayGameScreenPainter createPainter() {
		return new PlayGameScreenPainter(this);
	}

	public <T extends Sprite<?>> Set<T> getSpritesOfType(Class<T> type) {
		Set<T> result = new HashSet<T>();
		for (Sprite<?> sprite : sprites) {
			if (type.isInstance(sprite)) {
				result.add(type.cast(sprite));
			}
		}
		return result;
	}

	public <ObjectType, SpriteType extends Sprite<ObjectType>> SpriteType getSpriteOfTypeFor(
			Class<SpriteType> type, ObjectType object) {
		if (object == null) {
			return null;
		}
		for (SpriteType sprite : getSpritesOfType(type)) {
			if (object.equals(sprite.getObject())) {
				return sprite;
			}
		}
		return null;
	}

	public WormSprite getWormSprite(Worm worm) {
		return getSpriteOfTypeFor(WormSprite.class, worm);
	}

	public void move() {
		Worm worm = getSelectedWorm();

		if (worm != null) {
			userActionHandler.move(worm);
		}
	}

	public void jump() {
		Worm worm = getSelectedWorm();
		if (worm != null) {
			userActionHandler.jump(worm);
		}

	}

	public void turn(double angle) {
		Worm worm = getSelectedWorm();
		angle = GUIUtils.restrictAngle(angle, -Math.PI);

		if (worm != null) {
			userActionHandler.turn(worm, angle);
		}
	}

	public void changeName(String newName) {
		Worm worm = getSelectedWorm();

		if (worm != null) {
			userActionHandler.changeName(worm, newName);
		}
	}

	public synchronized Worm getSelectedWorm() {
		return getGameState().getSelectedWorm();
	}

	@Override
	protected void paintScreen(Graphics2D g) {
		painter.paint(g);
	}

	public static PlayGameScreen create(WormsGUI gui, GameState gameState,
			boolean debugMode) {
		if (!debugMode) {
			return new PlayGameScreen(gui, gameState);
		} else {
			return new PlayGameScreen(gui, gameState) {
				@Override
				protected PlayGameScreenPainter createPainter() {
					return new PlayGameScreenDebugPainter(this);
				}
			};
		}
	}

	public void addSprite(Sprite<?> sprite) {
		sprites.add(sprite);
	}

	public void removeSprite(Sprite<?> sprite) {
		sprites.remove(sprite);
	}

	/**
	 * Scale of the displayed world (in worm-meter per pixel)
	 */
	private double getDisplayScale() {
		return GUIConstants.DISPLAY_SCALE;
	}

	/**
	 * Distance in the world (worm-meter) to distance on the screen (pixels)
	 */
	public double worldToScreenDistance(double ds) {
		return ds / getDisplayScale();
	}

	/**
	 * Distance on the screen (pixels) to distance in the world (worm-meter)
	 */
	public double screenToWorldDistance(double ds) {
		return ds * getDisplayScale();
	}

	/**
	 * World x coordinate to screen x coordinate
	 */
	public double getScreenX(double x) {
		return getScreenWidth()/2.0 + worldToScreenDistance(x);
	}

	/**
	 * Screen x coordinate to world x coordinate
	 */
	public double getLogicalX(double screenX) {
		return screenToWorldDistance(screenX - getScreenWidth()/2.0);
	}

	/**
	 * World y coordinate to screen y coordinate
	 */
	public double getScreenY(double y) {
		return getScreenHeight()/2.0 - worldToScreenDistance(y);
	}

	/**
	 * Screen y coordinate to world y coordinate
	 */
	public double getLogicalY(double screenY) {
		return screenToWorldDistance(getScreenHeight()/2.0 - screenY);
	}

	public void paintTextEntry(Graphics2D g, String message, String enteredName) {
		painter.paintTextEntry(g, message, enteredName);
	}

	public void drawTurnAngleIndicator(Graphics2D g, WormSprite wormSprite,
			double currentAngle) {
		painter.drawTurnAngleIndicator(g, wormSprite, currentAngle);
	}

	public <T, S extends Sprite<T>> void removeSpriteFor(Class<S> type, T object) {
		S sprite = getSpriteOfTypeFor(type, object);
		sprites.remove(sprite);
	}

	@SuppressWarnings("unchecked")
	@Override
	public InputMode<PlayGameScreen> getCurrentInputMode() {
		return (InputMode<PlayGameScreen>) super.getCurrentInputMode();
	}

	public void gameStarted() {
		switchInputMode(new DefaultInputMode(this, getCurrentInputMode()));
	}

	public void renameWorm() {
		switchInputMode(new EnteringNameMode("Enter new name for worm: ", this,
				getCurrentInputMode(), new EnteringNameMode.Callback() {
					@Override
					public void onNameEntered(String newName) {
						changeName(newName);
					}
				}));
	}

	public void showInstructions(Graphics2D g, String string) {
		painter.paintInstructions(g, string);
	}

	public WormSprite getSelectedWormSprite() {
		return getWormSprite(getSelectedWorm());
	}

	public void selectNextWorm() {
		getGameState().selectNextWorm();
	}

	public IActionHandler getProgramActionHandler() {
		return programActionHandler;
	}

	public void selectWorm(Worm worm) {
		while (getSelectedWorm() != worm) {
			selectNextWorm();
		}
	}

	public void resizeWorm(int sign) {
		Worm worm = getSelectedWorm();
		
		if (worm != null) {
			userActionHandler.resizeWorm(worm, sign);
		}
	}

}
