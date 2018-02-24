package worms.exceptions;

import be.kuleuven.cs.som.annotate.*;

/**
 * A class for signaling invalid locations for various objects
 * 
 * @author   
 */
public class InvalidLocationException extends RuntimeException {
    
    /**
     * Initialize this new illegal denominator exception with given value.
     * 
     * @param   x
     *          The x-value for the new invalid location exception.
     * @param   y
     *          The y-value for the new invalid location exception.
     * @post    The value of the new illegal denominator exception is set
     *          to the given value.
     *          | new.getValue() == value
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
    private final double value[];

    /**
     * We must declare a version to ensure all systems/ programs run on the same exception version.
     */
    private static final long serialVersionUID = 2018001L;
    
}