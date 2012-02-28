package com.unitt.framework.websocket.netty;


import java.net.URI;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.unitt.framework.websocket.WebSocket;
import com.unitt.framework.websocket.WebSocketConnectConfig;
import com.unitt.framework.websocket.WebSocketFactory;
import com.unitt.framework.websocket.WebSocketObserver;


public class ClientWebsocketFactory
{

    private static NioClientSocketChannelFactory socketChannelFactory;
    private static WebSocketFactory wsFactory;

    static
    {
        socketChannelFactory = new NioClientSocketChannelFactory( Executors.newCachedThreadPool(), Executors.newCachedThreadPool() );
        wsFactory = new WebSocketFactory();
    }
    
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
        ClientBootstrap bootstrap = new ClientBootstrap( socketChannelFactory );
        WebSocketConnectConfig config = getConfig( aUrl );
        NettyClientNetworkSocket networkSocket = new NettyClientNetworkSocket(bootstrap, aObserver);
        return wsFactory.createClient( networkSocket, config, networkSocket );
    }

    /**
     * Creates a client websocket that will attach to the specified url.
     * 
     * @param aConfig config used to setup websocket
     * @param aObserver observer that will respond to lifecycle events and messages
     */
    public static WebSocket create( WebSocketConnectConfig aConfig, WebSocketObserver aObserver )
    {
        ClientBootstrap bootstrap = new ClientBootstrap( socketChannelFactory );
        NettyClientNetworkSocket networkSocket = new NettyClientNetworkSocket(bootstrap, aObserver);
        return wsFactory.createClient( networkSocket, aConfig, networkSocket );
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
