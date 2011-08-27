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

    public WebSocketClientConnection( WebSocketObserver aObserver, NetworkSocketFacade aNetwork, WebSocketConnectConfig aConnectConfig )
    {
        super( aObserver, aNetwork, aConnectConfig );
        
        setHandshake( new WebSocketHandshake( aConnectConfig ) );
    }

        
    // client logic
    // ---------------------------------------------------------------------------
    @Override
    protected boolean sendWithMask()
    {
        return true;
    }
    
    @Override
    public void onReceivedData( byte[] aData )
    {
        if ( getState() == WebSocketState.NeedsHandshake )
        {
            handleHandshake( aData );
        }
        else
        {
            super.onReceivedData( aData );
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
    
    protected void handleHandshake(byte[] aHandshakeBytes)
    {
        if (getHandshake().verifyServerHandshake( aHandshakeBytes ))
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
