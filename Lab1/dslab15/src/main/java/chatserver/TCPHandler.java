package chatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPHandler extends Thread{
	private Socket soc;
	private String logCmd = "!login";
	
	public TCPHandler(Socket soc){
		this.soc = soc;
	}

	public void run(){
		
		try {
			InputStreamReader in = new InputStreamReader(soc.getInputStream());
			BufferedReader buf = new BufferedReader(in);
			String line = buf.readLine(), name = "", passwd = "";

			if(line.substring(0, line.indexOf(' ')).equals("!login")){
				name = line.substring(line.indexOf(' '),line.lastIndexOf(' '));
				passwd = line.substring(line.lastIndexOf(' '));
			
/*			byte[] login = new byte[logCmd.length()];
			if(in.read(login,0,login.length) == logCmd.length() && logCmd.equals(login.toString())){
				byte[] cred = new byte[256];
				int cnt = in.read(cred);
				String credString = cred.toString();
*/
			//TODO proceed with authentication and a welcome message
			//does buffered reader return null if there is nothing coming from the stream?
				do{
					//TODO send messages to client (or maybe later?)
					line = buf.readLine();
					String head = line.substring(0,line.indexOf(' '));
					String body = line.substring(line.indexOf(' '));
					
					//TODO
					 switch(head) {
					 case "!send":
						 break;
					 case "!register":
						 break;
					 case "!lookup": 
						 break;
					 case "!msg":
						 break;
					 case "!logout":
						 break;
					 default:
						 break;
					}
//					
//					if(getCmd(line).equals("!send")){
//						//TODO
//					} else if(line.substring(0,line.indexOf(' ')).equals("!register")){
//						//TODO
//					} else if(line.substring(0,line.indexOf(' ')).equals("!lookup")){
//						//TODO
//					} else if(line.substring(0,line.indexOf(' ')).equals("!msg")){
//						//TODO
//					} else if(line.substring(0,line.indexOf(' ')).equals("!logout")){
//						//TODO
//					} else {
//						//TODO
//					}


				} while(line != null); //maybe use while(true) ?

			} else {
				//TODO maybe send a line saying "first request must be: !login <username> <password>"
				in.close();
				//TODO return to listening
			}


			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
