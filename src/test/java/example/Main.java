package example;


import endercrypt.library.jpantry.JPantry;
import endercrypt.library.jpantry.PantryBasket;

import com.google.gson.JsonObject;


public class Main
{
	private static final String token = System.getenv("PANTRY_TOKEN"); // this example uses the token from the environmental variable
	
	public static void main(String[] args)
	{
		// create a JPantry instance by using the builder, only token is required
		JPantry pantry = new JPantry.Builder()
			.setToken(token)
			.setCacheTime(500) // in milliseconds (default is 1000, network requests usually take 300+ ms anyways)
			.login();
		
		// changing of name/description
		pantry.setInformation("Pantry name", "Pantry Description").queue();
		// .queue() performs this action in a non-blocking way
		// (it will return instantly and then finish the action in a background thread provided by the executor setup in JPantry.Builder)
		
		// retrieve a basket, this action is instant and free (it doesn't actually do anything, just provide a pointer to the basket in the cloud)
		PantryBasket coordinateBasket = pantry.getBasket("coordinates");
		
		// delete any old json value in this basket if one already exist
		coordinateBasket.deleteJson().complete();
		// .complete() performs this action in a blocking way
		// some actions .complete() and .queue(callback) will return a value
		
		// set the value of a basket
		JsonObject firstJson = new JsonObject();
		firstJson.addProperty("x", 10);
		firstJson.addProperty("y", 20);
		coordinateBasket.setJson(firstJson).complete();
		
		// merge the basket (performs an update)
		JsonObject secondJson = new JsonObject();
		secondJson.addProperty("z", 30);
		coordinateBasket.mergeJson(secondJson).complete();
		
		// get basket value
		coordinateBasket.getJson().queue((json) -> System.out.println(json));
		// by using .queue with a callback, we'll receive the value there once available
		// (in this example, the result is cached, but it MAY still arrive slightly delayed a few nanoseconds due to use of threads)
		
		// bye
		System.out.println("Main over!");
	}
}
