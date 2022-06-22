package junit;


import static org.junit.jupiter.api.Assertions.*;

import endercrypt.library.jpantry.JPantry;
import endercrypt.library.jpantry.exception.JPantryAuthenticationException;

import org.junit.jupiter.api.Test;


public class StandardUnitTests
{
	private static final String token = System.getenv("PANTRY_TOKEN");
	
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
}
