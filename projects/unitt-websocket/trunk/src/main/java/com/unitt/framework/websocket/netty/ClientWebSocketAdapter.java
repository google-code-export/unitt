package com.unitt.framework.websocket.netty;

import com.unitt.framework.websocket.WebSocket;

public class ClientWebSocketAdapter implements ClientWebsocket
{
    private WebSocket websocket;

    public ClientWebSocketAdapter( WebSocket websocket )
    {
        super();
        this.websocket = websocket;
    }

    public void open()
    {
        websocket.open();
    }

    public void close( String message )
    {
        websocket.close( message );
    }

    public void sendMessage( byte[] message )
    {
        websocket.sendMessage( message );
    }

    public void sendMessage( String message )
    {
        websocket.sendMessage( message );
    }
}
