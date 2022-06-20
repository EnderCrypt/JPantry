package endercrypt.library.jpantry;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class PantryBasketCollection
{
	private final PantryCore core;
	private final Map<String, PantryBasket> baskets = new HashMap<>();
	
	public PantryBasketCollection(PantryCore core)
	{
		this.core = Objects.requireNonNull(core, "core");
	}
	
	public synchronized void sync(PantryDetails details)
	{
		baskets.values().removeIf(basket -> details.getBaskets().contains(basket) == false);
	}
	
	public synchronized PantryBasket fetch(String name)
	{
		return baskets.computeIfAbsent(name, this::createRawBasket);
	}
	
	private PantryBasket createRawBasket(String name)
	{
		return new PantryBasket(core, name);
	}
}
