package com.unitt.testws;


import java.io.IOException;

import org.eclipse.jetty.websocket.WebSocket;


public class TestWebSocket implements WebSocket, WebSocket.OnTextMessage, WebSocket.OnBinaryMessage, WebSocket.OnControl, WebSocket.OnFrame
{
    protected Connection          outbound;


    // websocket logic
    // ---------------------------------------------------------------------------

    public void onDisconnect()
    {
        outbound = null;
    }

    public boolean isConnected()
    {
        return outbound != null;
    }

    public void onMessage( byte aFrame, String aData )
    {
        try
        {
            System.out.println("Received message: " + aData);
            outbound.sendMessage( "Message: " + aData );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public void onFragment( boolean aMore, byte aOpcode, byte[] aData, int aOffset, int aLength )
    {
        throw new UnsupportedOperationException( "Not Implemented Yet." );
    }

    public void onMessage( byte aOpcode, byte[] aData, int aOffset, int aLength )
    {
        try
        {
            System.out.println("Copying array of length: " + aLength + " from: " + new String(aData));
            byte[] stringContents = new byte[aLength];
            System.arraycopy( aData, aOffset, stringContents, 0, aLength );
            System.out.println("Message Contents: " + new String(stringContents));
            outbound.sendMessage( "Message: " + new String(stringContents) );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public void onClose( int aCloseCode, String aMessage )
    {
        outbound = null;
    }

    public void onOpen( Connection aConnection )
    {
        System.out.println("OnOpen");
        outbound = aConnection;
        aConnection.setMaxTextMessageSize( 32*1024 );
    }

    public void onMessage( String aData )
    {
        try
        {
            System.out.println("OnMessage: " + aData);
            byte[] bytes = aData.getBytes();
            for (byte b : bytes)
            {
                System.out.println(Integer.toHexString( b ));
            }
            outbound.sendMessage( "Message: " + aData );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public void onMessage( byte[] aData, int aOffset, int aLength )
    {
        try
        {
            System.out.println("OnBinaryMessage: " + new String(aData));
            outbound.sendMessage( "Message: " + new String(aData) );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public boolean onControl( byte aControlCode, byte[] aData, int aOffset, int aLength )
    {
        System.out.println("OnControl: " + new Integer(aControlCode));
        return false;
    }

    public boolean onFrame( byte aFlags, byte aOpcode, byte[] aData, int aOffset, int aLength )
    {
        System.out.println("OnFrame: " + new Integer(aOpcode));
        return false;
    }

    public void onHandshake( FrameConnection aConnection )
    {
        System.out.println("OnHandshake");
    }
}
