package com.unitt.servicemanager.hazelcast.websocket;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.unitt.servicemanager.response.ResponseQueueManager;
import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.MessageSerializerRegistry;
import com.unitt.servicemanager.websocket.MessagingWebSocket;
import com.unitt.servicemanager.websocket.MessagingWebSocketManager;
import com.unitt.servicemanager.websocket.ServerWebSocket;


public class HazelcastWebSocketFactory extends MessagingWebSocketManager
{
    private static Logger     logger = LoggerFactory.getLogger( HazelcastWebSocketFactory.class );

    private HazelcastInstance hazelcastClient;
    private long              queueTimeoutInMillis;
    private String            headerQueueName;


    // constructors
    // ---------------------------------------------------------------------------
    public HazelcastWebSocketFactory()
    {
        this( null, 30000, null, null, null );
    }

    public HazelcastWebSocketFactory( MessageSerializerRegistry aSerializers, long aQueueTimeout, String aHeaderQueueName, HazelcastInstance aHazelcastClient, ResponseQueueManager aResponseQueueManager )
    {
        super( aSerializers, aResponseQueueManager );
        setQueueTimeoutInMillis( aQueueTimeout );
        setHazelcastClient( aHazelcastClient );
        setHeaderQueueName( aHeaderQueueName );
    }


    // lifecycle logic
    // ---------------------------------------------------------------------------
    @Override
    public void initialize()
    {
        String missing = null;

        // validate we have all properties
        if ( getHazelcastClient() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing hazelcast client. " );
        }
        if ( getResponseQueueManager() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing response queue manager. " );
        }
        if ( getSerializerRegistry() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing serializer registry. " );
        }
        if ( getQueueTimeoutInMillis() == 0 )
        {
            setQueueTimeoutInMillis( 30000 );
        }
        if ( getHeaderQueueName() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing header queue name." );
        }

        // fail out with appropriate message if missing anything
        if ( missing != null )
        {
            logger.error( missing );
            throw new IllegalStateException( missing );
        }

        // init response queue mgr
        if ( !getResponseQueueManager().isInitialized() )
        {
            getResponseQueueManager().initialize();
        }

        setInitialized( true );
    }

    @Override
    public void destroy()
    {
        setHazelcastClient( null );
        setHeaderQueueName( null );
        setQueueTimeoutInMillis( 0 );
        super.destroy();
    }


    // websocket logic
    // ---------------------------------------------------------------------------
    @Override
    public MessagingWebSocket internalCreateWebSocket( ServerWebSocket aServerWebSocket )
    {
        logger.debug( "Creating web socket." );
        return new HazelcastWebSocket( getResponseQueueManager().getServerId(), getSerializerRegistry(), getQueueTimeoutInMillis(), getHeaderQueueName(), aServerWebSocket, getHazelcastClient() );
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

    public long getQueueTimeoutInMillis()
    {
        return queueTimeoutInMillis;
    }

    public void setQueueTimeoutInMillis( long aQueueTimeout )
    {
        queueTimeoutInMillis = aQueueTimeout;
    }

    public String getHeaderQueueName()
    {
        return headerQueueName;
    }

    public void setHeaderQueueName( String aHeaderQueueName )
    {
        headerQueueName = aHeaderQueueName;
    }
}
