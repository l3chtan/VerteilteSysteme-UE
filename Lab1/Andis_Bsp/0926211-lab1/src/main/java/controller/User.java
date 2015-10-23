package controller;

public class User {

	private String name;
	private String password;
	private int credits;
	private Boolean online;
	
	public User(String name, String password, int credits) {
		this.name = name;
		this.password = password;
		this.credits = credits;
		online = false;
	}
	
	public synchronized Boolean login(String name, String password) {
		
		if(this.name.equals(name) && this.password.equals(password) && online == false) {
			online = true;
			return true;
		}
		return false;
	}
	
	public synchronized Boolean isLoggedIn() {
		return online;
	}
	
	public synchronized void logout() {
		online = false;
	}
	
	public synchronized int getCredits() {
		return credits;
	}

	public synchronized void increaseCredits(int increase) {
		credits += increase;
	}
	
	public synchronized void decreaseCredits(int decrease) {
		credits -= decrease;
	}
	
	public synchronized String toString() {
		String output = name + " ";
		if(online) {
			output += "online ";
		} else {
			output += "offline ";
		}
		output += "Credits: " + credits;
		return output;
	}
	
}
