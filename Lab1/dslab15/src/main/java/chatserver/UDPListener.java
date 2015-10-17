package chatserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by et on 16.10.15.
 */
public class UDPListener extends Listener {
	
	private DatagramSocket dSoc;
	private ExecutorService pool;

	public UDPListener(int port) throws IOException {
		dSoc = new DatagramSocket(port);
		pool = Executors.newCachedThreadPool();
	}
	
	public void run(){
		try{
			while(true){
				byte[] buf = new byte[256];
				DatagramPacket dp = new DatagramPacket(buf,buf.length);
				dSoc.receive(dp);
				pool.execute(new UPDHandler(dp));
			}
		}catch(IOException e) {
			
		}
	}
}
