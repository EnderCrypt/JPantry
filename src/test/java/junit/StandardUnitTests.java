package junit;


import static org.junit.jupiter.api.Assertions.*;

import endercrypt.library.jpantry.JPantry;
import endercrypt.library.jpantry.PantryBasket;
import endercrypt.library.jpantry.PantryDetails;
import endercrypt.library.jpantry.exception.JPantryAuthenticationException;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;


public class StandardUnitTests
{
	private static final String token = System.getenv("PANTRY_TOKEN");
	private static final String testBasket = "JUNIT_TEST";
	
	@Test
	public void testThatValidLoginWorks()
	{
		JPantry pantry = new JPantry.Builder()
			.setToken(token)
			.login();
		
		assertNotNull(pantry);
	}
	
	@Test
	public void testThatFakeLoginFails()
	{
		assertThrows(JPantryAuthenticationException.class, () -> {
			
			String badToken = token.replaceAll("[0-9]", "a");
			
			new JPantry.Builder()
				.setToken(badToken)
				.login();
			
		});
	}
	
	@Test
	public void testThatDetailsAreValid()
	{
		JPantry pantry = new JPantry.Builder()
			.setToken(token)
			.login();
		
		PantryDetails details = pantry.getDetails().complete();
		
		assertTrue(details.getName().length() > 0);
		assertTrue(details.getDescription().length() > 0);
	}
	
	@Test
	public void testThatDeletesWork()
	{
		JPantry pantry = new JPantry.Builder()
			.setToken(token)
			.login();
		
		PantryBasket basket = pantry.getBasket(testBasket);
		
		// ensure its already empty
		basket.deleteJson().complete();
		
		assertFalse(basket.deleteJson().complete());
		
		// set value
		JsonObject sample = new JsonObject();
		sample.addProperty("key", "value");
		basket.setJson(sample).complete();
		
		assertTrue(basket.deleteJson().complete());
	}
	
	@Test
	public void testThatMergeWorks()
	{
		JPantry pantry = new JPantry.Builder()
			.setToken(token)
			.login();
		
		PantryBasket basket = pantry.getBasket(testBasket);
		
		basket.deleteJson();
		
		JsonObject a = new JsonObject();
		a.addProperty("a", "1");
		basket.setJson(a).complete();
		
		JsonObject b = new JsonObject();
		b.addProperty("b", "2");
		basket.mergeJson(b).complete();
		
		JsonObject expected = new JsonObject();
		expected.addProperty("a", "1");
		expected.addProperty("b", "2");
		
		assertEquals(expected, basket.getJson().complete());
	}
}
