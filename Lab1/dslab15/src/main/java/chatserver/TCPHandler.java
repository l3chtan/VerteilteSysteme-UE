package chatserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;

import util.Config;

public class TCPHandler extends Handler{
	private static ConcurrentSkipListMap<User,String> publicMsgs;
	private Socket soc;
	private User user;

	
	public TCPHandler(Socket soc/*, HashMap<String, User> users*/){
//		super(users);
		this.soc = soc;
	}

	public void run(){
		
		try {
			InputStreamReader in = new InputStreamReader(soc.getInputStream());
			BufferedReader bufIn = new BufferedReader(in);
			
			OutputStreamWriter out = new OutputStreamWriter(soc.getOutputStream());
			BufferedWriter bufOut = new BufferedWriter(out);
			
			String line = bufIn.readLine(), passwd = "";

			if(line.substring(0, line.indexOf(' ')).equals("!login")){
				//TODO check if user is already logged in
				user = getUser(line.substring(line.indexOf(' '),line.lastIndexOf(' ')));
				passwd = line.substring(line.lastIndexOf(' '));

				if( user == null || this.user.getPassword() != Integer.parseInt(passwd)) {
					bufOut.write("Wrong username or password.");
					out.close();
					in.close();
					soc.close();
					return;
				}
				
				bufOut.write("Successfully logged in.");
				
			//does buffered reader return null if there is nothing coming from the stream?
				do{
					//TODO send messages to client (or maybe later?) and probably use ObjectInputStream and ObjectOutputStream
					
					
					line = bufIn.readLine();
					String head = line.substring(0,line.indexOf(' '));
					String body = line.substring(line.indexOf(' ')+1);
					
					Config registry = new Config("user");
					
					 switch(head) {
					 case "!send":
						 
						 publicMsgs.put(this.user,body);
						 break;
					 case "!register":
						 registry.setProperty(user+".registry", body);
						 bufOut.write("Successfully registered address for "+user);
						 break;
					 case "!lookup": 
						 String ipPort = registry.getString(body+".registry");
						 bufOut.write(ipPort);
						 break;
					 case "!logout":
						 bufOut.write("Successfully logged out.");
						 line = null;
						 break;
					 default:
						 break; //maybe breaks loop
					}

				} while(line != null); //maybe use while(true) ?

			} else {
				bufOut.write("You must be logged in first: !login <username> <password>");
			}
				out.close();
				in.close();
				soc.close();


			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
