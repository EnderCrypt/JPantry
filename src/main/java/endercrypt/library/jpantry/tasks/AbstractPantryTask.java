package endercrypt.library.jpantry.tasks;


import endercrypt.library.jpantry.PantryCore;
import endercrypt.library.jpantry.exception.JPantryException;

import java.util.Objects;
import java.util.function.Consumer;


/**
 * This class represents a (usually) network task who will probablly return
 * something {@link PantryTask} or wont return anything {@link VoidPantryTask}
 * 
 * when an action is called on {@link endercrypt.library.jpantry.JPantry} or
 * {@link endercrypt.library.jpantry.PantryBasket} that returns either of these,
 * the request will NOT do anything on its own. you must call either .complete
 * (blocking) or .queue (async) to actually initialize and start the network
 * request in whichever way you preffer (if you dont know which one to use, use
 * .complete).
 * 
 * @author EnderCrypt
 *
 * @param <T>
 *     return value
 */
public abstract class AbstractPantryTask<T>
{
	protected final PantryCore core;
	
	public AbstractPantryTask(PantryCore core)
	{
		this.core = Objects.requireNonNull(core, "core");
	}
	
	protected abstract T perform() throws JPantryException;
	
	protected void asyncPerform(Consumer<T> onSuccess, Consumer<JPantryException> onFail)
	{
		core.getExecutor().execute(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					T data = perform();
					if (onSuccess != null)
					{
						onSuccess.accept(data);
					}
				}
				catch (JPantryException e)
				{
					if (onFail != null)
					{
						onFail.accept(e);
					}
				}
			}
		});
	}
}
