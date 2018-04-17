package worms.internal.gui;

public abstract class AbstractPainter<S extends Screen> {

	private final S screen;

	protected AbstractPainter(S screen) {
		this.screen = screen;
	}

	protected S getScreen() {
		return screen;
	}

}
