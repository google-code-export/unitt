package com.unitt.framework.websocket.netty;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.unitt.framework.websocket.WebSocketConnectConfig;
import com.unitt.framework.websocket.WebSocketFactory;


public class ClientWebsocketFactory
{

    private static NioClientSocketChannelFactory socketChannelFactory;
    private static WebSocketFactory wsFactory;

    static
    {
        socketChannelFactory = new NioClientSocketChannelFactory( Executors.newCachedThreadPool(), Executors.newCachedThreadPool() );
        wsFactory = new WebSocketFactory();
    }
    
    protected static WebSocketConnectConfig getConfig(String urlString)
    {
        //use websocket protocol, if missing
        if ( urlString.indexOf( "://" ) == -1 )
        {
            urlString = "ws://" + urlString;
        }

        //create url
        URL url;
        try
        {
            url = new URL(urlString);
        }
        catch ( MalformedURLException e )
        {
            throw new RuntimeException( "invalid url syntax: " + urlString );
        }

        //validate websocket protocols
        String protocol = url.getProtocol();
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
     * @param url url to server websocket
     * @param listener listener that will respond to lifecycle events and messages
     */
    public static ClientWebsocket create( String url, WebsocketListener listener )
    {
        ClientBootstrap bootstrap = new ClientBootstrap( socketChannelFactory );
        WebSocketConnectConfig config = getConfig( url );
        NettyClientNetworkSocket networkSocket = new NettyClientNetworkSocket(bootstrap, listener);
        return new ClientWebSocketAdapter( wsFactory.createClient( networkSocket, config, networkSocket ) );
    }

    /**
     * Creates a client websocket and opens it to the specified url. The listener will have
     * to respond as opening is not a synchronous operation.
     * 
     * @param url url to server websocket
     * @param listener listener that will respond to lifecycle events and messages
     */
    public static ClientWebsocket connect( String url, WebsocketListener listener )
    {
        ClientWebsocket socket = create( url, listener );
        socket.open();
        return socket;
    }

}
