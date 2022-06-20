package endercrypt.library.jpantry;


import java.util.concurrent.Executor;


/**
 * assistance class for most of JPantry, contains lots of useful
 * information/classes required for all to work shouldnt be accessible by the
 * public
 * 
 * @author EnderCrypt
 */
public class PantryCore
{
	private final Executor executor;
	private final JPantryAgent agent;
	private final int cacheTime;
	
	public PantryCore(JPantry.Builder builder)
	{
		this.executor = builder.getExecutor();
		this.agent = new JPantryAgent(builder.getToken(), builder.getName());
		this.cacheTime = builder.getCacheTime();
	}
	
	public Executor getExecutor()
	{
		return executor;
	}
	
	public JPantryAgent getAgent()
	{
		return agent;
	}
	
	public int getCacheTime()
	{
		return cacheTime;
	}
}
