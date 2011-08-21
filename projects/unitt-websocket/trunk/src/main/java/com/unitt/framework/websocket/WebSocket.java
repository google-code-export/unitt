package com.unitt.framework.websocket;

/**
 * @author Josh Morris
 */
public interface WebSocket
{
    public void open();
    public void close(String message);
    public void ping(String message);
    public void sendMessage(byte[] message);
    public void sendMessage(String message);
}
