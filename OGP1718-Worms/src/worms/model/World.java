package worms.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class World {
	
	public World(double width, double height, boolean[][] passableMap) {
		if(isValidWorldSize(width)) {
			width = 0;
		}
		if(isValidWorldSize(height)) {
			height = 0;
		}
		this.worldWidth = width;
		this.worldHeight = height;	
		this.passableMap = passableMap;
	}

	
	private final double worldWidth;
	private final double worldHeight;
	
	public boolean isValidWorldSize(double size) {
		if(size == Double.NaN) {
			return false;
		}else if(!(size >= 0 && size <= Double.MAX_VALUE)) {
			return false;
		}
		
		return true;
	}
	
	private boolean[][] passableMap;
	
	private Set<GameObject> worldObjects = new HashSet<GameObject>();
}
