package fr.game.dopewars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import org.junit.BeforeClass;
import org.junit.Test;

public class LocationTest {

	final static private Product[] products = { new Product("Acid", 1000, 4500), new Product("Cocaine", 15000, 30000)};
	final private static Location location = new Location("test", products);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testLocation() throws Exception {
		assertEquals("test", location.toString());
	}

	@Test
	public void testToString() throws Exception {
		assertEquals("test", location.getName());
	}

	@Test
	public void testGetName() throws Exception {
		assertEquals("test", location.getName());
	}

	@Test
	public void testGetProducts() throws Exception {
		assertArrayEquals(products, location.getProducts());
	}

}
