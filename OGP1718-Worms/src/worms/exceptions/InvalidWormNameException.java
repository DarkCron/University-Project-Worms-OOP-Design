package worms.exceptions;

import be.kuleuven.cs.som.annotate.*;
import worms.model.values.Name;

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
     * Initialize this new illegal location exception with given value.
     * 
     * @param   name
     *          The string for the new invalid name exception.
     * @post    The value of the new illegal name exception is set
     *          to the given value. If the given Name was null this value will be set to an invalid value null.
     *          | if name == null
     *          |	then
     *          |		this.value = null
     *          |	else
     *          |		this.value = name.getName()
     */
    public InvalidWormNameException(Name name) {
    	if(name == null) {
    		this.value = null;
    	}else {
    		this.value = name.getName();
    	}
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