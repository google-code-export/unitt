package com.unitt.servicemanager.websocket;

import java.io.IOException;

import com.unitt.commons.foundation.lifecycle.Destructable;
import com.unitt.commons.foundation.lifecycle.Initializable;

public interface ServerWebSocket extends Initializable, Destructable
{
    public void sendMessage(byte[] aMessage) throws IOException;
}
