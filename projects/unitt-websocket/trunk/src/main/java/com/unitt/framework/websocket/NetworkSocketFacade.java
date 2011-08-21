package com.unitt.framework.websocket;

import java.io.IOException;

public interface NetworkSocketFacade
{
    public void connect(WebSocketConnectConfig config);
    public void disconnect();
    public void write(byte[] bytes) throws IOException;
    public void upgrade();
    
    public void setObserver(NetworkSocketObserver observer);
}
