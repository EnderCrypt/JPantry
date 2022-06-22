package endercrypt.library.jpantry;


import endercrypt.library.jpantry.exception.JPantryException;
import endercrypt.library.jpantry.exception.JPantryRejectedException;
import endercrypt.library.jpantry.tasks.PantryTask;
import endercrypt.library.jpantry.tasks.VoidPantryTask;
import endercrypt.library.jpantry.util.PantryCache;

import java.util.Objects;

import org.jsoup.Connection.Method;

import com.google.gson.JsonObject;


/**
 * a Basket from Pantry, values may be fetched when and if needed automatically,
 * depending also on cache settings (configured in {@link JPantry.Builder})
 * 
 * 2 instances of baskets with the same name can not exist, if 2 baskets have
 * the same name, they'll be the same instance and so should be safe to compare
 * using ==
 * 
 * this can however lead to an ever increasing memory usage if you keep opening
 * new buckets and destroying them
 * 
 * @author EnderCrypt
 */
public class PantryBasket
{
	private final PantryCore core;
	private final String name;
	
	private PantryCache<JsonObject> cachedJson;
	
	public PantryBasket(PantryCore core, String name)
	{
		this.core = Objects.requireNonNull(core, "core");
		this.name = Objects.requireNonNull(name, "name");
		
		this.cachedJson = new PantryCache<>(core, () -> {
			
			try
			{
				return core.getAgent().requestJson(Method.GET, "basket/" + name, null);
			}
			catch (JPantryRejectedException e)
			{
				return null;
			}
			
		});
	}
	
	public String getName()
	{
		return name;
	}
	
	protected PantryBasket internalSetJson(JsonObject json)
	{
		cachedJson.set(json);
		
		return this;
	}
	
	public VoidPantryTask setJson(JsonObject json)
	{
		return new VoidPantryTask(core)
		{
			@Override
			protected Void perform() throws JPantryException
			{
				core.getAgent().requestVoid(Method.POST, "basket/" + name, json);
				cachedJson.set(json);
				return null;
			}
		};
	}
	
	public PantryTask<JsonObject> mergeJson(JsonObject json)
	{
		return new PantryTask<JsonObject>(core)
		{
			@Override
			protected JsonObject perform() throws JPantryException
			{
				JsonObject updatedJson = core.getAgent().requestJson(Method.PUT, "basket/" + name, json);
				internalSetJson(updatedJson);
				return updatedJson;
			}
		};
	}
	
	public PantryTask<JsonObject> getJson()
	{
		return new PantryTask<JsonObject>(core)
		{
			@Override
			protected JsonObject perform() throws JPantryException
			{
				return cachedJson.get();
			}
		};
	}
	
	/**
	 * @return true if a json was present and deleted
	 */
	public PantryTask<Boolean> deleteJson()
	{
		return new PantryTask<>(core)
		{
			@Override
			protected Boolean perform() throws JPantryException
			{
				cachedJson.reset();
				try
				{
					core.getAgent().requestVoid(Method.DELETE, "basket/" + name, null);
					return true;
				}
				catch (JPantryRejectedException e)
				{
					return false;
				}
			}
		};
	}
	
	@Override
	public String toString()
	{
		String json = cachedJson.getRaw().toString();
		if (cachedJson.isExpired())
		{
			json = "(local cache expired)";
		}
		return "PantryBasket [name=" + name + ", json=" + json + "]";
	}
}
