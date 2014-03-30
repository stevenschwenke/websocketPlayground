package websocketPlayground.amilamanoj;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

// Beispiel von amilamanoj.blogspot.de/2013/06/secure-websockets-with-jetty.html - hier aber keystore auskommentiert, weil jks-files nicht erstellt werden koennen

public class WebSocketServer {
	private Server server;
	private String host;
	private int port;
	private Resource keyStoreResource;
	private String keyStorePassword;
	private String keyManagerPassword;
	private List<Handler> webSocketHandlerList = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		WebSocketServer webSocketServer = new WebSocketServer();
		webSocketServer.setHost("localhost");
		webSocketServer.setPort(8443);
		// webSocketServer.setKeyStoreResource(new
		// FileResource(WebSocketServer.class.getResource("keystore.jks")));
		// webSocketServer.setKeyStorePassword("password");
		// webSocketServer.setKeyManagerPassword("password");
		webSocketServer.addWebSocket(MyWebSocket.class, "/");
		webSocketServer.initialize();
		webSocketServer.start();
	}

	public void initialize() {
		server = new Server();
		// connector configuration
		SslContextFactory sslContextFactory = new SslContextFactory();
		// sslContextFactory.setKeyStoreResource(keyStoreResource);
		// sslContextFactory.setKeyStorePassword(keyStorePassword);
		// sslContextFactory.setKeyManagerPassword(keyManagerPassword);
		SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString());
		HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(new HttpConfiguration());
		ServerConnector sslConnector = new ServerConnector(server, sslConnectionFactory, httpConnectionFactory);
		sslConnector.setHost(host);
		sslConnector.setPort(port);
		server.addConnector(sslConnector);
		// handler configuration
		HandlerCollection handlerCollection = new HandlerCollection();
		handlerCollection.setHandlers(webSocketHandlerList.toArray(new Handler[0]));
		server.setHandler(handlerCollection);
	}

	public void addWebSocket(final Class<?> webSocket, String pathSpec) {
		WebSocketHandler wsHandler = new WebSocketHandler() {
			@Override
			public void configure(WebSocketServletFactory webSocketServletFactory) {
				webSocketServletFactory.register(webSocket);
			}
		};
		ContextHandler wsContextHandler = new ContextHandler();
		wsContextHandler.setHandler(wsHandler);
		wsContextHandler.setContextPath(pathSpec); // this context path doesn't
													// work ftm
		webSocketHandlerList.add(wsHandler);
	}

	public void start() throws Exception {
		server.start();
		server.join();
	}

	public void stop() throws Exception {
		server.stop();
		server.join();
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setKeyStoreResource(Resource keyStoreResource) {
		this.keyStoreResource = keyStoreResource;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	public void setKeyManagerPassword(String keyManagerPassword) {
		this.keyManagerPassword = keyManagerPassword;
	}

}