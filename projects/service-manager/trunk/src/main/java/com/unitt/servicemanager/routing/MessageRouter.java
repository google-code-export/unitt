package com.unitt.servicemanager.routing;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unitt.servicemanager.service.ServiceDelegateJob;
import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;


public abstract class MessageRouter
{
    private static Logger         logger        = LoggerFactory.getLogger( MessageRouter.class );

    private boolean               isInitialized = false;
    private long                  queueTimeoutInMillis;
    private int                   corePoolSize;
    private int                   maxPoolSize;
    private long                  queueKeepAliveTimeInMillis;
    private MessageRouterExecutor executor;


    // constructors
    // ---------------------------------------------------------------------------
    public MessageRouter()
    {
    }

    public MessageRouter( long aQueueTimeoutInMillis, int aCorePoolSize, int aMaxPoolSize, long aQueueKeepAliveTimeInMillis )
    {
        setCorePoolSize( aCorePoolSize );
        setMaxPoolSize( aMaxPoolSize );
        setQueueKeepAliveTimeInMillis( aQueueKeepAliveTimeInMillis );
        setQueueTimeoutInMillis( aQueueTimeoutInMillis );
    }


    // lifecycle logic
    // ---------------------------------------------------------------------------
    public boolean isInitialized()
    {
        return isInitialized;
    }

    public void initialize()
    {
        if ( !isInitialized() )
        {
            String missing = null;

            // validate we have all properties
            if ( getRoutingQueue() == null )
            {
                missing = ValidationUtil.appendMessage( missing, "Missing routing queue. " );
            }
            if ( getQueueTimeoutInMillis() < 1 )
            {
                missing = ValidationUtil.appendMessage( missing, "Missing valid queue timeout: " + getQueueTimeoutInMillis() + ". " );
            }
            if ( getCorePoolSize() < 1 )
            {
                missing = ValidationUtil.appendMessage( missing, "Missing valid core pool size: " + getCorePoolSize() + ". " );
            }
            if ( getQueueKeepAliveTimeInMillis() < 1 )
            {
                missing = ValidationUtil.appendMessage( missing, "Missing valid queue keep alive: " + getQueueKeepAliveTimeInMillis() + ". " );
            }
            if ( getMaxPoolSize() < 1 )
            {
                missing = ValidationUtil.appendMessage( missing, "Missing valid max pool size: " + getMaxPoolSize() + ". " );
            }

            // fail out with appropriate message if missing anything
            if ( missing != null )
            {
                logger.error( missing );
                throw new IllegalStateException( missing );
            }

            // apply values
            if ( executor == null )
            {
                executor = new MessageRouterExecutor( getCorePoolSize(), getMaxPoolSize(), getQueueKeepAliveTimeInMillis(), TimeUnit.MILLISECONDS, getRoutingQueue() );
            }
            executor.setRouter( this );

            setInitialized( true );
        }
    }

    public void destroy()
    {
        setCorePoolSize( 0 );
        setMaxPoolSize( 0 );
        setQueueKeepAliveTimeInMillis( 0 );
        setQueueTimeoutInMillis( 0 );
        try
        {
            if ( executor != null )
            {
                executor.shutdown();
            }
            setExecutor( null );
        }
        catch ( Exception e )
        {
            logger.error( "An error occurred shutting down the executor: " + getExecutor(), e );
        }
        setInitialized( false );
    }

    protected void setInitialized( boolean aIsInitialized )
    {
        isInitialized = aIsInitialized;
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

    public MessageRouterExecutor getExecutor()
    {
        return executor;
    }

    public void setExecutor( MessageRouterExecutor aExecutor )
    {
        executor = aExecutor;
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

    public abstract BlockingQueue<Runnable> getRoutingQueue();
}
