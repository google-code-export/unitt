package com.unitt.servicemanager.routing;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unitt.servicemanager.service.ServiceDelegateJob;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;

// @todo: use message router executor
public abstract class MessageRouter
{
    private static Logger logger = LoggerFactory.getLogger( MessageRouter.class );

    private long          queueTimeoutInMillis;
    private int           corePoolSize;
    private int           maxPoolSize;
    private long          queueKeepAliveTimeInMillis;


    // constructors
    // ---------------------------------------------------------------------------
    public MessageRouter()
    {
    }

    public MessageRouter( long aQueueTimeoutInMillis, int aCorePoolSize, int aMaxPoolSize, long aQueueKeepAliveTimeInMillis )
    {
        super();
        queueTimeoutInMillis = aQueueTimeoutInMillis;
        corePoolSize = aCorePoolSize;
        maxPoolSize = aMaxPoolSize;
        queueKeepAliveTimeInMillis = aQueueKeepAliveTimeInMillis;
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

    public int getCorePoolSize()
    {
        return corePoolSize;
    }

    public void setCorePoolSize( int aCorePoolSize )
    {
        corePoolSize = aCorePoolSize;
    }

    public int getMaxPoolSize()
    {
        return maxPoolSize;
    }

    public void setMaxPoolSize( int aMaxPoolSize )
    {
        maxPoolSize = aMaxPoolSize;
    }

    public long getQueueKeepAliveTimeInMillis()
    {
        return queueKeepAliveTimeInMillis;
    }

    public void setQueueKeepAliveTimeInMillis( long aQueueKeepAliveTimeInMillis )
    {
        queueKeepAliveTimeInMillis = aQueueKeepAliveTimeInMillis;
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
