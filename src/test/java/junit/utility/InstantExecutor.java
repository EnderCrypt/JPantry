package junit.utility;


import java.util.concurrent.Executor;


public class InstantExecutor implements Executor
{
	@Override
	public void execute(Runnable command)
	{
		command.run();
	}
}
