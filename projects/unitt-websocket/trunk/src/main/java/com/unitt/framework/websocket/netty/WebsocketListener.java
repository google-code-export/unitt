package com.unitt.framework.websocket.netty;


public interface WebsocketListener
{
    public void onOpen();
    public void onError(Exception exception);
    public void onClose(Exception exception, String message);
    
    public void onBinaryMessage(byte[] message);
    public void onTextMessage(String message);
}
