package com.unitt.framework.websocket.simple;


import java.net.URI;

import org.junit.Before;

import com.unitt.framework.websocket.AbstractNetworkSocketTest;
import com.unitt.framework.websocket.WebSocketConnectConfig;

public class NetworkSocketTest extends AbstractNetworkSocketTest
{
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        WebSocketConnectConfig config = new WebSocketConnectConfig();
        config.setUrl( new URI("ws://10.0.1.36:8080/testws/ws/test") );
        config.setMaxPayloadSize( 90 );
        ws =  SimpleSocketFactory.create( config, this );
    }
}
