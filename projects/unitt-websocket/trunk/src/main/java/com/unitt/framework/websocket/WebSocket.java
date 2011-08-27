package com.unitt.framework.websocket;

/**
 * @author Josh Morris
 */
public interface WebSocket
{
    public void open();
    public void close(String aMessage);
    public void ping(String aMessage);
    public void sendMessage(byte[] aMessage);
    public void sendMessage(String aMessage);
}
