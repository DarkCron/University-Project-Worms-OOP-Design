package worms.internal.gui.game;

import java.awt.Color;
import java.awt.Shape;

import worms.internal.gui.GUIUtils;
import worms.internal.gui.game.sprites.WormSprite;

public class PlayGameScreenDebugPainter extends PlayGameScreenPainter {

	private static final int LOCATION_MARKER_SIZE = 4;

	public PlayGameScreenDebugPainter(PlayGameScreen screen) {
		super(screen);
	}

	@Override
	protected void paintWorm(WormSprite sprite) {

		drawName(sprite);

		drawActionBar(sprite);

		drawOutline(sprite);
		drawJumpMarkers(sprite); // also draw for other worms

		drawDirectionLine(sprite);

		drawLocationMarker(sprite);

	}
	
	@Override
	protected void paintLevel() {
		drawCrossMarker(getScreenX(0), getScreenY(0), 10, Color.BLUE);
	}

	@Override
	protected void drawJumpMarkers(WormSprite sprite) {

		double[][] xys = sprite.getJumpSteps();
		if (xys != null) {
			double[] prevXY = xys[0];
			for (int i = 1; i < xys.length; i++) {
				double[] xy = xys[i];
				if (xy != null && prevXY != null) {
					double jumpX = getScreenX(xy[0]);
					double jumpY = getScreenY(xy[1]);
					currentGraphics.setColor(JUMP_MARKER_COLOR);
					currentGraphics.drawLine((int) getScreenX(prevXY[0]),
							(int) getScreenY(prevXY[1]), (int) jumpX,
							(int) jumpY);
					prevXY = xy;
					drawCrossMarker(jumpX, jumpY, JUMP_MARKER_SIZE,
							JUMP_MARKER_COLOR);
				}
			}
		}
	}

	/**
	 * Draw a marker at the current location of the worm (which is not
	 * necessarily equal to the sprite's location)
	 */
	protected void drawLocationMarker(WormSprite worm) {
		double x = worm.getActualX();
		double y = worm.getActualY();

		drawCrossMarker(getScreenX(x), getScreenY(y), LOCATION_MARKER_SIZE,
				Color.YELLOW);
	}

	protected void drawOutline(WormSprite sprite) {
		double r = sprite.getRadius();
		double x = sprite.getCenterX();
		double y = sprite.getCenterY();

		currentGraphics.setColor(Color.YELLOW);
		Shape circle = GUIUtils.circleAt(x, y, getScreen()
				.worldToScreenDistance(r));
		currentGraphics.draw(circle);

	}

	protected void drawDirectionLine(WormSprite sprite) {
		double x = sprite.getCenterX();
		double y = sprite.getCenterY();
		double dist = sprite.getHeight(currentGraphics) / 2.0;
		double direction = sprite.getOrientation();

		currentGraphics.setColor(Color.YELLOW);
		currentGraphics.drawLine((int) x, (int) y,
				(int) (x + dist * Math.cos(direction)),
				(int) (y - dist * Math.sin(direction)));
	}

}
