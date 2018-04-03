package worms.internal.gui.game.modes;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import worms.internal.gui.InputMode;
import worms.internal.gui.game.PlayGameScreen;
import worms.internal.gui.game.sprites.WormSprite;
import worms.model.Worm;

public class DefaultInputMode extends InputMode<PlayGameScreen> {

	/**
	 * @param playGameScreen
	 */
	public DefaultInputMode(PlayGameScreen playGameScreen,
			InputMode<PlayGameScreen> previous) {
		super(playGameScreen, previous);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (getScreen().getGUI().getOptions().enableClickToSelect) {
			Point point = e.getPoint();
			for (WormSprite sprite : getScreen().getSpritesOfType(
					WormSprite.class)) {
				Worm worm = sprite.getWorm();
				if (sprite.hitTest(point.getX(), point.getY())) {
					getScreen().selectWorm(worm);
					return;
				}
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		getScreen().switchInputMode(new TurningMode(getScreen(), this));
		getScreen().getCurrentInputMode().mouseDragged(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		switch (e.getKeyChar()) {
		case 'j':
		case 'J':
			getScreen().jump();
			break;
		case 'n':
		case 'N':
			getScreen().renameWorm();
			break;
		case 'e':
		case 'E':
			getScreen().eat();
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			getScreen().move();
			break;
		case KeyEvent.VK_ESCAPE:
			getScreen().getGUI().exit();
			break;
		case KeyEvent.VK_TAB:
			getScreen().selectNextWorm();
			break;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:
			getScreen().switchInputMode(new TurningMode(getScreen(), this));
			getScreen().getCurrentInputMode().keyPressed(e);
			break;
		}
	}

}