package com.unitt.framework.websocket;

/**
 * @author Josh Morris
 */
public class WebSocketFactory
{
    public WebSocket createClient(WebSocketObserver observer, WebSocketConnectConfig config, NetworkSocketFacade network)
    {
        return new WebSocketClientConnection( observer, network, config );
    }
    
    public WebSocket createServer(WebSocketObserver observer, WebSocketConnectConfig config, NetworkSocketFacade network, byte[] clientHandshakeBytes)
    {
        return new WebSocketServerConnection( observer, network, config, clientHandshakeBytes );
    }
}
