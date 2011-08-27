package com.unitt.framework.websocket.netty;


import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map.Entry;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.buffer.HeapChannelBufferFactory;
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

import com.unitt.framework.websocket.NetworkSocketFacade;
import com.unitt.framework.websocket.NetworkSocketObserver;
import com.unitt.framework.websocket.WebSocketConnectConfig;
import com.unitt.framework.websocket.WebSocketObserver;


public class NettyClientNetworkSocket extends SimpleChannelUpstreamHandler implements NetworkSocketFacade, ChannelPipelineFactory, WebSocketObserver
{
    protected static final Charset utf8Charset = Charset.forName( "UTF-8" );
    
    private NetworkSocketObserver socketObserver;
    private ClientBootstrap       bootstrap;
    private Channel               channel;
    private WebSocketObserver     observer;
    private long                  timeOfLastActivity;
    private ChannelBufferFactory  buffers;


    // constructors
    // ---------------------------------------------------------------------------
    public NettyClientNetworkSocket( ClientBootstrap aBootstrap, WebSocketObserver aObserver )
    {
        bootstrap = aBootstrap;
        observer = aObserver;

        // set pipeline factory on bootstrap
        bootstrap.setPipelineFactory( this );
        buffers = HeapChannelBufferFactory.getInstance();
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
    public void channelConnected( ChannelHandlerContext aContext, ChannelStateEvent aEvent ) throws Exception
    {
        // we are connected - init values
        channel = aContext.getChannel();

        // notify socketObserver
        if ( socketObserver != null )
        {
            socketObserver.onConnect();
        }
    }

    @Override
    public void messageReceived( ChannelHandlerContext aContext, MessageEvent aEvent ) throws Exception
    {
        Object msg = aEvent.getMessage();
        if ( msg instanceof HttpResponse )
        {
            HttpResponse message = (HttpResponse) msg ;
            StringBuffer headers = new StringBuffer();
            headers.append( "HTTP/1.1 " );
            headers.append( message.getStatus().getCode() );
            headers.append("\r\n");
            for (Entry<String, String> header : message.getHeaders())
            {
                headers.append(header.getKey());
                headers.append(":");
                headers.append(header.getValue());
                headers.append("\r\n");
            }
            socketObserver.onReceivedData( headers.toString().getBytes( utf8Charset ) );
        }
        else
        {
            if (msg instanceof ChannelBuffer)
            {
                socketObserver.onReceivedData( ((ChannelBuffer) msg).array() );
            }
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
    public void connect( WebSocketConnectConfig aConfig )
    {
        bootstrap.connect( new InetSocketAddress( aConfig.getUrl().getHost(), aConfig.getUrl().getPort() ) );
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

        // notify socketObserver
        if ( socketObserver != null )
        {
            socketObserver.onDisconnect( exception );
        }
    }

    public void write( byte[] aBytes )
    {
        // write bytes to channel
        // channel.write( aBytes );
        channel.write( buffers.getBuffer( aBytes, 0, aBytes.length ) );
    }

    public void upgrade()
    {
        channel.getPipeline().remove( "encoder" );
        channel.getPipeline().remove( "decoder" );
    }

    public void setObserver( NetworkSocketObserver aSocketObserver )
    {
        socketObserver = aSocketObserver;
    }


    // websocket socketObserver logic
    // ---------------------------------------------------------------------------
    public void onOpen( String aProtocol, List<String> aExtensions )
    {
        updateLastActivity();
        observer.onOpen( aProtocol, aExtensions );
    }

    public void onError( Exception aException )
    {
        updateLastActivity();
        observer.onError( aException );
    }

    public void onClose( Exception aException, String aMessage )
    {
        updateLastActivity();
        observer.onClose( aException, aMessage );
    }

    public void onPong( String aMessage )
    {
        updateLastActivity();
    }

    public void onBinaryMessage( byte[] aMessage )
    {
        updateLastActivity();
        observer.onBinaryMessage( aMessage );
    }

    public void onTextMessage( String aMessage )
    {
        updateLastActivity();
        observer.onTextMessage( aMessage );
    }

    protected void updateLastActivity()
    {
        timeOfLastActivity = System.currentTimeMillis();
    }
}