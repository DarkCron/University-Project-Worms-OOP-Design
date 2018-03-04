package worms.exceptions;

import be.kuleuven.cs.som.annotate.*;

/**
 * A class for signaling invalid names for various objects
 * 
 * @author   Liam, Bernd
 */
public class InvalidWormNameException extends RuntimeException {
    
    /**
     * Initialize this new illegal location exception with given value.
     * 
     * @param   name
     *          The string for the new invalid name exception.
     * @post    The value of the new illegal name exception is set
     *          to the given value.
     *          | new.getValue() == value
     */
    public InvalidWormNameException(String name) {
    	this.value = name;
    }
    
    /**
     * Return the value registered for this invalid name exception.
     */
    @Basic @Immutable
    public String getValue() {
        return this.value;
    }
    
    /**
     * Variable registering the value involved in this invalid name
     * exception.
     */
    private final String value;

    /**
     * We must declare a version to ensure all systems/ programs run on the same exception version.
     */
    private static final long serialVersionUID = 2018001L;
    
} 