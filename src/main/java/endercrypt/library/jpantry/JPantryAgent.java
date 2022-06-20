package endercrypt.library.jpantry;


import endercrypt.library.jpantry.exception.JPantryException;
import endercrypt.library.jpantry.exception.JPantryRejectedException;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


/**
 * Internal network requesting api for making requests to
 * https://getpantry.cloud/ easily
 * 
 * @author EnderCrypt
 */
public class JPantryAgent
{
	public static String generatePantryApiUrl(String token, String endpoint)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("https://getpantry.cloud/apiv1/pantry/");
		builder.append(token);
		builder.append("/");
		if (endpoint != null)
		{
			builder.append(endpoint);
		}
		return builder.toString();
	}
	
	private static final Gson gson = new Gson();
	
	private final UUID token;
	private final String name;
	
	protected JPantryAgent(UUID token, String name)
	{
		this.token = Objects.requireNonNull(token, "token");
		this.name = Objects.requireNonNull(name, "name");
	}
	
	public JsonObject requestJson(Method method, String endpoint, JsonObject data) throws JPantryRejectedException
	{
		String body = requestBody(method, endpoint, data);
		return gson.fromJson(body, JsonObject.class);
	}
	
	public void requestVoid(Method method, String endpoint, JsonObject data) throws JPantryRejectedException
	{
		requestBody(method, endpoint, data);
	}
	
	private String requestBody(Method method, String endpoint, JsonObject data) throws JPantryRejectedException
	{
		try
		{
			String url = generatePantryApiUrl(token.toString(), endpoint);
			String dataString = Optional.ofNullable(data).map(JsonObject::toString).orElse(null);
			Response response = requestResponse(method, dataString, url);
			return response.body();
		}
		catch (JPantryRejectedException e)
		{
			throw e;
		}
		catch (JPantryException e)
		{
			String censoredToken = "*".repeat(token.toString().length());
			String url = generatePantryApiUrl(censoredToken, endpoint);
			throw new JPantryException("Failed to request " + url, e);
		}
	}
	
	private Response requestResponse(Method method, String data, String url) throws JPantryRejectedException
	{
		// System.out.println(method + " " + url);
		try
		{
			Connection connection = Jsoup.connect(url);
			connection.method(method);
			connection.followRedirects(true);
			connection.header("Content-Type", "application/json");
			connection.ignoreContentType(true);
			connection.ignoreHttpErrors(true);
			
			if (name != null)
			{
				connection.userAgent(name);
			}
			
			if (data != null)
			{
				connection.requestBody(data);
			}
			
			Response response = connection.execute();
			
			int status = response.statusCode();
			if (status == 400)
			{
				throw new JPantryRejectedException();
			}
			if (status != 200)
			{
				throw new JPantryException("Received HTTP status code " + status + " (" + response.statusMessage() + ")");
			}
			
			String contentType = response.contentType();
			if (contentType.equalsIgnoreCase("application/json"))
			{
				throw new JPantryException("receieved unexpected content type: " + contentType);
			}
			return response;
		}
		catch (IOException e)
		{
			throw new JPantryException("Network/connection failure to request " + url, e);
		}
	}
}
