package client;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
	private ServerSocket sSoc;
	
	private TCPClient tcpCon;
	private UDPClient udpCon;
	private TCPReader tcpRead;
	private boolean listening;
	
	private BufferedReader reader;
	private PrintWriter writer;
	
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
		listening = false;
		
		// TODO
	}

	@Override
	public void run() {
		
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
/*	
	private void listen(){
		String msg ="";
		while(listening){
			try {
				msg = reader.readLine();
				shell.writeLine(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
	}
	
	private String doIO(String msg){
		String str = "";
		try {
			tcpRead.interrupt();
			if(tcpRead.isInterrupted()){
			if(msg != null){
				writer.println(msg);
			}
				str = reader.readLine();
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tcpRead.start();
		System.out.println("everything went fine");
		return str;
	}*/

	@Override
	@Command
	public String login(String username, String password) throws IOException {
		// TODO Auto-generated method stub

//		tcpCon = new TCPClient(config.getString("chatserver.host"),config.getInt("chatserver.tcp.port"),shell.getOut());
//		tcpCon.start();
		String host = config.getString("chatserver.host");
		int port = config.getInt("chatserver.tcp.port");
		
		
		try {
			socket = new Socket(host,port);
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			reader = new BufferedReader(new InputStreamReader(in));
			writer = new PrintWriter(new OutputStreamWriter(out),true);
			
			tcpRead = new TCPReader(reader,shell.getOut());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			String str = "";
			writer.println("!login " + username + " " + password);
			
			str = reader.readLine();
		if(str.startsWith("Wrong")){
//			tcpCon.close();
			socket.close();
			return null;
		} 
//		else {
//			listening = true;
//			listen();
//		}
			tcpRead.start();
		return str;
	}

	@Override
	@Command
	public String logout() throws IOException {
		// TODO Auto-generated method stub
		writer.println("!logout\n");
//		tcpCon.close();
		socket.close();
		return "Successfully logged out.";
	}

	@Override
	@Command
	public String send(String message) throws IOException {
		writer.println("!send "+message);
		return null;
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
		String subs[] = lookup(username).split(":");
		Socket soc = new Socket(subs[0],Integer.parseInt(subs[1]));
		return null;
	}

	@Override
	@Command
	public String lookup(String username) throws IOException {
		// TODO Auto-generated method stub
		writer.println("!lookup "+username);
		tcpRead.interrupt();
		String addr = reader.readLine();
		return addr;
	}

	@Override
	@Command
	public String register(String privateAddress) throws IOException {
		// TODO Auto-generated method stub
		sSoc.
		writer.println("!register "+privateAddress);
		return null;
	}
	
	@Override
	@Command
	public String lastMsg() throws IOException {
		return tcpRead.getLstMsg();
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
