package chatserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import cli.Shell;
import util.Config;

public class Chatserver implements IChatserverCli, Runnable {

	private String componentName;
	private Config config;
	private InputStream userRequestStream;
	private PrintStream userResponseStream;
	
	private Shell shell;

	Listener tcpListen, udpListen;

	private ConcurrentSkipListMap<String,User> userConfig;

	/**
	 * @param componentName
	 *            the name of the component - represented in the prompt
	 * @param config
	 *            the configuration to use
	 * @param userRequestStream
	 *            the input stream to read user input from
	 * @param userResponseStream
	 *            the output stream to write the console output to
	 */
	public Chatserver(String componentName, Config config,
			InputStream userRequestStream, PrintStream userResponseStream) {
		this.componentName = componentName;
		this.config = config;
		this.userRequestStream = userRequestStream;
		this.userResponseStream = userResponseStream;
		
		tcpListen = null;
		udpListen = null;
		
		shell = new Shell(componentName,userRequestStream,userResponseStream);
		shell.register(this);

		//is this good?
		getUserConfig(new Config("User"));
		Handler handle = new Handler(userConfig);
		
		

		// TODO
	}

	@Override
	public void run() {
		try {
			tcpListen = new TCPListener(config.getInt("tcp.port"));
			udpListen = new UDPListener(config.getInt("udp.port"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tcpListen.start();
		udpListen.start();
		
		new Thread(shell).start();
	}

	@Override
	public String users() throws IOException {
		String result = "";
		Config users = new Config("User");
		for(String s: users.listKeys()){
			if(s.substring(s.lastIndexOf('.')+1).equals("password")){
				String user = s.substring(0, s.lastIndexOf('.'));
				result += String.format("%s %s\n", user,userConfig.get(user).getStatus());
			}
		}
		return result;
	}

	@Override
	public String exit() throws IOException {
		// TODO Auto-generated method stub
		tcpListen.close();
		udpListen.close();
		return null;
	}

	private void getUserConfig(Config config) {
		userConfig = new ConcurrentSkipListMap<String,User>();

		for(String s:config.listKeys()){
			String name = s.substring(0,s.lastIndexOf("."));
			String entry = s.substring(s.lastIndexOf('.'));

			if(entry.equals("password")){
				User newUser = new User(name,config.getInt(s));
				User old = userConfig.putIfAbsent(name,newUser);
				if(old != null && old.getPassword() == -1){
					newUser.setRegistry(old.getRegistry());
					userConfig.replace(name, newUser);
				}
			} else if(entry.equals("registry")){
				User u = userConfig.get(name);
				if(u == null){
					userConfig.put(name, new User(name,-1));
				}
				u.setRegistry(config.getString(s));
			}
		}
	}

	/**
	 * @param args
	 *            the first argument is the name of the {@link Chatserver}
	 *            component
	 */
	public static void main(String[] args) {
		Chatserver chatserver = new Chatserver(args[0],
				new Config("chatserver"), System.in, System.out);
		// TODO: start the chatserver
		chatserver.run();
	}
}
