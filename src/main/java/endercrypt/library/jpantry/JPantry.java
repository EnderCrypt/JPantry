package endercrypt.library.jpantry;


import endercrypt.library.jpantry.exception.JPantryAuthenticationException;
import endercrypt.library.jpantry.exception.JPantryException;
import endercrypt.library.jpantry.exception.JPantryRejectedException;
import endercrypt.library.jpantry.tasks.PantryTask;
import endercrypt.library.jpantry.tasks.VoidPantryTask;
import endercrypt.library.jpantry.util.PantryCache;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jsoup.Connection.Method;

import com.google.gson.JsonObject;


/**
 * Main public api class for accessing https://getpantry.cloud/ An instance can
 * be constructed by creating an instance of the {@link JPantry.Builder} and
 * giving it a token token
 * 
 * @author EnderCrypt
 */
public class JPantry
{
	public static String getDashboardUrl(String token)
	{
		return "https://getpantry.cloud/?show=dashboard&pantryid=" + token;
	}
	
	private final PantryCore core;
	
	private final PantryBasketCollection collection;
	
	private final PantryCache<PantryDetails> cachedDetails;
	
	private JPantry(PantryCore core)
	{
		this.core = core;
		
		this.collection = new PantryBasketCollection(core);
		
		this.cachedDetails = new PantryCache<>(core, () -> {
			
			JsonObject json = core.getAgent().requestJson(Method.GET, null, null);
			PantryDetails details = new PantryDetails(JPantry.this, json);
			collection.sync(details);
			return details;
			
		});
		
		// login attempt
		try
		{
			getDetails().complete();
		}
		catch (JPantryRejectedException e)
		{
			throw new JPantryAuthenticationException();
		}
	}
	
	public PantryTask<PantryDetails> getDetails()
	{
		return new PantryTask<PantryDetails>(core)
		{
			@Override
			protected PantryDetails perform() throws JPantryException
			{
				return cachedDetails.get();
			}
		};
	}
	
	public VoidPantryTask setInformation(String name, String description)
	{
		return new VoidPantryTask(core)
		{
			@Override
			protected Void perform() throws JPantryException
			{
				JsonObject data = new JsonObject();
				data.addProperty("name", name);
				data.addProperty("description", description);
				JsonObject response = core.getAgent().requestJson(Method.PUT, null, data);
				cachedDetails.set(new PantryDetails(JPantry.this, response));
				return null;
			}
		};
	}
	
	/**
	 * instantly (and without doing any network) generates a local instance of a
	 * {@link PantryBasket} which points at a basket in the pantry cloud, this
	 * can then be used to set, update, get and clear the json value stored on
	 * it
	 * 
	 * @param name
	 *     of the basket
	 * @return the basket instance
	 */
	public PantryBasket getBasket(String name)
	{
		return collection.fetch(name);
	}
	
	/**
	 * Class for building an instance of {@link JPantry} only token is required,
	 * once done you can call {@link #login()} to get the instance. as pantry
	 * lacks any kind of api authentication check, the library will immidietly
	 * fetch the details of the library which also gives us a chance to check if
	 * the provided token was valid. if token is invalid, then
	 * {@link endercrypt.library.jpantry.exception.JPantryAuthenticationException}
	 * will be thrown
	 * 
	 * @author EnderCrypt
	 */
	public static class Builder
	{
		// NAME //
		
		private String name = "EnderCrypt/JPantry";
		
		public Builder setName(String name)
		{
			this.name = name;
			return this;
		}
		
		public Builder disableName()
		{
			setName(null);
			return this;
		}
		
		public String getName()
		{
			return name;
		}
		
		// TOKEN //
		
		private UUID token = null;
		
		public Builder setToken(String token)
		{
			Objects.requireNonNull(token, "token");
			setToken(UUID.fromString(token));
			return this;
		}
		
		public Builder setToken(UUID token)
		{
			this.token = Objects.requireNonNull(token, "token");
			return this;
		}
		
		public UUID getToken()
		{
			if (token == null)
			{
				throw new IllegalArgumentException("Token missing");
			}
			return token;
		}
		
		// EXECUTOR //
		
		private Executor executor = null;
		
		public Builder setExecutor(Executor executor)
		{
			this.executor = executor;
			return this;
		}
		
		public Executor getExecutor()
		{
			if (executor == null)
			{
				executor = Executors.newCachedThreadPool();
			}
			return executor;
		}
		
		// CACHE //
		
		private int cacheTime = 1_000;
		
		public Builder setCacheTime(int cacheTime)
		{
			this.cacheTime = cacheTime;
			return this;
		}
		
		public Builder disableCache()
		{
			setCacheTime(0);
			return this;
		}
		
		public int getCacheTime()
		{
			if (cacheTime < 0)
			{
				throw new IllegalArgumentException("cache time cannot be " + cacheTime);
			}
			return cacheTime;
		}
		
		// LOGIN //
		
		public JPantry login()
		{
			return new JPantry(new PantryCore(this));
		}
	}
}
