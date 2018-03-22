package worms.model.values;

import be.kuleuven.cs.som.annotate.*;
import worms.exceptions.InvalidWormNameException;
/**
 * @invar	Returns true if and only if the name only has valid characters.
 * 			|nameContainsValidCharactersOnly(getName())
 */
@Value
public class Name {
	/**
	 * @param name
	 * 		  The name for this new Name.
	 * @post the name for this new Name, is the same as the given name.
	 * 		 |new.getName().equals(name)
	 * @throws InvalidWormNameException()
	 * 		  The given name is not a valid name.
	 * 		 | ! nameContainsValidCharactersOnly(name)
	 */
	@Raw
	public Name(String name) throws InvalidWormNameException{
		if (!nameContainsValidCharactersOnly(name)){
			throw new InvalidWormNameException(name);
		}
		this.name = name;
	}
	
	/**
	 * A default Name that adheres to the class invariants.
	 */
	public final static Name DEFAULT_NAME = new Name("Please name me");
	
	/**
	 * Returns the name.
	 */
	@Basic @Raw @Immutable
	public String getName() {
		return this.name;
	}
	
	/**
	 * Checks whether a character is a valid one for a worm's name.
	 * Currently only letters, whitespaces (blank spaces) and single quotation
	 * marks are allowed, any other character is rejected.
	 * 
	 * @return returns true if given character is a letter
	 * 		| result ==
	 * 		| 	Character.isLetter(c)
	 * @return returns true if given character is a whitespace (blank space)
	 * 		| result ==
	 * 		| 	Character.isWhitespace(c)
	 * @return returns true if given character is a single quotation mark '
	 * 		| result ==
	 * 		| 	c == '\''
	 * @return returns false if given character not a valid character.
	 * 			Not a letter, whitespace or single quotation mark.
	 * 		| result ==
	 * 		| 	(!Character.isLetter(c) && !(c == '\'') && !Character.isLetter(c))
	 */
	public static boolean isValidCharacter(char c) {
		if (Character.isLetter(c)) {
			return true;
		}

		if (Character.isWhitespace(c)) {
			return true;
		}
		if (c == '\'') {
			return true;
		}
		return false;
	}

	/**
	 * Check whether a string name only contains characters that are allowed by the
	 * programmer.
	 * 
	 * @return False if the given name is shorter than 2 characters
	 * 		| result ==
	 * 		|	(name == null)
	 * @return False if the given name is null
	 * 		| result ==
	 * 		|	(name.length() < 2)
	 * @return False if the given name does not start with an uppercase character
	 * 		| result ==
	 * 		|	(!Character.isUpperCase(name.charAt(0))
	 * @return True if all characters in string name are valid. 
	 * 		|	boolean bIsValidName = true
	 * 		|	for (char c :name.toCharArray()) 
	 * 		|		bIsValidName = isValidCharacter(c)
	 * 		|	if bIsValidName
	 * 		|	then	result == true
	 * @return False if a single character in string name is invalid. 
	 * 		|	boolean bIsValidName = false
	 * 		|	for (char c :name.toCharArray()) 
	 * 		|		bIsValidName = isValidCharacter(c)
	 * 		|		if !bIsValidName
	 * 		|		then	result == false
	 */
	public static boolean nameContainsValidCharactersOnly(String name){
		if (name == null)
		{
			return false;
		}
		if(name.length() < 2) {
			return false;
		}
		if(!Character.isUpperCase(name.charAt(0))) {
			return false;
		}
		
		boolean bNameIsValid = true;
		
		for (char c : name.toCharArray()) {
			if (!isValidCharacter(c)) {
				bNameIsValid = false;
			}	
		}
		return bNameIsValid;
	}
	
	/**
	 * name member of Name.
	 */
	private final String name;
	
	/**
	 * Checks whether this current Name is a valid one.
	 * 
	 * @return True if and only if this name is valid, only contains valid characters and if it starts with a capital letter.
	 * 		| result ==
	 * 		|		nameContainsValidCharactersOnly(this.getName())
	 */
	public boolean isValid()
	{
		return nameContainsValidCharactersOnly(this.getName());
	}
	
	/**
	 * Check whether this Name is equal to the given object.
	 * 
	 * @return True if and only if the given object is effective,
	 * 		   if this Name and the given object belong to the same class,
	 * 		   and if this Name's name and the given object interpreted as 
	 * 		   a Name have equal names. 
	 * 		  | result == 
	 * 		  |   ((obj != null)
	 * 		  |  && (this.getClass() == obj.getClass())
	 * 		  |  && this.getName().equals(otherName.getName())
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Name otherName = (Name) obj;
		return this.getName().equals(otherName.getName());
	}
	
	/**
	 * Returns the hashcode for this Name.
	 */
	@Override
	public int hashCode() {
		return getName().hashCode();
		}

	/**
	 * Returns the name as a textual representation 
	 * @return A string containing the name and a dot.
	 * 		  | result.equals(getName() + ".")
	 */
	@Override
	public String toString() {
		return getName() + ".";
	}
}
