package endercrypt.library.jpantry.exception;

/**
 * Generic JPantry exception
 * 
 * @author EnderCrypt
 */
public class JPantryException extends RuntimeException
{
	private static final long serialVersionUID = 5552635776332161994L;
	
	public JPantryException(String message)
	{
		super(message);
	}
	
	public JPantryException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
