package worms.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import worms.model.values.Direction;
import worms.model.values.Location;
import worms.model.values.Name;
import worms.model.values.Radius;

public class TeamTest {

	private boolean[][] passableMap = new boolean[][] { //
		{ false, false, false, false }, //
		{ true, true, true, true }, //
		{ true, true, true, true }, //
		{ false, false, false, false } };
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		World w = new World(passableMap);
		Team t = new Team(w, Name.DEFAULT_NAME);
		t.addWorm(new Worm(new Location(1, 1), Direction.DEFAULT_DIRECTION, w, Radius.DEFAULT_RADIUS, new Name("Worm A"), t));
		t.addWorm(new Worm(new Location(1, 1), Direction.DEFAULT_DIRECTION, w, Radius.DEFAULT_RADIUS, new Name("Worm B"), t));
		t.addWorm(new Worm(new Location(1, 1), Direction.DEFAULT_DIRECTION, w, Radius.DEFAULT_RADIUS, new Name("Worm C"), t));
		t.addWorm(new Worm(new Location(1, 1), Direction.DEFAULT_DIRECTION, w, Radius.DEFAULT_RADIUS, new Name("Worm E"), t));
		t.addWorm(new Worm(new Location(1, 1), Direction.DEFAULT_DIRECTION, w, Radius.DEFAULT_RADIUS, new Name("Worm D"), t));
		assertTrue(t.getAlphabeticalListTeamRoster().get(3).getName().equals("Worm D"));
		t.addWorm(new Worm(new Location(1, 1), Direction.DEFAULT_DIRECTION, w, Radius.DEFAULT_RADIUS, new Name("Worm AA"), t));
		t.addWorm(new Worm(new Location(1, 1), Direction.DEFAULT_DIRECTION, w, Radius.DEFAULT_RADIUS, new Name("Worm F"), t));
		assertTrue(t.getAlphabeticalListTeamRoster().get(0).getName().equals("Worm A"));
		assertTrue(t.getAlphabeticalListTeamRoster().get(1).getName().equals("Worm AA"));
		assertTrue(t.getAlphabeticalListTeamRoster().get(6).getName().equals("Worm F"));
		t.addWorm(new Worm(new Location(1, 1), Direction.DEFAULT_DIRECTION, w, Radius.DEFAULT_RADIUS, new Name("Worm CC"), t));
		assertTrue(t.getAlphabeticalListTeamRoster().get(3).getName().equals("Worm C"));
		assertTrue(t.getAlphabeticalListTeamRoster().get(4).getName().equals("Worm CC"));
	}

}
