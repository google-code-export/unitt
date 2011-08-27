package com.unitt.framework.websocket.netty;


import java.net.InetSocketAddress;
import java.util.List;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrameEncoder;

import com.unitt.framework.websocket.NetworkSocketFacade;
import com.unitt.framework.websocket.NetworkSocketObserver;
import com.unitt.framework.websocket.WebSocketConnectConfig;
import com.unitt.framework.websocket.WebSocketObserver;


public class NettyClientNetworkSocket extends SimpleChannelUpstreamHandler implements NetworkSocketFacade, ChannelPipelineFactory, WebSocketObserver
{
    private NetworkSocketObserver observer;
    private ClientBootstrap       bootstrap;
    private Channel               channel;
    private WebsocketListener     listener;
    private long                  timeOfLastActivity;


    // constructors
    // ---------------------------------------------------------------------------
    public NettyClientNetworkSocket( ClientBootstrap bootstrap, WebsocketListener listener )
    {
        this.bootstrap = bootstrap;
        this.listener = listener;

        // set pipeline factory on bootstrap
        bootstrap.setPipelineFactory( this );
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public long getTimeOfLastActivity()
    {
        return timeOfLastActivity;
    }


    // channel logic
    // ---------------------------------------------------------------------------
    @Override
    public void channelConnected( ChannelHandlerContext ctx, ChannelStateEvent e ) throws Exception
    {
        // we are connected - init values
        channel = ctx.getChannel();
    }

    @Override
    public void messageReceived( ChannelHandlerContext ctx, MessageEvent e ) throws Exception
    {
        Object msg = e.getMessage();
        if ( msg instanceof HttpResponse )
        {
           observer.onReceivedData( ((HttpResponse) msg ).getContent().array());
        }
        else
        {
            observer.onReceivedData( (byte[]) msg );
        }
    }

    // channel pipeline logic
    // ---------------------------------------------------------------------------
    public ChannelPipeline getPipeline() throws Exception
    {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast( "decoder", new HttpResponseDecoder() );
        pipeline.addLast( "encoder", new HttpRequestEncoder() );
        pipeline.addLast( "wshandler", this );
        return pipeline;
    }

    // facade logic
    // ---------------------------------------------------------------------------
    public void connect( WebSocketConnectConfig config )
    {
        bootstrap.connect( new InetSocketAddress( config.getUrl().getHost(), config.getUrl().getPort() ) );

        // notify observer
        if ( observer != null )
        {
            observer.onConnect();
        }
    }

    public void disconnect()
    {
        Exception exception = null;

        // disconnect from channel
        try
        {
            channel.disconnect();
        }
        catch ( Exception e )
        {
            exception = e;
        }

        // notify observer
        if ( observer != null )
        {
            observer.onDisconnect( exception );
        }
    }

    public void write( byte[] bytes )
    {
        // write bytes to channel
        channel.write( bytes );
    }

    public void upgrade()
    {
        // @todo: use actual websocket encoder or see if i can just remove and
        // use handler?
        channel.getPipeline().replace( "encoder", "wsencoder", new WebSocketFrameEncoder() );
    }

    public void setObserver( NetworkSocketObserver observer )
    {
        this.observer = observer;
    }


    // websocket observer logic
    // ---------------------------------------------------------------------------
    public void onOpen( String protocol, List<String> extensions )
    {
        updateLastActivity();
        listener.onOpen();
    }

    public void onError( Exception exception )
    {
        updateLastActivity();
        listener.onError( exception );
    }

    public void onClose( Exception exception, String message )
    {
        updateLastActivity();
        listener.onClose( exception, message );
    }

    public void onPong( String message )
    {
        updateLastActivity();
    }

    public void onBinaryMessage( byte[] message )
    {
        updateLastActivity();
        listener.onBinaryMessage( message );
    }

    public void onTextMessage( String message )
    {
        updateLastActivity();
        listener.onTextMessage( message );
    }

    protected void updateLastActivity()
    {
        timeOfLastActivity = System.currentTimeMillis();
    }
}