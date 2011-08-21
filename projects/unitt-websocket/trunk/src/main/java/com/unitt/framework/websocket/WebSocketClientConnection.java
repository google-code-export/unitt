package com.unitt.framework.websocket;

import java.io.IOException;


/**
 * @author Josh Morris
 */
public class WebSocketClientConnection extends WebSocketConnection
{
    
    // constructors
    // ---------------------------------------------------------------------------
    public WebSocketClientConnection()
    {
        //default
    }

    public WebSocketClientConnection( WebSocketObserver observer, NetworkSocketFacade network, WebSocketConnectConfig connectConfig )
    {
        super( observer, network, connectConfig );
        
        setHandshake( new WebSocketHandshake( connectConfig ) );
    }

        
    // client logic
    // ---------------------------------------------------------------------------
    @Override
    protected boolean sendWithMask()
    {
        return true;
    }
    
    @Override
    public void onReceivedData( byte[] data )
    {
        if ( getState() == WebSocketState.NeedsHandshake )
        {
            handleHandshake( data );
        }
        else
        {
            super.onReceivedData( data );
        }
    }
    
    @Override
    public void onConnect()
    {
        setState( WebSocketState.NeedsHandshake );
        try
        {
            getNetwork().write( getHandshake().getClientHandshakeBytes() );
        }
        catch ( IOException e )
        {
            sendErrorToObserver( e );
        }
    }
    
    protected void handleHandshake(byte[] handshakeBytes)
    {
        if (getHandshake().verifyServerHandshake( handshakeBytes ))
        {
            getNetwork().upgrade();
            setState( WebSocketState.Connected );
            //@todo: handle extensions
            sendOpenToObserver( getHandshake().getServerConfig().getSelectedProtocol(), null );
        }
        else
        {
            setCloseMessage( "Invalid Handshake" );
            setState( WebSocketState.Disconnected );
            getNetwork().disconnect();
        }
    }
}
