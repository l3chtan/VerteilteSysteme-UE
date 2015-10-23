package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

import cli.Command;
import cli.Shell;
import util.Config;

public class Client implements IClientCli, Runnable {

	private String componentName;
	private Config config;
	private InputStream userRequestStream;
	private PrintStream userResponseStream;
	
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private Shell shell;

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
	public Client(String componentName, Config config,
			InputStream userRequestStream, PrintStream userResponseStream) {
		this.componentName = componentName;
		this.config = config;
		this.userRequestStream = userRequestStream;
		this.userResponseStream = userResponseStream;

		shell = new Shell(this.componentName, this.userRequestStream, this.userResponseStream);
		shell.register(this);
	}

	@Override
	public void run() {
		try {
			socket = new Socket(config.getString("controller.host"), config.getInt("controller.tcp.port"));
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
			
		} catch (IOException e) {
			System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
		}
		new Thread(shell).start();
	}

	@Override
	@Command
	public String login(String username, String password) {
		writer.println("!login "+ username + " " + password);
		try {
			return reader.readLine();
		} catch (IOException e) {
			return e.getClass().getSimpleName() + ": " + e.getMessage();
		}
	}

	@Override
	@Command
	public String logout() {
		writer.println("!logout");
		try {
			return reader.readLine();
		} catch (IOException e) {
			return e.getClass().getSimpleName() + ": " + e.getMessage();
		}
	}

	@Override
	@Command
	public String credits() {
		writer.println("!credits");
		try {
			return reader.readLine();
		} catch (IOException e) {
			return e.getClass().getSimpleName() + ": " + e.getMessage();
		}
	}

	@Override
	@Command
	public String buy(long credits) {
		writer.println("!buy " + credits);
		try {
			return reader.readLine();
		} catch (IOException e) {
			return e.getClass().getSimpleName() + ": " + e.getMessage();
		}
	}

	@Override
	@Command
	public String list() {
		writer.println("!list");
		try {
			return reader.readLine();
		} catch (IOException e) {
			return e.getClass().getSimpleName() + ": " + e.getMessage();
		}
	}

	@Override
	@Command
	public String compute(String term) {
		writer.println("!compute " + term);
		try {
			return reader.readLine();
		} catch (IOException e) {
			return e.getClass().getSimpleName() + ": " + e.getMessage();
		}
	}

	@Override
	@Command
	public String exit() {
		logout();
		try {
			writer.close();
			reader.close();
			if(socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
		}
		shell.close();
		return "bye";
	}

	/**
	 * @param args
	 *            the first argument is the name of the {@link Client} component
	 */
	public static void main(String[] args) {
		Client client = new Client(args[0], new Config("client"), System.in,
				System.out);
		client.run();
	}

	// --- Commands needed for Lab 2. Please note that you do not have to
	// implement them for the first submission. ---

	@Override
	public String authenticate(String username) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
