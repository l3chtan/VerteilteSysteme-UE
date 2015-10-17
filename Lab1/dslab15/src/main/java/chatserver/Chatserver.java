package chatserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

import util.Config;

public class Chatserver implements IChatserverCli, Runnable {

	private String componentName;
	private Config config;
	private InputStream userRequestStream;
	private PrintStream userResponseStream;

	private HashMap<String,Integer> users;

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

		initList();

		// TODO
	}

	private void initList() {
		Config cfg = new Config("list");
		for(String s: config.listKeys()) {
			cfg.setProperty(s, "offline");
		}
	}

	@Override
	public void run() {
		Listener tcpListen = null, udpListen = null;
		try {
			tcpListen = new TCPListener(config.getInt("tcp.port"));
			udpListen = new UDPListener(config.getInt("udp.port"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tcpListen.start();
		udpListen.start();
	}

	@Override
	public String users() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exit() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	private void getUserConfig(Config config) {
		users = new HashMap<>();
		for(String s:config.listKeys()){
			users.put(s.substring(0,s.lastIndexOf(".")),config.getInt(s));
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
		chatserver.getUserConfig(new Config("user"));
		// TODO: start the chatserver
	}
}
