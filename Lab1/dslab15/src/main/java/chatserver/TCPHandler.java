package chatserver;

import java.io.IOException;
import java.net.Socket;

public class TCPHandler extends Thread{
	private Socket soc;
	
	public TCPHandler(Socket soc){
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
