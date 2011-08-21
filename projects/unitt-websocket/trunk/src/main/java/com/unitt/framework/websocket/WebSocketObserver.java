package com.unitt.framework.websocket;

import java.util.List;

/**
 * @author Josh Morris
 */
public interface WebSocketObserver
{
    public enum WebSocketReadyState 
    {
        CONNECTING, //The connection has not yet been established.
        OPEN, //The WebSocket connection is established and communication is possible.
        CLOSING, //The connection is going through the closing handshake.
        CLOSED //The connection has been closed or could not be opened
    };
    
    public void onOpen(String protocol, List<String> extensions);
    public void onError(Exception exception);
    public void onClose(Exception exception, String message);
    public void onPong(String message);
    
    public void onBinaryMessage(byte[] message);
    public void onTextMessage(String message);
}
