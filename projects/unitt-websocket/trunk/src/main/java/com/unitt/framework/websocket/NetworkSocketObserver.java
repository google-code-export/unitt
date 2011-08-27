package com.unitt.framework.websocket;

/**
 * @author Josh Morris
 */
public interface NetworkSocketObserver
{
    public void onConnect();
    public void onDisconnect(Exception aException);
    public void onReceivedData(byte[] aData);
}
