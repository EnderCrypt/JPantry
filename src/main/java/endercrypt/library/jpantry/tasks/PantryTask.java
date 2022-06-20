package endercrypt.library.jpantry.tasks;


import endercrypt.library.jpantry.PantryCore;
import endercrypt.library.jpantry.exception.JPantryException;

import java.util.function.Consumer;


/**
 * see {@link AbstractPantryTask} for more info
 * 
 * @author EnderCrypt
 * @param <T>
 *     result value from the request
 */
public abstract class PantryTask<T> extends AbstractPantryTask<T>
{
	public PantryTask(PantryCore core)
	{
		super(core);
	}
	
	public T complete()
	{
		return perform();
	}
	
	public void queue()
	{
		asyncPerform(null, null);
	}
	
	public void queue(Consumer<T> onSuccess)
	{
		asyncPerform(onSuccess, null);
	}
	
	public void queue(Consumer<T> onSuccess, Consumer<JPantryException> onFail)
	{
		asyncPerform(onSuccess, onFail);
	}
}
