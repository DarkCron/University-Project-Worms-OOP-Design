package worms.parser.expressions;

import be.kuleuven.cs.som.annotate.Value;
import worms.model.values.Direction;
import worms.programs.SourceLocation;

/**
 * A class representing a basic expression
 * 
 * @author bernd
 *
 * @Invar | getSource() != null
 */
@Value
public abstract class BaseExpression {
	
	public BaseExpression(SourceLocation sourceLoc) {
		expSource = sourceLoc;
	}
	
	private final SourceLocation expSource;
	
	public final SourceLocation getSource() {
		return expSource;
	}
	
	/**
	 * Check whether this basic expression is equal to the given object.
	 * 
	 * @return 
	 * 		  | result == 
	 * 		  |   ((obj != null)
	 * 		  |  && (this.getClass() == obj.getClass())
	 * 		  |  && this.getSource() == ((BaseExpression)obj).getSource())
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		BaseExpression otherExp = (BaseExpression) obj;
		return this.getSource() == otherExp.getSource();
	}
	
	/**
	 * Returns the hashcode for this base expression.
	 */
	@Override
	public int hashCode() {
		return this.getSource().hashCode();
	}

	/**
	 * Returns this base expression as a textual representation 
	 * @return 
	 * 		  | result.equals("Exp from source: " + this.getSource() )
	 */
	@Override
	public String toString() {
		return "Exp from source: " + this.getSource() ;
	}
}
