package endercrypt.library.jpantry.exception;

public class JPantryRejectedException extends JPantryException
{
	private static final long serialVersionUID = -9050375792664009872L;
	
	public JPantryRejectedException()
	{
		super("Pantry rejected the request");
	}
}
