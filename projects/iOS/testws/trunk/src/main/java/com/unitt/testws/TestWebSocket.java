package com.unitt.testws;


import java.io.IOException;

import org.eclipse.jetty.websocket.WebSocket;


public class TestWebSocket implements WebSocket
{
    protected Outbound          outbound;


    // websocket logic
    // ---------------------------------------------------------------------------
    public void onConnect( Outbound aOutbound )
    {
        outbound = aOutbound;
    }

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
        throw new UnsupportedOperationException( "Not Implemented Yet." );
    }
}
