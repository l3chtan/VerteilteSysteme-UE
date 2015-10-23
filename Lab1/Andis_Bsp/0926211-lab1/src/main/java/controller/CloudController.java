package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListMap;

import util.Config;
import cli.Command;
import cli.Shell;

public class CloudController implements ICloudControllerCli, Runnable {

	private String componentName;
	private Config config;
	private InputStream userRequestStream;
	private PrintStream userResponseStream;
	
	private Shell shell;
	
	private DatagramSocket datagramSocket;
	private ServerSocket serverSocket;
	
	private NodeHandler nodeHandler;
	
	private ConcurrentSkipListMap<String, User> users;

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
	public CloudController(String componentName, Config config,
			InputStream userRequestStream, PrintStream userResponseStream) {
		this.componentName = componentName;
		this.config = config;
		this.userRequestStream = userRequestStream;
		this.userResponseStream = userResponseStream;
		
		users = new ConcurrentSkipListMap<String, User>();
		Config userConfig = new Config("user");
		ArrayList<String> userList = new ArrayList<String>();
		for(String u: userConfig.listKeys()) {
			String[] parts = u.split("\\.");
			if(!userList.contains(parts[0])) {
				userList.add(parts[0]);
			}
		}

		for(String u: userList) {
			User user = new User(
						u,
						userConfig.getString(u + ".password"),
						userConfig.getInt(u + ".credits")
					);
			users.put(u, user);
		}
		
		shell = new Shell(this.componentName, this.userRequestStream, this.userResponseStream);
		shell.register(this);

		nodeHandler = new NodeHandler(config.getInt("node.checkPeriod"), config.getInt("node.timeout"));
		nodeHandler.start();
	}

	@Override
	public void run() {		
		try {
			serverSocket = new ServerSocket(config.getInt("tcp.port"));
			new ClientListener(serverSocket, users, nodeHandler).start();
			datagramSocket = new DatagramSocket(config.getInt("udp.port"));
			new NodeListener(datagramSocket, nodeHandler).start();
		} catch (IOException e) {
			System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
		}
		
		new Thread(shell).start();
		System.out.println("Server is up");
		
		
	}

	@Override
	@Command
	public String nodes() throws IOException {
		return nodeHandler.getNodes();
	}

	@Override
	@Command
	public String users() throws IOException {
		String output = "";
		//ArrayList<String> userList = new ArrayList<String>();
		for(User u: users.values()) {
			output += u.toString() + "\n";
		}
		
		return output;
	}

	@Override
	@Command
	public String exit() throws IOException {
		serverSocket.close();
		datagramSocket.close();
		shell.close();
		nodeHandler.stopTimeoutCheck();
		return "Shutdown complete";
	}

	/**
	 * @param args
	 *            the first argument is the name of the {@link CloudController}
	 *            component
	 */
	public static void main(String[] args) {
		CloudController cloudController = new CloudController(args[0],
				new Config("controller"), System.in, System.out);
		
		cloudController.run();
	}

}
