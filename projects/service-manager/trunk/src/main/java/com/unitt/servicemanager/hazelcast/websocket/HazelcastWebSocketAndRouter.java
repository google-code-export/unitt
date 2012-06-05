package com.unitt.servicemanager.hazelcast.websocket;


import com.hazelcast.core.HazelcastInstance;
import com.unitt.servicemanager.routing.InPlaceMessageRouter;
import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;


public class HazelcastWebSocketAndRouter extends MessagingWebSocket
{
    private static Logger                     logger = LoggerFactory.getLogger( HazelcastWebSocketAndRouter.class );

    private HazelcastInstance                 hazelcastClient;
    private InPlaceMessageRouter router;


    // constructors
    // ---------------------------------------------------------------------------
    public HazelcastWebSocketAndRouter()
    {
        // default
    }

    public HazelcastWebSocketAndRouter(String aServerId, MessageSerializerRegistry aSerializers, ServerWebSocket aServerWebSocket, HazelcastInstance aHazelcastClient)
    {
        this( aServerId, aSerializers, aServerWebSocket, aHazelcastClient, null );
    }

    public HazelcastWebSocketAndRouter(String aServerId, MessageSerializerRegistry aSerializers, ServerWebSocket aServerWebSocket, HazelcastInstance aHazelcastClient, String aSocketId)
    {
        super(aServerId, aSerializers, 0, aServerWebSocket, aSocketId);
        setHazelcastClient(aHazelcastClient);
    }


    // lifecycle logic
    // ---------------------------------------------------------------------------
    public void initialize()
    {
        String missing = null;

        // init
        if ( getSocketId() == null )
        {
            setSocketId( UUID.randomUUID().toString() );
        }
        if ( getQueueTimeoutInMillis() == 0 )
        {
            setQueueTimeoutInMillis( 30000 );
        }

        // validate we have all properties
        if ( getSerializerRegistry() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing serializer registry. " );
        }
        if ( getServerWebSocket() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing server web socket. " );
        }
        if ( getHazelcastClient() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing hazelcast client. " );
        }
        if ( getHazelcastClient().getQueue(getSocketId()) == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing socket queue: " + getSocketId() + ". " );
        }
        if ( getHazelcastClient().getMap(getSocketId()) == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing socket map: " + getSocketId() + ". " );
        }
        if ( getServerId() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing server id. " );
        }
        if ( getRouter() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing router. " );
        }

        // fail out with appropriate message if missing anything
        if ( missing != null )
        {
            logger.error( missing );
            throw new IllegalStateException( missing );
        }

        setInitialized( true );
    }

    public void destroy()
    {
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
        setHazelcastClient(null);
        setServerId(null);
        setInitialized(false);
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

    public InPlaceMessageRouter getRouter() {
        return router;
    }

    public void setRouter(InPlaceMessageRouter aRouter) {
        router = aRouter;
    }


    // service logic
    // ---------------------------------------------------------------------------
    public boolean pushHeader(MessageRoutingInfo aHeader) {
        try {
            getRouter().process(aHeader);
            return true;
        } catch (Exception e) {
            logger.error("An error occurred routing: " + aHeader, e);
        }
        return false;
    }

    public ConcurrentMap<String, SerializedMessageBody> getBodyMap()
    {
        return getHazelcastClient().getMap( "body:" + getSocketId() );
    }

    public BlockingQueue<MessageRoutingInfo> getHeaderQueue() {
        throw new UnsupportedOperationException("This should never be called.");
    }
}
