package worms.exceptions;

import be.kuleuven.cs.som.annotate.*;

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
    private final double value[];

    /**
     * We must declare a version to ensure all systems/ programs run on the same exception version.
     */
    private static final long serialVersionUID = 2018001L;
    
} 