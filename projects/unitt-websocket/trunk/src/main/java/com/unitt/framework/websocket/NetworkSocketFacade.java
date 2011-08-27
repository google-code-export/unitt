package com.unitt.framework.websocket;

import java.io.IOException;

public interface NetworkSocketFacade
{
    public void connect(WebSocketConnectConfig oConfig);
    public void disconnect();
    public void write(byte[] aBytes) throws IOException;
    public void upgrade();
    
    public void setObserver(NetworkSocketObserver aObserver);
}
