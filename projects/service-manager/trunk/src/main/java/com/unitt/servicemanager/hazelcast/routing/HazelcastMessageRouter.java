package com.unitt.servicemanager.hazelcast.routing;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.unitt.servicemanager.routing.MessageRouter;
import com.unitt.servicemanager.service.ServiceDelegateJob;
import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;

public class HazelcastMessageRouter extends MessageRouter
{
    private static Logger        logger = LoggerFactory.getLogger( HazelcastMessageRouter.class );

    private HazelcastInstance    hazelcastClient;
    private boolean isInitialized = false;

    public HazelcastMessageRouter()
    {
        //default
    }

    public HazelcastMessageRouter( long aQueueTimeoutInMillis, HazelcastInstance aHazelcastClient )
    {
        super( aQueueTimeoutInMillis );
        setHazelcastClient( aHazelcastClient );
    }
    
        
    // lifecycle logic
    // ---------------------------------------------------------------------------
    public void initialize()
    {
        String missing = null;
        
        //validate we have all properties
        if (hazelcastClient == null)
        {
            missing = ValidationUtil.appendMessage( missing, "Missing hazelcast client. ");
        }
        if (getQueueTimeoutInMillis() < 1)
        {
            missing = ValidationUtil.appendMessage( missing, "Missing valid queue timeout: " + getQueueTimeoutInMillis() + ". ");
        }
        
        //fail out with appropriate message if missing anything
        if (missing != null)
        {
	        logger.error(missing);
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
        //clear hazelcast
        setHazelcastClient( null );
        
        setInitialized( false );
    }
    
    protected void setInitialized(boolean aIsInitialized)
    {
        isInitialized = aIsInitialized;
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
    public BlockingQueue<ServiceDelegateJob> getServiceQueue( MessageRoutingInfo aInfo )
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

    protected String getServiceQueueName( MessageRoutingInfo aInfo )
    {
        if (aInfo != null)
        {
            return aInfo.getServiceName();
        }
        
        return null;
    }
}
