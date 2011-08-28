package com.unitt.framework.websocket;

import java.util.List;

/**
 * @author Josh Morris
 */
public interface WebSocketObserver
{
    public void onOpen(String aProtocol, List<String> aExtensions);
    public void onError(Exception aException);
    public void onClose(int aStatusCode, String aMessage, Exception aException);
    public void onPong(String aMessage);
    
    public void onBinaryMessage(byte[] aMessage);
    public void onTextMessage(String aMessage);
}
