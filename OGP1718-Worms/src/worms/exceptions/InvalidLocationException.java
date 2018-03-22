package worms.exceptions;

import be.kuleuven.cs.som.annotate.*;
import worms.model.values.Location;

/**
 * A class for signaling invalid locations for various objects
 * 
 * @author   Liam, Bernd
 */
public class InvalidLocationException extends RuntimeException {
    
    /**
     * Initialize this new illegal location exception with given value.
     * 
     * @param   location
     *          The set of coordinates for the new invalid location exception.
     * @post    The value of the new illegal location exception is set
     *          to the given value.
     *          | new.getValue() == value
     */
    public InvalidLocationException(double[] location) {
    	this.value = location;
    }
    
    /**
     * Initialize this new illegal location exception with given value.
     * 
     * @param   location
     *          A invalid Location
     * @post    The value of the new illegal location exception is set
     *          to the given value. If the given location was null this value will also be set to null.
     *          If the given location contains coordinates this value's coordinates will be set to those.
     *          | if location == null
     *          |	then
     *          |		this.value = null
     *          |	else
     *          |		this.value = location.getLocation()
     *          
     */
    public InvalidLocationException(Location location) {
    	if(location == null) {
    		this.value = null;
    	}else {
    		this.value = location.getLocation();
    	}
    	
    }
    
    /**
     * Initialize this new illegal location exception with given value.
     * 
     * @param   location
     *          A invalid Location
     * @post    The value of the new illegal location exception is set
     *          to the given coordinates x and y to respectively index 0 and 1.
     *          | new.value[0] = x
     *          | new.value[1] = y
     *          
     */
    public InvalidLocationException(double x, double y) {
    	this.value = new double[2];
    	this.value[0] = x;
    	this.value[1] = y;
    	
    }
    
    /**
     * Return the value registered for this invalid location exception.
     */
    @Basic @Immutable
    public double[] getValue() {
        return this.value;
    }
    
    /**
     * Variable registering the value involved in this invalid location
     * exception.
     */
    private final double[] value;

    /**
     * We must declare a version to ensure all systems/ programs run on the same exception version.
     */
    private static final long serialVersionUID = 2018001L;
    
} 