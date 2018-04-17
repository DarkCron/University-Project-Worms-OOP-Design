package worms.model;

import worms.facade.IFacade;
import worms.internal.gui.GUIConstants;
import worms.internal.gui.game.IActionHandler;
import worms.util.ModelException;

/**
 * A simple action handler that just calls the necessary methods on the facade.
 * Useful for writing tests; there's no other reason to use this.
 */
public class SimpleActionHandler implements IActionHandler {

	private final IFacade facade;

	public SimpleActionHandler(IFacade facade) {
		this.facade = facade;
	}

	@Override
	public boolean turn(Worm worm, double angle) {
		try {
			facade.turn(worm, angle);
			return true;
		} catch (ModelException e) {
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			}
			throw e;
		}
	}

	@Override
	public void print(String message) {
		System.out.println(message);
	}

	@Override
	public boolean move(Worm worm) {
		try {
			facade.move(worm);
			if (facade.canFall(worm)) {
				facade.fall(worm);
			}
			return true;
		} catch (ModelException e) {
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			}
			throw e;
		}
	}

	@Override
	public boolean jump(Worm worm) {
		try {
			facade.jump(worm, GUIConstants.JUMP_TIME_STEP);
			if (facade.canFall(worm)) {
				facade.fall(worm);
			}
			return true;
		} catch (ModelException e) {
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			}
			throw e;
		}
	}

	@Override
	public boolean fire(Worm worm) {
		try {
			facade.fire(worm);
			return true;
		} catch (ModelException e) {
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			}
			throw e;
		}
	}

	@Override
	public boolean eat(Worm worm) {
		try {
			facade.eat(worm);
			return true;
		} catch (ModelException e) {
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			}
			throw e;
		}
	}
}