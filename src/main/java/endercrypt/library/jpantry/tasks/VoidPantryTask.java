package endercrypt.library.jpantry.tasks;


import endercrypt.library.jpantry.PantryCore;
import endercrypt.library.jpantry.exception.JPantryException;

import java.util.function.Consumer;


/**
 * see {@link AbstractPantryTask} for more info
 * 
 * @author EnderCrypt
 */
public abstract class VoidPantryTask extends AbstractPantryTask<Void>
{
	public VoidPantryTask(PantryCore core)
	{
		super(core);
	}
	
	public void complete()
	{
		perform();
	}
	
	public void queue()
	{
		asyncPerform(null, null);
	}
	
	public void queue(Runnable callback)
	{
		asyncPerform((v) -> callback.run(), null);
	}
	
	public void queue(Consumer<JPantryException> onFail)
	{
		asyncPerform(null, onFail);
	}
}
