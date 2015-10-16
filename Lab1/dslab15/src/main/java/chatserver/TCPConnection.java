package chatserver;

import java.io.IOException;
import java.net.Socket;

public class TCPConnection extends Thread{
	private Socket soc;
	
	
	public void setSoc(Socket soc) {
		this.soc = soc;
	}


	public void run(){
		
		try {
			soc.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
