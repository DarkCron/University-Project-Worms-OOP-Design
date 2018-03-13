package worms.internal.gui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Random;

import worms.facade.IFacade;
import worms.model.World;

public class GUIUtils {

	public static Ellipse2D.Double circleAt(double centerX, double centerY,
			double r) {
		return new Ellipse2D.Double(centerX - r, centerY - r, 2 * r, 2 * r);
	}

	public static void drawCenteredString(Graphics2D g2d, String text,
			double width, double y) {
		Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(text, g2d);
		g2d.drawString(text, (int) (width / 2 - bounds.getCenterX()), (int) y);
	}

	public static double restrictDirection(double direction) {
		return restrictAngle(direction, 0);
	}

	/**
	 * Restrict angle to [min, min+2pi)
	 */
	public static double restrictAngle(double angle, double min) {
		while (angle < min) {
			angle += 2 * Math.PI;
		}
		double max = min + 2 * Math.PI;
		while (angle >= max) {
			angle -= 2 * Math.PI;
		}
		return angle;
	}

	public static double distance(double x1, double y1, double x2, double y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public static Image scaleTo(BufferedImage image, int screenWidth,
			int screenHeight, int hints) {
		double ratio = Math.min((double) screenHeight / image.getHeight(),
				(double) screenWidth / image.getWidth());
		return image.getScaledInstance((int) (ratio * image.getWidth()),
				(int) (ratio * image.getHeight()), hints);
	}

	public static InputStream openResource(String filename) throws IOException {
		URL url = toURL(filename);
		return openResource(url);
	}

	public static InputStream openResource(URL url) throws IOException {
		InputStream result;

		URLConnection conn = url.openConnection();
		result = conn.getInputStream();

		return result;
	}

	public static URL toURL(String filename) throws FileNotFoundException {
		URL url = GUIUtils.class.getResource("/" + filename);
		if (url == null) {
			try {
				File file = new File(filename);
				if (file.exists()) {
					url = new File(filename).toURI().toURL();
				} else {
					throw new FileNotFoundException("File not found: " + filename);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
		}
		return url;
	}
	
	// ideally, this constant would only exist once in the code, but we cannot refer to the constant from worms.model
	private static final double ADJACENCY_RADIUS_FRACTION = 0.1;

	public static double[] findFreeAdjacentSpot(IFacade facade, World world, double radius, Random random) {
		double worldWidth = facade.getWorldWidth(world);
		double worldHeight = facade.getWorldHeight(world);

		// start at random location
		double x = random.nextDouble() * worldWidth;
		double y = random.nextDouble() * worldHeight;
		int n = 0;
		// move towards center
		double angle = Math.atan((worldHeight / 2 - y) / (worldWidth / 2 - x));
		while (!isValidLocation(facade, world, x, y, radius)) {
			// at some point, give up and start somewhere else
			if (!liesInWorld(worldWidth, worldHeight, x, y) || n % 1000 == 0) {
				x = random.nextDouble() * worldWidth;
				y = random.nextDouble() * worldHeight;
				angle = Math.atan((worldHeight / 2 - y) / (worldWidth / 2 - x));
				n = 0;
			}
			double d = ADJACENCY_RADIUS_FRACTION / 2 * radius;
			x += d * Math.cos(angle);
			y += d * Math.sin(angle);
			n += 1;
		}
		return new double[] { x, y };
	}

	private static boolean isValidLocation(IFacade facade, World world, double x, double y, double radius) {
		double[] center = { x, y };
		return facade.isPassable(world, center, radius) &&
		 facade.isAdjacent(world, center , radius);
	}

	private static boolean liesInWorld(double width, double height, double x, double y) {
		return 0 <= x && x <= width && 0 <= y && y <= height;
	}

	public static String numberToName(int n) {
		return String.join("",  Collections.nCopies(n / 26, "Z")) + (char)('A' + (n % 26));
	}
}
