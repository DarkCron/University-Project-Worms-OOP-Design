package worms.programs;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import worms.programs.IProgramFactory;
import worms.programs.internal.parser.ParserVisitor;
import worms.programs.internal.parser.generated.WormsProgramLexer;
import worms.programs.internal.parser.generated.WormsProgramParser;
import worms.util.MustNotImplementException;
import worms.internal.gui.GUIUtils;
import worms.model.Program;

/**
 * Parser for Worms programs.
 * 
 * To use this class, first create an implementation of {@link IProgramFactory}:
 * 
 * <pre>
 * <code>
 * IProgramFactory&lt;MyExpression, MyStatement, MyProcedure, Program&gt; factory = new ProgramFactory();
 * </code>
 * </pre>
 * 
 * The easiest way to use this class for parsing a Program given as a String is
 * via the {@link #parseProgramFromString(String, IProgramFactory)} method:
 * 
 * <pre>
 * <code>
 * Program program = ProgramParser.parseProgram(text, factory);
 * </code>
 * </pre>
 * 
 * For more control, create an instance of this class:
 * 
 * <pre>
 * <code>
 * ProgramParser&lt;MyExpression, MyStatement, Task&gt; parser = new ProgramParser<>(factory);
 * </code>
 * </pre>
 * 
 * Finally, parse a string or file: <code><pre>
 * ParseOurcome&lt;Program&gt; parseResult = parser.parse(textToParse);
 * </pre></code>
 * 
 * If parsing is successful, <code>parseResult.isSuccess()</code> returns true
 * and <code>parseResult.getSuccessValue()</code> returns the created program.
 * 
 * If parsing was not successful, <code>parseResult.ifSuccess()</code> returns
 * false and <code>parseResult.getFailValue()</code> returns the list of errors
 * during parsing.
 * 
 *
 * @param E
 *            The type of expressions
 * @param S
 *            The type of statements
 * @param P
 *            The type of procedures
 * @param Prg
 *            The type of program that is created; should be your
 *            worms.model.Program class
 */
public class ProgramParser<E, S, P, Prg> {

	private final IProgramFactory<E, S, P, Prg> factory;

	private final List<String> errors = new ArrayList<>();

	protected ProgramParser(IProgramFactory<E, S, P, Prg> factory) {
		if (factory == null) {
			throw new NullPointerException("Factory must be effective.");
		}
		this.factory = factory;
	}

	public IProgramFactory<E, S, P, Prg> getFactory() {
		return factory;
	}

	/**
	 * Returns the program that results from parsing the given string, or
	 * Optional.empty() if parsing has failed.
	 * 
	 * When parsing has failed, the error messages can be retrieved with the
	 * getErrors() method.
	 *
	 */
	public ParseOutcome<Prg> parseString(String string) {
		return parse(CharStreams.fromString(string));
	}

	/**
	 * Returns the program that results from parsing the file with the given name,
	 * or Optional.empty() if parsing has failed.
	 * 
	 * When parsing has failed, the error messages can be retrieved with the
	 * getErrors() method.
	 */
	public ParseOutcome<Prg> parseFile(String filename) throws IOException {
		return parse(CharStreams.fromStream(GUIUtils.openResource(filename)));
	}

	/**
	 * Returns the program that results from parsing the file with the given URL, or
	 * Optional.empty() if parsing has failed.
	 * 
	 * When parsing has failed, the error messages can be retrieved with the
	 * getErrors() method.
	 */
	public ParseOutcome<Prg> parseFile(URL url) throws IOException {
		return parse(CharStreams.fromStream(GUIUtils.openResource(url)));
	}

	/**
	 * Returns the program that results from parsing the given CharStream, or
	 * Optional.empty() if parsing has failed.
	 * 
	 * When parsing has failed, the error messages can be retrieved with the
	 * getErrors() method.
	 * 
	 * @note For more convenient methods, see the
	 *       {@link #parseProgramFromString(String, ITaskFactory, List)},
	 *       {@link #parseProgramFromFile(String, ITaskFactory, List)},
	 *       {@link #parseString(String, List)} and {@link #parseFile(String, List)}
	 *       methods.
	 */
	protected ParseOutcome<Prg> parse(CharStream input) {
		reset();

		WormsProgramLexer lexer = new WormsProgramLexer(input);
		WormsProgramParser parser = new WormsProgramParser(new CommonTokenStream(lexer));
		parser.addErrorListener(new BaseErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
					int charPositionInLine, String msg, RecognitionException e) {
				errors.add(msg + " (" + line + ", " + charPositionInLine + ")");
			}
		});
		ParserVisitor<E, S, P, Prg> visitor = new ParserVisitor<>(factory);
		try {
			visitor.visitProgram(parser.program());
			if (errors.isEmpty()) {
				assert visitor.getMain() != null;
				Prg program = factory.createProgram(visitor.getProcedures(), visitor.getMain());
				if (program != null) {
					return ParseOutcome.success(program);
				}
				errors.add("Factory did not return a Program object");
			}
		} catch (MustNotImplementException e) {
			errors.add(e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			errors.add(e.toString());
		}
		return ParseOutcome.failure(errors);
	}

	protected void reset() {
		this.errors.clear();
	}

	public List<String> getErrors() {
		return Collections.unmodifiableList(errors);
	}

	/**
	 * Create a new parser from the given factory.
	 * 
	 * @param factory
	 * @return
	 */
	public static <E, S, T, P> ProgramParser<E, S, T, P> create(IProgramFactory<E, S, T, P> factory) {
		return new ProgramParser<>(factory);
	}

	/**
	 * Parse program text using the given factory.
	 * 
	 * @param text
	 *            The text to parse
	 * @param factory
	 *            The factory to use
	 * @return The parsed program, if any, or null if an error occurred during
	 *         parsing.
	 */
	public static Program parseProgramFromString(String text, IProgramFactory<?, ?, ?, ? extends Program> factory) {
		ProgramParser<?, ?, ?, ? extends Program> parser = create(factory);
		ParseOutcome<? extends Program> outcome = parser.parseString(text);
		if (outcome.isFail()) {
			System.out.println("Parsing failed: " + outcome.getFailValue());
			return null;
		}
		return outcome.getSuccessValue();
	}

	/**
	 * Parse program from a file using the given factory.
	 * 
	 * @param filename
	 *            The filename from which to read the program text
	 * @param factory
	 *            The factory to use
	 * @return The parsed program, if any, or null if an error occurred during
	 *         parsing.
	 */
	public static Program parseProgramFromFile(String filename, IProgramFactory<?, ?, ?, ? extends Program> factory)
			throws IOException {
		ProgramParser<?, ?, ?, ? extends Program> parser = create(factory);
		ParseOutcome<? extends Program> outcome = parser.parseFile(filename);
		if (outcome.isFail()) {
			System.out.println("Parsing failed: " + outcome.getFailValue());
			return null;
		}
		return outcome.getSuccessValue();
	}

	/**
	 * Parse program from a file using the given factory.
	 * 
	 * @param filename
	 *            The filename from which to read the program text
	 * @param factory
	 *            The factory to use
	 * @return The parsed program, if any, or null if an error occurred during
	 *         parsing.
	 */
	public static Program parseProgramFromURL(URL url, IProgramFactory<?, ?, ?, Program> factory) throws IOException {
		ProgramParser<?, ?, ?, Program> parser = create(factory);
		ParseOutcome<Program> outcome = parser.parseFile(url);
		if (outcome.isFail()) {
			System.out.println("Parsing failed: " + outcome.getFailValue());
			return null;
		}
		return outcome.getSuccessValue();
	}
}
