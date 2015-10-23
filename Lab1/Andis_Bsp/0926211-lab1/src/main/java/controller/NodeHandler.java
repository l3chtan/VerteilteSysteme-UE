package controller;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

public class NodeHandler extends Thread {
	
	private ConcurrentHashMap<String, Node> nodes = new ConcurrentHashMap<String, Node>();
	private int checkPeriod;
	private int timeout;
	private Boolean checkTimeout = true;
	
	public NodeHandler(int checkPeriod, int timeout) {
		this.checkPeriod = checkPeriod;
		this.timeout = timeout;
	}
	
	public void register(String name, InetAddress address, int port, String supportedOperators) {
		if(!nodes.containsKey(name)) {
			Node node = new Node(address, port, supportedOperators);
			nodes.put(name, node);
			for(int i = 0; i < supportedOperators.length(); i++) {
			}
		} else {
			nodes.get(name).update();
		}
	}
	
	public void isAlive(String name, InetAddress address, int port) {
		if(nodes.containsKey(name)) {
			nodes.get(name).update();
		}
	}
	
	public void run() {
		while(checkTimeout) {
			long currentTime = System.currentTimeMillis();
			for(Node node: nodes.values()) {
				if(node.checkTimeout(currentTime, timeout)) {
					System.out.println("Node " + node.getAddress() + " timed out.");
				};
			}
			
			try {
				Thread.sleep(checkPeriod);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
		
	}
	
	public void stopTimeoutCheck() {
		checkTimeout = false;
	}
	
	/**
	 * @return A list of all registered nodes as a String.
	 */
	public String getNodes() {
		String output = "";
		for(Node node: nodes.values()) {
			output += node.toString() + "\n";
		}
		return output;
	}
	
	/**
	 * Looks up all currently supported operators from the nodes.
	 * @return All supported operators
	 */
	public String getAllSupportedOperators() {
		String operators = "";
		
		for(Node n: nodes.values()) {
			if(n.isAlive()) {
				for(int i = 0; i < n.getSupportedOperators().length(); i++) {
					if(operators.indexOf(n.getSupportedOperators().charAt(i)) == -1) {
						operators += n.getSupportedOperators().charAt(i);
					}
				}
			}
		}
		
		return operators;
	}
	
	/**
	 * @param operator
	 * 			+, -, * or /
	 * @return The node with the least usage, that supports the operator.
	 */
	public Node getBestNodeForOperator(String operator) {
		
		Node node = null;
		for(Node n: nodes.values()) {
			if(n.isAlive() && n.getSupportedOperators().contains(operator)) {
				if(node != null) {
					if(node.getUsage() > n.getUsage()) {
						node = n;
					}
				} else {
					node = n;
				}
			}
		}
		
		return node;
	}
}
