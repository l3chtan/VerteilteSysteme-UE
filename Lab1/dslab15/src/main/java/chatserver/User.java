package chatserver;

public class User {
	private String name, registry; 
	private int password;
	private boolean online;
	
	public String getName() {
		return name;
	}
	public String getRegistry() {
		return registry;
	}
	public void setRegistry(String registry) {
		this.registry = registry;
	}
	public int getPassword() {
		return password;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	
}
