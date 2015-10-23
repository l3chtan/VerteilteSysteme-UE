package chatserver;

import java.io.IOException;

/**
 * Created by et on 16.10.15.
 */
abstract class Listener extends Thread{
	
	abstract public void close() throws IOException;
}
