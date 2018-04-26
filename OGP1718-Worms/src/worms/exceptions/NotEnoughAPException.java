package worms.exceptions;

import be.kuleuven.cs.som.annotate.*;
import worms.model.values.Location;

/**
 * A class for signaling invalid locations for various objects
 * 
 * @author   Liam, Bernd
 */
public class NotEnoughAPException extends RuntimeException {
    
    /**
     * Initialize this new illegal location exception with given value.
     * 
     * @param   location
     *          The set of coordinates for the new invalid location exception.
     * @post    The value of the new illegal location exception is set
     *          to the given value.
     *          | new.getValue() == msg
     */
    public NotEnoughAPException(String msg) {
    	this.value = msg;
    }

    
    /**
     * Return the value registered for this invalid location exception.
     */
    @Basic @Immutable
    public String getValue() {
        return this.value;
    }
    
    /**
     * Variable registering the value involved in this invalid location
     * exception.
     */
    private final String value;

    /**
     * We must declare a version to ensure all systems/ programs run on the same exception version.
     */
    private static final long serialVersionUID = 2018001L;
    
} 