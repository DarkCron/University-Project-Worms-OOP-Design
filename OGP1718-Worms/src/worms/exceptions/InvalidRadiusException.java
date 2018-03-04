package worms.exceptions;

import be.kuleuven.cs.som.annotate.*;

/**
 * A class for signaling invalid radius for various objects
 * 
 * @author   Liam, Bernd
 */
public class InvalidRadiusException extends RuntimeException {
    
    /**
     * Initialize this new illegal radius exception with given value.
     * 
     * @param   radius
     *          The radius for the new invalid radius exception.
     * @post    The value of the new illegal radius exception is set
     *          to the given value.
     *          | new.getValue() == value
     */
    public InvalidRadiusException(double radius) {
    	this.value = radius;
    }
    
    /**
     * Return the value registered for this invalid radius exception.
     */
    @Basic @Immutable
    public double getValue() {
        return this.value;
    }
    
    /**
     * Variable registering the value involved in this invalid radius
     * exception.
     */
    private final double value;

    /**
     * We must declare a version to ensure all systems/ programs run on the same exception version.
     */
    private static final long serialVersionUID = 2018001L;
    
} 