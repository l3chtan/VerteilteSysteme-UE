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
	
	private UDPClient udpCon;
	private TCPReader tcpRead;
	private PrivateMsg pMsg;
	
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
	}

	@Override
	public void run() {
		new Thread(shell).start();
	}

	@Override
	@Command
	public String login(String username, String password) throws IOException {

		String host = config.getString("chatserver.host");
		int port = config.getInt("chatserver.tcp.port");
		
		try {
			socket = new Socket(host,port);
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			reader = new BufferedReader(new InputStreamReader(in));
			writer = new PrintWriter(new OutputStreamWriter(out),true);
			
			tcpRead = new TCPReader(reader,shell);

		} catch (IOException e) {
			System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
		}
			String str = "";
			writer.println("!login " + username + " " + password);
			
			str = reader.readLine();
		if(str.startsWith("Wrong")){
			socket.close();
			return null;
		} 
			tcpRead.start();
		return str;
	}

	@Override
	@Command
	public String logout() throws IOException {
		writer.println("!logout\n");
		pMsg.close();
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
			udpCon = new UDPClient(config.getString("chatserver.host"),config.getInt("chatserver.udp.port"),shell.getOut());
			udpCon.start();
		return null;
	}

	@Override
	@Command
	public String msg(String username, String message) throws IOException {
		String ans = "";
		tcpRead.setFlag(true);
		writer.println("!lookup "+username);

		ans = tcpRead.getMsg();
		System.out.println("hallo");
		tcpRead.setFlag(false);

		String subs[] = ans.split(":");
		System.out.println(subs[0]+" : "+subs[1]);
		Socket soc = new Socket(subs[0],Integer.parseInt(subs[1]));
		
		InputStreamReader in = new InputStreamReader(soc.getInputStream());
		BufferedReader bufIn = new BufferedReader(in);
			
		OutputStreamWriter out = new OutputStreamWriter(soc.getOutputStream());
		PrintWriter wr = new PrintWriter(out,true);
		
		wr.println(message);
		shell.writeLine(bufIn.readLine());
		soc.close();
		return null;
	}

	@Override
	@Command
	public String lookup(String username) throws IOException {
		System.out.println("lookup");
		writer.println("!lookup "+username);
		return null;
	}

	@Override
	@Command
	public String register(String privateAddress) throws IOException {
		
		String subs[] = privateAddress.split(":");
		pMsg = new PrivateMsg(Integer.parseInt(subs[1]),shell);
		pMsg.start();
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
		pMsg.close();
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
