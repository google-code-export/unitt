package com.unitt.servicemanager.hazelcast.service;


import com.hazelcast.core.HazelcastInstance;
import com.unitt.servicemanager.service.ServiceDelegate;
import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.MessageResponse;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;
import com.unitt.servicemanager.websocket.MessageSerializerRegistry;
import com.unitt.servicemanager.websocket.SerializedMessageBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;


public class HazelcastServiceDelegate extends ServiceDelegate
{
    private static Logger     logger = LoggerFactory.getLogger( HazelcastServiceDelegate.class );

    private HazelcastInstance hazelcastClient;
    private String            requestQueueName;

    public HazelcastServiceDelegate()
    {
        // default
    }

    public HazelcastServiceDelegate( Object aService, long aQueueTimeoutInMillis, MessageSerializerRegistry aReqistry, int aNumberOfWorkers, HazelcastInstance aHazelcastClient, String aRequestQueueName )
    {
        super( aService, aQueueTimeoutInMillis, aReqistry, aNumberOfWorkers );
        setHazelcastClient( aHazelcastClient );
        setRequestQueueName( aRequestQueueName );
    }


    // lifecycle logic
    // ---------------------------------------------------------------------------
    public void initialize()
    {
        String missing = null;

        // validate we have all properties
        if ( hazelcastClient == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing hazelcast client. " );
        }

        // fail out with appropriate message if missing anything
        if ( missing != null )
        {
            logger.error( missing );
            throw new IllegalStateException( missing );
        }

        super.initialize();

        logger.info("Started workers (" + getService() + ":" + getNumberOfWorkers() + ") using request queue: " + getRequestQueueName());
    }

    public void destroy()
    {
        // clear hazelcast
        setHazelcastClient( null );

        super.destroy();
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

    public String getRequestQueueName()
    {
        return requestQueueName;
    }

    public void setRequestQueueName( String aRequestQueueName )
    {
        requestQueueName = aRequestQueueName;
    }


    // service logic
    // ---------------------------------------------------------------------------
    @Override
    public ConcurrentMap<String, SerializedMessageBody> getBodyMap( MessageRoutingInfo aInfo )
    {
        if ( aInfo != null )
        {
            String mapName = getBodyMapName( aInfo );
            if ( mapName != null )
            {
                return getHazelcastClient().getMap( mapName );
            }
        }

        logger.error( "Could not determine body map for routing info: " + aInfo );
        return null;
    }

    protected String getBodyMapName( MessageRoutingInfo aInfo )
    {
        if ( aInfo != null )
        {
            return "body:" + aInfo.getWebSocketId();
        }

        return null;
    }

    @Override
    public BlockingQueue<MessageResponse> getDestinationQueue( MessageRoutingInfo aInfo )
    {
        if ( aInfo != null )
        {
            String queueName = getDestinationQueueName( aInfo );
            if ( queueName != null )
            {
                return getHazelcastClient().getQueue( queueName );
            }
        }

        logger.error( "Could not determine service queue for routing info: " + aInfo );
        return null;
    }

    protected String getDestinationQueueName( MessageRoutingInfo aInfo )
    {
        if ( aInfo != null )
        {
            return "outgoing:" + aInfo.getServerId();
        }

        return null;
    }

    @Override
    public BlockingQueue<MessageRoutingInfo> getRequestQueue()
    {
        return getHazelcastClient().getQueue( getRequestQueueName() );
    }
}
