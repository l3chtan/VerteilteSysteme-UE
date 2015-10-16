package chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by et on 16.10.15.
 */
public class TCPListener extends Listener {
	
	private ServerSocket sSoc;
	private TCPConnection con;
	
	public TCPListener(int port) throws IOException{
		sSoc = new ServerSocket(port);
		con = new TCPConnection();
	}
	
	public void run(){
		Socket soc = null;
		
		while(true){
			try{
				soc = sSoc.accept();
				con.setSoc(soc);
				con.start();

			} catch(Exception e) {
				
			}
		}
	}
}
