package worms.model;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

import worms.model.values.HP;

public class HPTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		HP hp = new HP(new BigInteger("1000"),new BigInteger("2000"));
		assertTrue(hp.getHp().compareTo(new BigInteger("2000"))<=0);
	}

}
