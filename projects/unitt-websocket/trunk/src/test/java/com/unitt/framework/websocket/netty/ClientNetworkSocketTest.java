package com.unitt.framework.websocket.netty;


import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.unitt.framework.websocket.WebSocket;
import com.unitt.framework.websocket.WebSocketObserver;

public class ClientNetworkSocketTest implements WebSocketObserver
{
    protected WebSocket ws;
    protected String response;

    @Before
    public void setUp() throws Exception
    {
        ws = ClientWebsocketFactory.create( "ws://10.0.1.5:8080/testws/ws/test", this); 
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testRoundTrip() throws Exception
    {
        ws.open();
        Thread.sleep(10000);
        Assert.assertEquals("Did not find the correct message.", "Message: Blue", response);
    }
    
        
    // service logic
    // ---------------------------------------------------------------------------
    public void onBinaryMessage( byte[] aMessage )
    {
        System.out.println("On Binary Message: " + aMessage.length);
    }

    public void onClose( Exception aException, String aMessage )
    {
        System.out.println("On Close: message=" + aMessage + ", exception=" + aException);
    }

    public void onError( Exception aException )
    {
        System.out.println("On error: " + aException);
    }

    public void onOpen( String aProtocol, List<String> aExtensions )
    {
        ws.sendMessage( "Blue" );
    }

    public void onPong( String aMessage )
    {
        System.out.println("On Pong: " + aMessage);
    }

    public void onTextMessage( String aMessage )
    {
        System.out.println("On Text Message: " + aMessage);
        response = aMessage;
    }
}
