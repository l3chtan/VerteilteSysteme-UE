package chatserver;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListMap;

public/* abstract*/ class Handler extends Thread{
	
	private static ConcurrentSkipListMap<String,User> users;
	
	public Handler(){}
	public Handler(ConcurrentSkipListMap<String,User> users){
		Handler.users = users;
	}
	
	protected void setOnline(String user, boolean val){
		if(!users.containsKey(user)) users.get(user).setOnline(val);
	}
	
	protected ArrayList<User> getOnline(){
		ArrayList<User> online = new ArrayList<User>();
		for(User u: users.values()){
			if(u.isOnline()) online.add(u);
		}
		return online;
	}
	
	protected User getUser(String u){
		if(users.containsKey(u))
			return users.get(u);
		return null;
	}
}
