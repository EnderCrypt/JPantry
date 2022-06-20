package endercrypt.library.jpantry.exception;

/**
 * Exception indicating that the token provided to
 * {@link endercrypt.library.jpantry.JPantry} was invalid and rejected by
 * https://getpantry.cloud/
 * 
 * @author EnderCrypt
 */
public class JPantryAuthenticationException extends JPantryException
{
	private static final long serialVersionUID = 6261648310382775819L;
	
	public JPantryAuthenticationException()
	{
		super("The provided token is invalid");
	}
}
