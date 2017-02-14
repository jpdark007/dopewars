package fr.game.dopewars;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class PlayerTest {
	
	private static fr.game.dopewars.Player player;

	@Test
	public void testTotal() {
		assertEquals(TraderConstants.STARTING_CASH, player.getCash());
	}
}
