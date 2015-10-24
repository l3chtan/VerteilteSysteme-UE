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
//			BufferedWriter bufOut = new BufferedWriter(out);
			user.setWriter(new BufferedWriter(out));
			
			String line = bufIn.readLine(), passwd = "";

			if(line.substring(0, line.indexOf(' ')).equals("!login")){
				//TODO check if user is already logged in
				user = getUser(line.substring(line.indexOf(' '),line.lastIndexOf(' ')));
				passwd = line.substring(line.lastIndexOf(' '));

				if( user == null || this.user.getPassword() != Integer.parseInt(passwd)) {
					user.write("Wrong username or password.");
					out.close();
					in.close();
					soc.close();
					return;
				}
				user.setOnline(true);
				user.write("Successfully logged in.");
				
			//does buffered reader return null if there is nothing coming from the stream?
				do{
					line = bufIn.readLine();
					String head = line.substring(0,line.indexOf(' '));
					String body = line.substring(line.indexOf(' ')+1);
					
					Config registry = new Config("user");
					
					switch(head) {
						case "!send":
							for(User u: getOnline()){
								u.write(body);
							}
							break;

						case "!register":
							registry.setProperty(user+".registry", body);
							user.write("Successfully registered address for "+user);
							break;

						case "!lookup": 
							String ipPort = registry.getString(body+".registry");
							user.write(ipPort);
							break;

						case "!logout":
							user.setOnline(false);
							user.write("Successfully logged out.");
							line = null;
							break;

						default:
							break; //maybe breaks loop
					}

				} while(line != null); //maybe use while(true) ?

			} else {
				user.write("You must be logged in first: !login <username> <password>");
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
