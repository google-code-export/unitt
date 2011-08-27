package com.unitt.framework.websocket.netty;


public interface ClientWebsocket
{
    public void open();
    public void close(String message);
    public void sendMessage(byte[] message);
    public void sendMessage(String message);
}
