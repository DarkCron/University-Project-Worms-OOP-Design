package worms.internal.gui.game.sprites;

import java.awt.Image;
import java.awt.image.BufferedImage;

import worms.internal.gui.game.ImageSprite;
import worms.internal.gui.game.PlayGameScreen;
import worms.model.Food;

public class FoodSprite extends ImageSprite<Food> {

	private static final double MAX_SCALE = 100;
	private static final double MIN_SCALE = 0.05;

	private final Food food;
	
	private double radius;
	
	private boolean poisonous = false;
	
	// original image, at original scale
	private final BufferedImage originalPoisonousImage;

	// only created when scale != 1.0
	private BufferedImage scaledPoisonousImage;

	public FoodSprite(PlayGameScreen screen, Food food) {
		super(screen, "images/burger.png");
		this.food = food;
		// constraint: must have same dimensions as original image
		// constraint: does not respect hflipping 
		this.originalPoisonousImage = loadImage("images/burger-poisonous.png");
		this.scaledPoisonousImage = this.originalPoisonousImage;
		update();
	}

	/**
	 * @param radius
	 *            (in worm-meter)
	 */
	public synchronized void setRadius(double radius) {
		this.radius = radius;
		
		/*
		 * Height of the image (when drawn at native size) in worm-meters, given
		 * the scale at which the world is drawn to screen
		 */
		double imageHeightInMeters = getScreen().screenToWorldDistance(
				getImageHeight());

		/*
		 * scale factor to nicely fit the image in a circle with diameter equal
		 * to the image height (value determined experimentally)
		 */
		double fitFactor = 1.2;

		double scaleFactor = fitFactor * 2 * radius / imageHeightInMeters;

		scaleFactor = Math.max(MIN_SCALE, Math.min(scaleFactor, MAX_SCALE));

		setScale(scaleFactor);
	}

	@Override
	public void setScale(double newScale) {
		double oldScale = getScale();
		
		super.setScale(newScale);
		
		if (newScale == oldScale) {
			return;
		}

		if (newScale != 1.0) {
			this.scaledPoisonousImage = toBufferedImage(originalPoisonousImage.getScaledInstance(
					(int) (newScale * originalPoisonousImage.getWidth()),
					(int) (newScale * originalPoisonousImage.getHeight()),
					Image.SCALE_SMOOTH));
		} else {
			this.scaledPoisonousImage = originalPoisonousImage;
		}
	}

	
	@Override
	protected Image getImageToDraw() {
		if (isPoisonous()) {
			return scaledPoisonousImage;
		} else {
			return super.getImageToDraw();
		}
	}

	public synchronized double getRadius() {
		return radius;
	}
	
	public synchronized boolean isPoisonous() {
		return poisonous;
	}
	
	public void setPoisonous(boolean poisonous) {
		this.poisonous = poisonous;
	}
	
	@Override
	public synchronized void update() {
		setPoisonous(getFacade().isPoisonous(getFood()));
		setRadius(getFacade().getRadius(getFood()));
		double[] xy = getFacade().getLocation(getFood());
		setCenterLocation(getScreen().getScreenX(xy[0]), getScreen().getScreenY(xy[1]));
	}

	@Override
	public Food getObject() {
		return getFood();
	}

	public Food getFood() {
		return food;
	}
	
	@Override
	public boolean isObjectAlive() {
		return !getFacade().isTerminated(food);
	}


}
