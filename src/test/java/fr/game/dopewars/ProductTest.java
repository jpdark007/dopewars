package fr.game.dopewars;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class ProductTest {
	
	private static Product product;

	@BeforeClass
	public static void onceExecutedBeforeAll() {
		product = new Product("test", 0, 100);
	}
	
	@Test
	public void testProduct() {
		assertEquals("test", product.getName());
	}
}
