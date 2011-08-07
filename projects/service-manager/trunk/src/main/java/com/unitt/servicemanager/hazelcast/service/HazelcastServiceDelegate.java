package com.unitt.servicemanager.hazelcast.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.unitt.servicemanager.service.ServiceDelegate;
import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.SerializedMessageBody;
import com.unitt.servicemanager.websocket.MessageResponse;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;

public class HazelcastServiceDelegate extends ServiceDelegate
{
    private static Logger        logger = LoggerFactory.getLogger( HazelcastServiceDelegate.class );

    private HazelcastInstance    hazelcastClient;

    public HazelcastServiceDelegate()
    {
        //default
    }

    public HazelcastServiceDelegate( Object aService, long aQueueTimeoutInMillis, HazelcastInstance aHazelcastClient )
    {
        super( aService, aQueueTimeoutInMillis );
        setHazelcastClient( aHazelcastClient );
    }
    
        
    // lifecycle logic
    // ---------------------------------------------------------------------------
    public void initialize()
    {
        String missing = null;
        
        //validate we have all properties
        if ( getSerializerRegistry() == null )
        {
            ValidationUtil.appendMessage( missing, "Missing serializer registry. " );
        }
        if (getService() == null)
        {
            ValidationUtil.appendMessage( missing, "Missing service instance. ");
        }
        if (hazelcastClient == null)
        {
            ValidationUtil.appendMessage( missing, "Missing hazelcast client. ");
        }
        if (getQueueTimeoutInMillis() < 1)
        {
            ValidationUtil.appendMessage( missing, "Missing valid queue timeout: " + getQueueTimeoutInMillis() + ". ");
        }
        
        //fail out with appropriate message if missing anything
        if (missing != null)
        {
            logger.error(missing);
            throw new IllegalStateException( missing );
        }
        
        super.initialize();
    }

    public void destroy()
    {
        //clear hazelcast
        setHazelcastClient( null );
        
        setInitialized( false );
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
    @Override
    public ConcurrentMap<String, SerializedMessageBody> getBodyMap( MessageRoutingInfo aInfo )
    {
        if (aInfo != null)
        {
            String mapName = aInfo.getServiceName();
            if (mapName != null)
            {
                return getHazelcastClient().getMap( mapName );
            }
        }

        logger.error( "Could not determine body map for routing info: " + aInfo );
        return null;
    }

    protected String getBodyMapName( MessageRoutingInfo aInfo )
    {
        if (aInfo != null)
        {
            return "body:" + aInfo.getServerId();
        }
        
        return null;
    }

    @Override
    public BlockingQueue<MessageResponse> getDestinationQueue( MessageRoutingInfo aInfo )
    {
        if (aInfo != null)
        {
            String queueName = aInfo.getServiceName();
            if (queueName != null)
            {
                return getHazelcastClient().getQueue( queueName );
            }
        }

        logger.error( "Could not determine service queue for routing info: " + aInfo );
        return null;
    }

    protected String getDestinationQueueName( MessageRoutingInfo aInfo )
    {
        if (aInfo != null)
        {
            return "outgoing:" + aInfo.getServerId();
        }
        
        return null;
    }
}
