package com.unitt.framework.websocket;

import java.io.IOException;


/**
 * @author Josh Morris
 */
public class WebSocketServerConnection extends WebSocketConnection
{
    
    // constructors
    // ---------------------------------------------------------------------------
    public WebSocketServerConnection()
    {
        //default
    }

    public WebSocketServerConnection( WebSocketObserver aObserver, NetworkSocketFacade aNetwork, WebSocketConnectConfig aConnectConfig, byte[] aClientHandshake )
    {
        super( aObserver, aNetwork, aConnectConfig );
        setHandshake( new WebSocketHandshake( aClientHandshake, aConnectConfig ) );
    }

        
    // server logic
    // ---------------------------------------------------------------------------
    @Override
    public void onReceivedData( byte[] aData )
    {
        if ( getState() == WebSocketState.NeedsHandshake )
        {
            getNetwork().upgrade();
            setState( WebSocketState.Connected );
            sendOpenToObserver( getHandshake().getClientConfig().getSelectedProtocol(), getHandshake().getClientConfig().getSelectedExtensions() );
        }
        
        super.onReceivedData( aData );
    }
    
    @Override
    public void onConnect()
    {
        //validate client handshake
        if (getHandshake().verifyClientHandshake())
        {
            setState( WebSocketState.NeedsHandshake );
            //send server handshake
            try
            {
                getNetwork().write(getHandshake().getServerHandshakeBytes());
            }
            catch ( IOException e )
            {
                sendErrorToObserver( e );
            }
        }
        else
        {
            //@todo: send correct failure status
            setCloseMessage( "Invalid Handshake" );
            setState( WebSocketState.Disconnected );
            getNetwork().disconnect();
        }
    }

    public boolean isClient()
    {
        return false;

    }
}
