package worms.model.ShapeHelp;

import worms.model.World;
import worms.model.values.Location;

/**
 * A utility class for managing Rectangle shapes
 * 
 * @author bernd
 *
 * @invar | isValidSize(getSize())
 * 
 * @see super
 */
public class Rectangle extends Shape {

	/**
	 * Creates a new Rectangle with it's center being the TOP LEFT point of the rectangle.
	 * 
	 * @param center
	 * @param size
	 * 
	 * @post | new.getSize() == size
	 * 
	 * @see super
	 * 
	 * @throws IllegalArgumentException
	 * 		| !isValidSize(size)
	 */
	public Rectangle(Location center, Location size) throws IllegalArgumentException {
		super(center);
		if(!isValidSize(size)) {
			throw new IllegalArgumentException("Invalid size for rectangle.");
		}
		this.setSize(size);
	}

	/**
	 * Checks whether the given size is valid for any and all rectangles.
	 * 
	 * @param size
	 * @return | result == size != null && size.getX() != 0 && size.getY() != 0
	 */
	public static boolean isValidSize(Location size) {
		return size != null && size.getX() > 0 && size.getY() > 0;
	}
	
	/**
	 * Sets the size of this rectangle.
	 * 
	 * @param size
	 * 
	 * @post | new.getSize() == size
	 */
	public void setSize(Location size) {
		this.size = size;
	}
	
	/**
	 * Returns this Rectangle's size.
	 */
	public Location getSize() {
		return this.size;
	}
	
	private Location size = new Location(1,1);
	
	/**
	 * Returns part of a given map based on this rectangle.
	 * 
	 * @param map
	 * @return 	| isValidSize(result.size())
	 * 			| && result.size() == this.getSize().getY()
	 * 			| && result[0].size() == this.getSize().getX()
	 * 			| for each y in 0..result.size() 
	 * 			|	result[y] != null
	 * 			|	for each x in 0..result[y].size() 
	 * 			|		array[x][y] == map[(int)(temp.getCenter().getY() ) + y][(int)(temp.getCenter().getX() ) + x]
	 */
	public BoundaryRectangle[][] getArraySetFromSizeAndPassableMap(BoundaryRectangle[][] map){
		Rectangle temp = this;
		try {
			//temp = this.getRectangleAroundMap(map);

			int x = (int)temp.getCenter().getX()-(int)temp.getSize().getX()/4;
			int y = (int)temp.getCenter().getY()-(int)temp.getSize().getY()/4;

			
			if(x < 0) {
				x=0;
			}
			if(y < 0) {
				y=0;
			}
			
			int w = (int)(Math.floor(temp.getCenter().getX() + temp.getSize().getX())) - x +(int)(temp.getSize().getX()/2);
			int h = (int)(Math.floor(temp.getCenter().getY() + temp.getSize().getY())) - y +(int)(temp.getSize().getX()/2);
			
			if(x < 0) {
				x=0;
			}
			if(y < 0) {
				y=0;
			}
			
			if(h >= map.length) {
				h = map.length -1;
			}
			if(w >= map[0].length) {
				w = map[0].length -1;
			}

			temp = new Rectangle(new Location(x, y), new Location(w, h));
		} catch (Exception e) {
			return new BoundaryRectangle[0][0];	
		}
		
		
		BoundaryRectangle[][] array = new BoundaryRectangle[(int)temp.getSize().getY()][];
		for (int i = 0; i < temp.getSize().getY(); i++) {
			array[i] = new BoundaryRectangle[(int)temp.getSize().getX()];
			for (int j = 0; j < temp.getSize().getX() ; j++) {
				int indexY = map.length - 1 - (int)(temp.getCenter().getY()) -i;
				int indexX = (int)(temp.getCenter().getX() ) + j;
				if(indexY >= map.length) {
					indexY = map.length - 1;
				}
				if(indexX >= map[0].length) {
					indexX = map[0].length -1;
				}
				if(indexY <= -1) {
					indexY = 0;
				}
				if(indexX <= -1) {
					indexX = 0;
				}
	
				array[i][j]= map[indexY][indexX];
			}
		}
		return array;
	}
	
	//TODO
	public Rectangle getRealMapSizeRectangle(World w) {
		return new Rectangle(new Location(this.getCenter().getX() / w.getWidthRatio(),this.getCenter().getY() / w.getHeightRatio()), new Location(this.getSize().getX() / w.getWidthRatio(),this.getSize().getY() / w.getHeightRatio()));
	}
	
	/**
	 * Returns an exact rectangle that a piece of map can contain.
	 * 
	 * @param map
	 * @return 	| result.getCenter().getX() >= 0 && result.getCenter().getX() <= map[0].length
	 * 			| && result.getCenter().getY() >= 0 && result.getCenter().getY() <= map.length
	 * 			| && result.getSize().getX() > 0 && result.getSize().getX() <= (map[0].length - result.getCenter().getX())
	 * 			| && result.getSize().getY() > 0 && result.getSize().getY() <= (map.length - result.getCenter().getY())
	 */
	public Rectangle getRectangleAroundMap(BoundaryRectangle[][] map) {
		boolean bTopOK = this.getCenter().getY() >= 0;
		boolean bBottomOK = this.getCenter().getY() + this.getSize().getY() <= map.length;
		boolean bLeftOK = this.getCenter().getX()  >= 0;
		boolean bRightOK = this.getCenter().getX() + this.getSize().getX() <= map[0].length;
		
		double actualHeight = this.getSize().getY();
		double actualWidth = this.getSize().getX();
		double topLeftX = (this.getCenter().getX());
		double topLeftY = (this.getCenter().getY());
		

		
		if(!bLeftOK) {
			double difference = topLeftX ;
			topLeftX = 0;
			actualWidth += difference;
		}
		if(!bRightOK) {
			double difference = (double)map[0].length - (this.getCenter().getX() + this.getSize().getX());
			actualWidth += difference;
		}
		if(!bTopOK) {
			double difference = topLeftY;
			topLeftY = 0;
			actualHeight += difference;
		}
		if(!bBottomOK) {
			double difference =  (double)map.length - (this.getCenter().getY() + this.getSize().getY());
			actualHeight += difference;
		}
		
		return new Rectangle(new Location((topLeftX), (topLeftY)), new Location((actualWidth), (actualHeight)));
	}
	
	//TODO
	public Location getInsideMapLocation() {
		Location newPoint = new Location(this.getCenter().getX(),this.getCenter().getY());
		if(this.getCenter().getX() < 0) {
			newPoint = new Location(0, this.getCenter().getY());
		}
		if(this.getCenter().getY() < 0) {
			newPoint = new Location(newPoint.getX(), 0);
		}
		return newPoint;
	}

	public boolean containsPoint(Location loc) {
		if(loc.getX() >= this.getCenter().getX() && loc.getX() <= this.getCenter().getX() + this.getSize().getX()) {
			if(loc.getY() >= this.getCenter().getY() && loc.getY() <= this.getCenter().getY() + this.getSize().getY()) {
				return true;
			}
		}
		return false;
	}

	public boolean fullyContains(Rectangle boundingRectangle) {
		return  boundingRectangle.getCenter().getX()>=0
				&& boundingRectangle.getCenter().getY() >= 0
				&& (this.getCenter().getX()+this.getSize().getX()) >= (boundingRectangle.getCenter().getX() + boundingRectangle.getSize().getX())
				&& (this.getCenter().getY()+this.getSize().getY()) >= (boundingRectangle.getCenter().getY() + boundingRectangle.getSize().getY());
	}
}
