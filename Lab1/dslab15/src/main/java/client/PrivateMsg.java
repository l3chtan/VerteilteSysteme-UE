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
  
  public void close(){
    if(!sSoc.isClosed()){
      sSoc.close();
    }
  }

  public void run(){
    sSoc = new ServerSocket(port);
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
        System.out.println(e.getClass() + ": "+e.getMessage());
      } finally {
        if(!soc.isClosed()){
          soc.close();
        }
      }
		}
  }

}
