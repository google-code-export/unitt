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
    
    public void onOpen(String aProtocol, List<String> aExtensions);
    public void onError(Exception aException);
    public void onClose(Exception aException, String aMessage);
    public void onPong(String aMessage);
    
    public void onBinaryMessage(byte[] aMessage);
    public void onTextMessage(String aMessage);
}
