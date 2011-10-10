package com.unitt.framework.websocket;

/**
 * @author Josh Morris
 */
public class WebSocketFactory
{
    public WebSocket createClient(WebSocketObserver aObserver, WebSocketConnectConfig aConfig, NetworkSocketFacade aNetwork)
    {
        return new WebSocketClientConnection( aObserver, aNetwork, aConfig );
    }
    
    public WebSocket createServer(WebSocketObserver aObserver, WebSocketConnectConfig aConfig, NetworkSocketFacade aNetwork, byte[] aClientHandshakeBytes)
    {
        return new WebSocketServerConnection( aObserver, aNetwork, aConfig, aClientHandshakeBytes );
    }
}
