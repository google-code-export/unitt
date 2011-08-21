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

    public WebSocketServerConnection( WebSocketObserver observer, NetworkSocketFacade network, WebSocketConnectConfig connectConfig, byte[] clientHandshake )
    {
        super( observer, network, connectConfig );
        setHandshake( new WebSocketHandshake( clientHandshake, connectConfig ) );
    }

        
    // server logic
    // ---------------------------------------------------------------------------
    @Override
    public void onReceivedData( byte[] data )
    {
        if ( getState() == WebSocketState.NeedsHandshake )
        {
            getNetwork().upgrade();
            setState( WebSocketState.Connected );
            //@todo: handle extensions
            sendOpenToObserver( getHandshake().getServerConfig().getSelectedProtocol(), null );
        }
        
        super.onReceivedData( data );
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
}
