package client;

import java.io.BufferedReader;
import java.io.IOException;

import cli.Shell;

public class TCPReader extends Thread {

	private BufferedReader reader;
	private String lstMsg;
	private boolean flag;
	private Shell shell;
	private String msg;

	public TCPReader(BufferedReader reader, Shell shell){
		this.reader = reader;
		this.shell = shell;

		lstMsg = "No message received!";
		flag = false;
		msg = "";
	}
	
	public String getLstMsg(){
		return lstMsg;
	}
	
	public void setFlag(boolean fl){
		flag = fl;
	}
	
	private synchronized void readMsg() throws InterruptedException{
		while(flag){
			System.out.println("wait");
			wait();
		}
			System.out.println("NOwait");
		try {
			msg = reader.readLine();
			if(msg == null) return;
			System.out.println("reader: "+msg);
			shell.writeLine(msg);
			if(msg.contains(":")){
				lstMsg = msg;
			}
			notify();
		} catch (IOException e) {
			System.out.println(e.getClass().getSimpleName() + ": "+e.getMessage());
			return;
		}
	}
	
	public synchronized String getMsg(){
		return msg;
	}
	
	public void run(){
		while(true){
				try {
					readMsg();
					sleep(500);
				} catch (InterruptedException e) {
					System.out.println(e.getClass().getSimpleName() + ": "+e.getMessage());
				}
		}
	}

}
