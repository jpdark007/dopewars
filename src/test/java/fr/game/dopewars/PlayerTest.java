package fr.game.dopewars;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class PlayerTest {
	
	private static Player player;

	@BeforeClass
	public static void onceExecutedBeforeAll() {
		player = new Player("test");
	}
	
	@Test
	public void testCash() {
		assertEquals(TraderConstants.STARTING_CASH, player.getCash());
	}
	
	@Test
	public void testHealth() {
		assertEquals(TraderConstants.STARTING_HEALTH, player.getHealth());
	}
}
