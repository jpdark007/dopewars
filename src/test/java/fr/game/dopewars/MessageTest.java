package fr.game.dopewars;

import java.awt.Color;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class MessageTest {
	
	private final static Message message = new Message(Color.BLACK, "test");

	@Test
	public void testGetColor() throws Exception {
		assertEquals(Color.BLACK, message.getColor());
	}

	@Test
	public void testGetMessage() throws Exception {
		assertEquals("test", message.getMessage());
	}

	@Test
	public void testToString() throws Exception {
		assertEquals("test", message.toString());
	}

}
