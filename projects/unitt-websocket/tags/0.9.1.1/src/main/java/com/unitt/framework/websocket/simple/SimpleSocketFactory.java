package com.unitt.framework.websocket.simple;

import java.net.URI;

import com.unitt.framework.websocket.WebSocket;
import com.unitt.framework.websocket.WebSocketConnectConfig;
import com.unitt.framework.websocket.WebSocketFactory;
import com.unitt.framework.websocket.WebSocketObserver;

public class SimpleSocketFactory
{
    protected static WebSocketConnectConfig getConfig(String aUrlString)
    {
        //use websocket protocol, if missing
        if ( aUrlString.indexOf( "://" ) == -1 )
        {
            aUrlString = "ws://" + aUrlString;
        }

        //create url
        URI url;
        try
        {
            url = new URI(aUrlString);
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            throw new RuntimeException( "invalid url syntax: " + aUrlString );
        }

        //validate websocket protocols
        String protocol = url.getScheme();
        if ( !protocol.equals( "ws" ) && !protocol.equals( "wss" ) )
        {
            throw new IllegalArgumentException( "Unsupported protocol: " + protocol );
        }
        
        //create config
        WebSocketConnectConfig config = new WebSocketConnectConfig();
        config.setAvailableProtocol( "oobium-service" );
        config.setUrl( url );
        config.setMaxPayloadSize( 32*1024 );
        config.setVerifySecurityKey( true );
        config.setVerifyTlsDomain( true );
        
        return config;
    }

    /**
     * Creates a client websocket that will attach to the specified url.
     * 
     * @param aUrl url to server websocket
     * @param aObserver observer that will respond to lifecycle events and messages
     */
    public static WebSocket create( String aUrl, WebSocketObserver aObserver )
    {
        WebSocketConnectConfig config = getConfig( aUrl );
        return new WebSocketFactory().createClient( aObserver, config, new NetworkSocket() );
    }

    /**
     * Creates a client websocket that will attach to the specified url.
     * 
     * @param aConfig config used to setup websocket
     * @param aObserver observer that will respond to lifecycle events and messages
     */
    public static WebSocket create( WebSocketConnectConfig aConfig, WebSocketObserver aObserver )
    {
        return new WebSocketFactory().createClient( aObserver, aConfig, new NetworkSocket() );
    }

    /**
     * Creates a client websocket and opens it to the specified url. The listener will have
     * to respond as opening is not a synchronous operation.
     * 
     * @param aUrl url to server websocket
     * @param aObserver observer that will respond to lifecycle events and messages
     */
    public static WebSocket connect( String aUrl, WebSocketObserver aObserver )
    {
        WebSocket socket = create( aUrl, aObserver );
        socket.open();
        return socket;
    }

    /**
     * Creates a client websocket and opens it to the specified url. The listener will have
     * to respond as opening is not a synchronous operation.
     * 
     * @param aConfig config used to setup websocket
     * @param aObserver observer that will respond to lifecycle events and messages
     */
    public static WebSocket open( WebSocketConnectConfig aConfig, WebSocketObserver aObserver )
    {
        WebSocket socket = create( aConfig, aObserver );
        socket.open();
        return socket;
    }
}
