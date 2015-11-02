package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
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
	private DatagramSocket dataSocket;
	
	private TCPClient tcpCon;
	private UDPClient udpCon;
	
	private BufferedReader reader;
	private BufferedWriter writer;
	
	private Shell shell;
	
	private String lastMsg;

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
		

		shell = new Shell(componentName,userRequestStream,userResponseStream);
		shell.register(this);
		
		lastMsg = null;
		
		// TODO
	}

	@Override
	public void run() {
		try {
			tcpCon = new TCPClient(config.getString("chatserver.host"),config.getInt("chatserver.tcp.port"),shell.getOut());
			tcpCon.start();

			while(reader == null || writer == null){
				reader = tcpCon.getReader();
				writer = tcpCon.getWriter();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new Thread(shell).start();
		
//		while(true){
//			try {
//				shell.writeLine(reader.readLine());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		// TODO
	}

	@Override
	@Command
	public String login(String username, String password) throws IOException {
		// TODO Auto-generated method stub
		String out = "";
		tcpCon.interrupt();
		if(tcpCon.isInterrupted()){		
			writer.write("!login " + username + " " + password +"\n");
			out = reader.readLine();
		}
		return out;
	}

	@Override
	@Command
	public String logout() throws IOException {
		// TODO Auto-generated method stub
		writer.write("!logout\n");
		return reader.readLine();
	}

	@Override
	@Command
	public String send(String message) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("foo");
		writer.write("!send "+message+"\n");
		return reader.readLine();
	}

	@Override
	@Command
	public String list() throws IOException {
		// TODO Auto-generated method stub
			udpCon = new UDPClient(config.getString("chatserver.host"),config.getInt("chatserver.udp.port"),shell.getOut());
			udpCon.start();
		return null;
	}

	@Override
	@Command
	public String msg(String username, String message) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Command
	public String lookup(String username) throws IOException {
		// TODO Auto-generated method stub
		writer.write("!lookup "+username+"\n");
		return reader.readLine();
	}

	@Override
	@Command
	public String register(String privateAddress) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	@Command
	public String lastMsg() throws IOException {
		if(lastMsg != null) return lastMsg;
		return "No message received !";
	}

	@Override
	@Command
	public String exit() throws IOException {
		// TODO Auto-generated method stub
		socket.close();
		return null;
	}

	/**
	 * @param args
	 *            the first argument is the name of the {@link Client} component
	 */
	public static void main(String[] args) {
		Client client = new Client(args[0], new Config("client"), System.in,
				System.out);
		// TODO: start the client
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
