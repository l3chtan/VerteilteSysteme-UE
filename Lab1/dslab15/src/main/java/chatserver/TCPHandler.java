package chatserver;

import java.io.BufferedReader;
import java.io.PrintWriter;
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

	
	public TCPHandler(Socket soc){
		this.soc = soc;
		user = null;
	}
	
	public void close() throws IOException{
		soc.close();
	}

	public void run(){
		
		try {
			InputStreamReader in = new InputStreamReader(soc.getInputStream());
			BufferedReader bufIn = new BufferedReader(in);
			
			OutputStreamWriter out = new OutputStreamWriter(soc.getOutputStream());
			PrintWriter wr = new PrintWriter(out,true);
			
			String line = "";
			Config registry = new Config("user");
			String msg = "You must be logged in first: !login <username> <password>";
				
			do{
				line = bufIn.readLine();
				if(line == null) {
					System.out.println("Client "+soc.getInetAddress()+":"+soc.getPort()+" has terminated the connection");
					if(user != null){
						user.setOnline(false);
					}
					if(!soc.isClosed()){
						soc.close();
					}
					return;
				}
				String head = "", body = "";
				if(line.contains(" ")){
					head = line.substring(0,line.indexOf(' '));
					body = line.substring(line.indexOf(' ')+1);
				} else {
					head = line;
				}
				String subs[];
				switch(head) {
					case "!login":
						subs = body.split("\\s");
						user = getUser(subs[0]);
						if(user == null) System.out.println("user is null");
						if( user == null || this.user.getPassword() != Integer.parseInt(subs[1])) {
							msg = "Wrong username or password.";
						} else {
							user.setSocket(soc);
							user.setOnline(true);
							user.setWriter(wr);
							msg = "Successfully logged in.";
						}
						break;
					case "!send":
						for(User u: getOnline()){
							if(u != user){
								u.write(user.getName()+": "+body);
							}
						}
						break;

					case "!register":
						registry.setProperty(user+".registry", body);
						user.setRegistry(body);
						msg = "Successfully registered address for "+user.getName();
						break;

					case "!lookup": 
						msg = getUser(body).getRegistry();
						break;

					case "!logout":
						user.setOnline(false);
						System.out.println("online: "+user.isOnline());
						msg = "Successfully logged out.";
						line = null;
						break;

					default:
						break; //maybe breaks loop
				}
				if(!head.startsWith("!send")){
					if(user != null){
						user.write(msg);
					} else {
						wr.println(msg);
					}
				}

			} while(line != null); //maybe use while(true) ?
/*			} else {
				wr.write("You must be logged in first: !login <username> <password>");
			}*/
				out.close();
				in.close();
				soc.close();
		} catch (IOException e) {
				System.out.println(e.getClass().getSimpleName() +": "+  e.getMessage());
		} finally {
			
			if(soc != null && !soc.isClosed())
				try {
					soc.close();
				} catch (IOException e1) {
					System.out.println(e1.getClass().getSimpleName() +": "+  e1.getMessage());
				}
		}
	}
}
