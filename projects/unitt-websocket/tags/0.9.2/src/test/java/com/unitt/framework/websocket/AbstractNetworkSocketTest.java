package com.unitt.framework.websocket;


import java.nio.charset.Charset;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractNetworkSocketTest implements WebSocketObserver
{
    protected static final Charset   utf8Charset      = Charset.forName( "UTF-8" );
    protected static final String prefix = "Message: ";
    
    protected WebSocket ws;
    protected String response;
    protected boolean testedSingleFragment = false;
    protected boolean testedMultipleFragments = false;
    protected String largeMessage = null;
    protected String smallMessage = null;

    @Before
    public void setUp() throws Exception
    {
        smallMessage = "blue";
        largeMessage = getReallyLongMessage();
        testedSingleFragment = false;
        testedMultipleFragments = false;
    }

    @After
    public void tearDown() throws Exception
    {
        ws = null;
    }

    @Test
    public void testRoundTrip() throws Exception
    {
        ws.open();
        Thread.sleep(30000);
    }
    
    public String getReallyLongMessage()
    {
        StringBuffer output = new StringBuffer();
        for (int i = 0; i < 40; i++)
        {
            output.append(i);
            output.append( smallMessage );
            output.append(",");
        }
        return output.toString();
    }
    
        
    // service logic
    // ---------------------------------------------------------------------------
    public void onBinaryMessage( byte[] aMessage )
    {
        System.out.println("On Binary Message: " + aMessage.length);
    }

    public void onClose( int aStatusCode, String aMessage, Exception aException )
    {
        System.out.println("On Close: status=" + aStatusCode + ", message=" + aMessage + ", exception=" + aException);
    }

    public void onError( Exception aException )
    {
        System.out.println("On error: " + aException);
    }

    public void onOpen( String aProtocol, List<String> aExtensions )
    {
        System.out.println("On Open");
        ws.sendMessage( smallMessage );
    }

    public void onPong( String aMessage )
    {
        System.out.println("On Pong: " + aMessage);
    }

    public void onTextMessage( String aMessage )
    {
        System.out.println("On Text Message: " + aMessage);
        response = aMessage;
        if (!testedSingleFragment)
        {
            Assert.assertEquals( "Did not receive the correct small message", prefix + smallMessage, response );
            testedSingleFragment = true;
            ws.sendMessage( largeMessage );
        }
        else if (!testedMultipleFragments)
        {
            Assert.assertEquals( "Did not receive the correct large message", prefix + largeMessage, response );
            testedMultipleFragments = true;
	        ws.close(WebSocket.WebSocketCloseStatusEndpointGone, "woah");
        }
    }
}
