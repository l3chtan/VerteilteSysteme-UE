package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import cli.Shell;

public class PrivateMsg extends Thread {

  private ServerSocket sSoc;
  private Socket soc;
  private int port;
  private Shell shell;
  
  public PrivateMsg(int port, Shell shell){
    this.port = port;
    this.shell = shell;
    soc = null;
  }
  
  public void close() throws IOException{
    if(!sSoc.isClosed()){
      sSoc.close();
    }
  }

  public void run(){
    try {
		sSoc = new ServerSocket(port);
	} catch (IOException e1) {
		System.out.println(e1.getClass().getSimpleName() + ": " + e1.getMessage());
	}
    while(true){
      try{
		Socket soc = sSoc.accept();
		
		InputStreamReader in = new InputStreamReader(soc.getInputStream());
		BufferedReader bufIn = new BufferedReader(in);
		
		OutputStreamWriter out = new OutputStreamWriter(soc.getOutputStream());
		PrintWriter wr = new PrintWriter(out,true);
		shell.writeLine(bufIn.readLine());
		wr.println("!ack");
		soc.close();
      } catch (IOException e){
        System.out.println(e.getClass().getSimpleName() + ": "+e.getMessage());
      } finally {
        if(soc != null && !soc.isClosed()){
			try {
				soc.close();
			} catch (IOException e) {
				System.out.println(e.getClass().getSimpleName() + ": "+e.getMessage());
			}
        }
      }
	}
  }

}
