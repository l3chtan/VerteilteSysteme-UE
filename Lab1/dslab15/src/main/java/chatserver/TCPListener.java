package chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by et on 16.10.15.
 */
public class TCPListener extends Listener {

	private ServerSocket sSoc;
	private ExecutorService pool;
	private ConcurrentLinkedQueue<Socket> socs;
	private Socket soc;

	public TCPListener(int port) throws IOException {
		sSoc = new ServerSocket(port);

		pool = Executors.newCachedThreadPool();
		socs = new ConcurrentLinkedQueue<Socket>();
	}

	public void run() {

		try {
			while (true) {
				soc = sSoc.accept();
				socs.add(soc);
				pool.execute(new TCPHandler(soc));
			}

		} catch (IOException e) {
			System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
			// e.printStackTrace();
		} finally {
			pool.shutdown();

			Iterator<Socket> i = socs.iterator();
			while (i.hasNext()) {
				Socket s = i.next();
				if (s != null && !s.isClosed()) {
					try {
						s.close();
					} catch (IOException e) {
					}
				}
			}
			try {
				if (!pool.awaitTermination(10000, TimeUnit.MILLISECONDS))
					pool.shutdownNow();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
			}
			try {
				if (sSoc != null && !sSoc.isClosed())
					sSoc.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getClass().getSimpleName() + ": " + e1.getMessage());
			}
		}
	}

	@Override
	public void close() throws IOException {
		sSoc.close();
	}
}
