package node;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import util.Config;

public class Logger {
	
	private String nodeId;
	private String dir;
	
	private ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {
		
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyyMMdd_HHmmss.SSS");
		}
	};
	
	public Logger(String nodeId, Config config) {
		this.nodeId = nodeId;
		dir = config.getString("log.dir");
	}
	
	public void log(String details) {
		
		try {
			FileWriter writer = new FileWriter(dir + "/" + dateFormat.get().format(new Date()) + "_" + nodeId + ".log", false);
			writer.write(details);
			writer.close();
		} catch (IOException e) {
			System.out.println("Could not create log file.");
		}
		
	}
	
	
	
}
