package com.unitt.servicemanager.routing;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unitt.servicemanager.service.ServiceDelegateJob;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;


public abstract class MessageRouter
{
    private static Logger logger = LoggerFactory.getLogger( MessageRouter.class );

    private long          queueTimeoutInMillis;

    
    // constructors
    // ---------------------------------------------------------------------------
    public MessageRouter()
    {
        this( 0 );
    }

    public MessageRouter( long aQueueTimeoutInMillis )
    {
        setQueueTimeoutInMillis( aQueueTimeoutInMillis );
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public long getQueueTimeoutInMillis()
    {
        return queueTimeoutInMillis;
    }

    public void setQueueTimeoutInMillis( long aQueueTimeoutInMillis )
    {
        queueTimeoutInMillis = aQueueTimeoutInMillis;
    }


    // routing logic
    // ---------------------------------------------------------------------------
    public boolean route( MessageRoutingInfo aInfo )
    {
        try
        {
            return getServiceQueue( aInfo ).offer( new ServiceDelegateJob( aInfo ), getQueueTimeoutInMillis(), TimeUnit.MILLISECONDS );
        }
        catch ( Exception e )
        {
            logger.error( "[" + this + "] - Could not route message: " + aInfo, e );
            return false;
        }
    }

    public abstract BlockingQueue<ServiceDelegateJob> getServiceQueue( MessageRoutingInfo aInfo );
}
