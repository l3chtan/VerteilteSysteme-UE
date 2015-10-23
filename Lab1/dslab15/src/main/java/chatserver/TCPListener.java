package chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by et on 16.10.15.
 */
public class TCPListener extends Listener {
	
	private ServerSocket sSoc;
	private ExecutorService pool;
	
	public TCPListener(int port) throws IOException{
		sSoc = new ServerSocket(port);
		
		pool = Executors.newCachedThreadPool();
	}
	
	public void run(){
		
		try{
			while(true){
				pool.execute(new TCPHandler(sSoc.accept()));
			}

		} catch(IOException e) {
			pool.shutdown();
		}
	}
	
	@Override
	public void close() throws IOException{
		sSoc.close();
	}
}
