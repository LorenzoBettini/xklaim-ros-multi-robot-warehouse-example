package messages;

import java.net.URI;

import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import ros.RosBridge;

@WebSocket
public class XklaimToRosConnection extends RosBridge {
	public XklaimToRosConnection(String rosbridgeWebsocketURI) {
		long timeout = 900000;
		this.connect(rosbridgeWebsocketURI, true,timeout);
		
	}
	
	public void connect(String rosBridgeURI, boolean waitForConnection,long idleTimeout){
		WebSocketClient client = new WebSocketClient();
		try {
			client.setMaxIdleTimeout(idleTimeout);
			client.start();
			URI echoUri = new URI(rosBridgeURI);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			client.connect(this, echoUri, request);
			System.out.printf("Connecting to : %s%n", echoUri);
			if(waitForConnection){
				this.waitForConnection();
			}

		} catch (Throwable t) {
			t.printStackTrace();
		}

	}
}
