package fr.game.dopewars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class NpcTest {

	final static private Npc npc =  new Npc("Street Thug", 20, 20, 5, 2000, 5000, 5);
	
	@Test
	public void testIsAlive() throws Exception {
		assertTrue(npc.isAlive());
	}

	@Test
	public void testGetName() throws Exception {
		assertEquals("Street Thug", npc.getName());
	}

	@Test
	public void testGetHealth() throws Exception {
		assertEquals(20, npc.getHealth());
	}

	@Test
	public void testGetMaxHealth() throws Exception {
		assertEquals(20, npc.getMaxHealth());
	}

	@Test
	public void testTakeDamage() throws Exception {
		npc.takeDamage(5);
		assertEquals(15, npc.getHealth());
		npc.reset();
	}

	@Test
	public void testGetStrength() throws Exception {
		assertEquals(20, npc.getStrength());
	}

	@Test
	public void testGetDefense() throws Exception {
		assertEquals(5, npc.getDefense());
	}

	@Test
	public void testGetLevel() throws Exception {
		assertEquals(5, npc.getLevel());
	}

	@Test
	public void testGetCash() throws Exception {
		assertThat(npc.getCash(), allOf(greaterThan(2000L), lessThan(5000L)));
	}

	@Test
	public void testToString() throws Exception {
		assertEquals("Street Thug", npc.toString());
	}

	@Test
	public void testReset() throws Exception {
		npc.takeDamage(5);
		npc.reset();
		assertEquals(20, npc.getHealth());
	}

}
