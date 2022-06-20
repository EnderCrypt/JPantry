package endercrypt.library.jpantry.util;


import endercrypt.library.jpantry.PantryCore;

import java.util.Objects;
import java.util.function.Supplier;


/**
 * simple utility class for holding a instance of anything in for a limited
 * period of time
 * 
 * @author EnderCrypt
 *
 * @param <T>
 *     type of class cached
 */
public class PantryCache<T>
{
	private final PantryCore core;
	private final Supplier<T> supplier;
	
	private Long time = null;
	private T value = null;
	
	public PantryCache(PantryCore core, Supplier<T> supplier)
	{
		this.core = Objects.requireNonNull(core, "core");
		this.supplier = Objects.requireNonNull(supplier, "supplier");
	}
	
	public synchronized void reset()
	{
		time = null;
	}
	
	public boolean isExpired()
	{
		return time == null || (System.currentTimeMillis() - core.getCacheTime() > time);
	}
	
	public synchronized void set(T value)
	{
		this.time = System.currentTimeMillis();
		this.value = value;
	}
	
	public synchronized T get()
	{
		if (isExpired())
		{
			set(supplier.get());
		}
		return value;
	}
	
	public T getRaw()
	{
		return value;
	}
}
