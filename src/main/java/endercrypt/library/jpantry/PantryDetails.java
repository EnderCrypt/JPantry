package endercrypt.library.jpantry;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.gson.JsonObject;


/**
 * immutable details and information about JPantry, it is not recommended that
 * you hold on to an instance of this as the information may become quickly
 * obsolete
 * 
 * @author EnderCrypt
 */
public class PantryDetails
{
	private final String name;
	private final String description;
	private final List<String> errors;
	private final boolean notifications;
	private final int percentFull;
	private final Set<PantryBasket> baskets;
	
	public PantryDetails(JPantry pantry, JsonObject json)
	{
		this.name = json.get("name").getAsString();
		
		this.description = json.get("description").getAsString();
		
		this.errors = StreamSupport.stream(json.get("errors").getAsJsonArray().spliterator(), false)
			.map(j -> j.getAsString())
			.collect(Collectors.toUnmodifiableList());
		
		this.notifications = json.get("notifications").getAsBoolean();
		
		this.percentFull = json.get("percentFull").getAsInt();
		
		this.baskets = StreamSupport
			.stream(json.get("baskets").getAsJsonArray().spliterator(), false)
			.map(j -> j.getAsJsonObject().get("name").getAsString())
			.map(name -> pantry.getBasket(name))
			.collect(Collectors.toUnmodifiableSet());
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public List<String> getErrors()
	{
		return errors;
	}
	
	public boolean isNotifications()
	{
		return notifications;
	}
	
	public int getPercentFull()
	{
		return percentFull;
	}
	
	public Set<PantryBasket> getBaskets()
	{
		return baskets;
	}
}
