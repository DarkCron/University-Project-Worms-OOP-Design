package worms.internal.gui.game.sprites;

import java.math.BigInteger;

import worms.internal.gui.GUIConstants;
import worms.internal.gui.GUIUtils;
import worms.internal.gui.game.ImageSprite;
import worms.internal.gui.game.PlayGameScreen;
import worms.model.Team;
import worms.model.Worm;
import worms.util.ModelException;
import worms.util.MustNotImplementException;

public class WormSprite extends ImageSprite<Worm> {

	private static final double MAX_SCALE = 100;
	private static final double MIN_SCALE = 0.05;
	private final Worm worm;
	
	private boolean isJumping;
	private boolean isMoving;
	private double[][] xys;
	private double orientation;
	private String name;
	private String teamName;
	private boolean atImpassableTerrain;
	private long actionPoints;
	private long maxActionPoints;
	private BigInteger hitPoints;
	private double actualX;
	private double actualY;
	private double radius;

	public WormSprite(PlayGameScreen screen, Worm worm) {
		super(screen, "images/worm.png");
		this.worm = worm;
		update();
	}

	@Override
	public Worm getObject() {
		return getWorm();
	}

	public Worm getWorm() {
		return worm;
	}

	private void setDirection(double newDirection) {
		double direction = GUIUtils.restrictDirection(newDirection);
		this.orientation = direction;

		if (Math.PI / 2 > direction || 3 * Math.PI / 2 < direction) {
			setHflipped(true);
		} else {
			setHflipped(false);
		}
	}

	/**
	 * @param radius
	 *            (in worm-meter)
	 */
	public synchronized void setRadius(double radius) {
		this.radius = radius;
		/*
		 * Height of the image (when drawn at native size) in worm-meters, given the
		 * scale at which the world is drawn to screen
		 */
		double imageHeightInMeters = getScreen().screenToWorldDistance(getImageHeight());

		/*
		 * scale factor to nicely fit the image in a circle with diameter equal to the
		 * image height (value determined experimentally)
		 */
		double fitFactor = 0.8;

		double scaleFactor = fitFactor * 2 * radius / imageHeightInMeters;

		// limit scaling
		scaleFactor = Math.max(MIN_SCALE, Math.min(scaleFactor, MAX_SCALE));

		setScale(scaleFactor);
	}

	public boolean hitTest(double screenX, double screenY) {
		double radius = getScale() * Math.max(getImageWidth(), getImageHeight()) / 2.0;
		double dx = screenX - getCenterX();
		double dy = screenY - getCenterY();
		return dx * dx + dy * dy <= radius * radius;
	}

	@Override
	public boolean isObjectAlive() {
		return !getFacade().isTerminated(getWorm());
	}

	@Override
	public synchronized void update() {
		double[] xy = getFacade().getLocation(getWorm());
		if (isJumping || isMoving) {
			// don't update the location here, because it may differ from the
			// location in the model (interpolation)
		} else {
			setCenterLocation(
					getScreen().getScreenX(xy[0]),
					getScreen().getScreenY(xy[1]));
		}
		this.actualX = xy[0];
		this.actualY = xy[1];
		setRadius(getFacade().getRadius(getWorm()));
		setDirection(getFacade().getOrientation(getWorm()));
		updateJumpTime();
		setName(getFacade().getName(getWorm()));
		try {
			Team team = getFacade().getTeam(getWorm());
			if (team != null) {
				setTeamName(getFacade().getName(team));
			} else {
				setTeamName("_teamless_");
			}
		} catch (MustNotImplementException e) {
			setTeamName("No Teams");
		}
		this.atImpassableTerrain = !getFacade().isPassable(getScreen().getWorld(), 
				xy, getFacade().getRadius(getWorm()));
		this.actionPoints = getFacade().getNbActionPoints(getWorm());
		this.maxActionPoints = getFacade().getMaxNbActionPoints(getWorm());
		this.hitPoints = getFacade().getNbHitPoints(getWorm());
	}

	public void setIsJumping(boolean isJumping) {
		this.isJumping = isJumping;
	}

	public void setIsMoving(boolean isMoving) {
		this.isMoving = isMoving;
	}

	protected static final double JUMP_MARKER_TIME_DISTANCE = 0.1; // worm-seconds

	private void updateJumpTime() {
		try {
			double time = getFacade().getJumpTime(getWorm(), GUIConstants.JUMP_TIME_STEP);
			if (time > 0) {
				int n = 1 + (int) (time / JUMP_MARKER_TIME_DISTANCE);
				xys = new double[n][];
				for (int i = 1; i <= n; i++) {
					double dt = i * time / n;
					double[] xy = getFacade().getJumpStep(getWorm(), dt);
					xys[i - 1] = xy;
				}
			} else {
				this.xys = null;
			}
		} catch (ModelException e) {
			this.xys = null;
		}
	}

	public synchronized double[][] getJumpSteps() {
		return xys;
	}

	public synchronized double getOrientation() {
		return orientation;
	}

	public synchronized String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public synchronized String getTeamName() {
		return teamName;
	}

	private void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public synchronized boolean isAtImpassableTerrain() {
		return atImpassableTerrain;
	}

	public synchronized long getActionPoints() {
		return actionPoints;
	}

	public synchronized long getMaxActionPoints() {
		return maxActionPoints;
	}

	public synchronized BigInteger getHitPoints() {
		return hitPoints;
	}

	public synchronized double getActualX() {
		return actualX;
	}

	public synchronized double getActualY() {
		return actualY;
	}

	public synchronized double getRadius() {
		return radius;
	}
}
