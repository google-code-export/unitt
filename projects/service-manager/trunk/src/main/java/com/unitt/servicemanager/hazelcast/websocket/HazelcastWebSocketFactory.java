package com.unitt.servicemanager.hazelcast.websocket;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.unitt.servicemanager.response.ResponseQueueManager;
import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.MessageSerializerRegistry;
import com.unitt.servicemanager.websocket.MessagingWebSocket;
import com.unitt.servicemanager.websocket.MessagingWebSocketManager;


public class HazelcastWebSocketFactory extends MessagingWebSocketManager
{
    private static Logger     logger = LoggerFactory.getLogger( HazelcastWebSocketFactory.class );

    private HazelcastInstance hazelcastClient;


    // constructors
    // ---------------------------------------------------------------------------
    public HazelcastWebSocketFactory()
    {
        this( null, 30000, null, null );
    }

    public HazelcastWebSocketFactory( MessageSerializerRegistry aSerializers, long aQueueTimeout, HazelcastInstance aHazelcastClient, ResponseQueueManager aResponseQueueManager )
    {
        super( aSerializers, aQueueTimeout, aResponseQueueManager );
        setHazelcastClient( aHazelcastClient );
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
            ValidationUtil.appendMessage( missing, "Missing hazelcast client. " );
        }
        if ( getResponseQueueManager() == null )
        {
            ValidationUtil.appendMessage( missing, "Missing response queue manager. " );
        }
        if ( getSerializerRegistry() == null )
        {
            ValidationUtil.appendMessage( missing, "Missing serializer registry. " );
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
        super.destroy();
    }

    // websocket logic
    // ---------------------------------------------------------------------------
    @Override
    public MessagingWebSocket internalCreateWebSocket()
    {
        return new HazelcastWebSocket( getSerializerRegistry(), getQueueTimeout(), getServerSocket(), getHazelcastClient() );
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
}
