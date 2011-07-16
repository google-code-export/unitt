package com.unitt.servicemanager.hazelcast.websocket;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.MessageBody;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;
import com.unitt.servicemanager.websocket.MessageSerializerRegistry;
import com.unitt.servicemanager.websocket.MessagingWebSocket;
import com.unitt.servicemanager.websocket.ServerWebSocket;


public class HazelcastWebSocket extends MessagingWebSocket
{
    private static Logger                     logger = LoggerFactory.getLogger( HazelcastWebSocket.class );

    private HazelcastInstance                 hazelcastClient;
    private BlockingQueue<MessageRoutingInfo> headerQueue;

    protected boolean                         isInitialized;


    // constructors
    // ---------------------------------------------------------------------------
    public HazelcastWebSocket()
    {
        // default
    }

    public HazelcastWebSocket( MessageSerializerRegistry aSerializers, long aQueueTimeoutInMillis, ServerWebSocket aServerWebSocket, HazelcastInstance aHazelcastClient )
    {
        this( aSerializers, aQueueTimeoutInMillis, aServerWebSocket, aHazelcastClient, null );
    }

    public HazelcastWebSocket( MessageSerializerRegistry aSerializers, long aQueueTimeoutInMillis, ServerWebSocket aServerWebSocket, HazelcastInstance aHazelcastClient, String aSocketId )
    {
        super( aSerializers, aQueueTimeoutInMillis, aServerWebSocket, aSocketId );
        setHazelcastClient( aHazelcastClient );
    }


    // lifecycle logic
    // ---------------------------------------------------------------------------
    public void initialize()
    {
        String missing = null;

        // validate we have all properties
        if ( getHazelcastClient() == null )
        {
            ValidationUtil.appendMessage( missing, "Missing hazelcast client. " );
        }
        if ( getHazelcastClient().getQueue( getSocketId() ) != null )
        {
            ValidationUtil.appendMessage( missing, "Missing socket queue. " );
        }
        if ( getHazelcastClient().getMap( getSocketId() ) != null )
        {
            ValidationUtil.appendMessage( missing, "Missing socket map. " );
        }

        // fail out with appropriate message if missing anything
        if ( missing != null )
        {
            logger.error( missing );
            throw new IllegalStateException( missing );
        }

        setInitialized( true );
    }

    public boolean isInitialized()
    {
        return isInitialized;
    }

    public void destroy()
    {
        // destroy queue
        try
        {
            getHazelcastClient().getQueue( getSocketId() ).destroy();
        }
        catch ( Exception e )
        {
            logger.error( "An error occurred while destroying the header queue for websocket: " + getSocketId(), e );
        }

        // destroy map
        try
        {
            getHazelcastClient().getMap( getSocketId() ).destroy();
        }
        catch ( Exception e )
        {
            logger.error( "An error occurred while destroying the body map for websocket: " + getSocketId(), e );
        }

        // clear hazelcast
        setHazelcastClient( null );
        isInitialized = false;
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public HazelcastInstance getHazelcastClient()
    {
        return hazelcastClient;
    }

    public void setHazelcastClient( HazelcastInstance aClient )
    {
        hazelcastClient = aClient;
    }


    // service logic
    // ---------------------------------------------------------------------------
    public ConcurrentMap<String, MessageBody> getBodyMap()
    {
        return getHazelcastClient().getMap( "body:" + getSocketId() );
    }

    public BlockingQueue<MessageRoutingInfo> getHeaderQueue()
    {
        if ( headerQueue == null )
        {
            headerQueue = getHazelcastClient().getQueue( "incoming:" + getSocketId() );
        }

        return headerQueue;
    }
}
