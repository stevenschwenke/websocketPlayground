import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

public class Client {

	public static void main(String[] args) throws IOException, URISyntaxException {
		new Client().run(new URI("ws://localhost:8080/"));
	}

	public void run(URI destinationUri) throws IOException {

		WebSocketClient client = new WebSocketClient();
		MyWebSocket socket = new MyWebSocket();
		try {
			client.start();
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			System.out.println("Connecting to " + destinationUri);
			client.connect(socket, destinationUri, request);
			socket.awaitClose(5, TimeUnit.SECONDS);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@WebSocket
	public class MyWebSocket {
		private final CountDownLatch closeLatch = new CountDownLatch(1);

		@OnWebSocketConnect
		public void onConnect(Session session) {
			try {
				System.out.println("Sending initial message");
				session.getRemote().sendString("Hello Server!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@OnWebSocketMessage
		public void onMessage(String message) {
			System.out.println("Receiving message: " + message);
		}

		@OnWebSocketClose
		public void onClose(int statusCode, String reason) {
			System.out.println("WebSocket Closed. Code:" + statusCode);
		}

		public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
			return this.closeLatch.await(duration, unit);
		}
	}

}