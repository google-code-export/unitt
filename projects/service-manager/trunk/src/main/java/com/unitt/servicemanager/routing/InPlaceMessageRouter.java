package com.unitt.servicemanager.routing;


import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;
import com.unitt.servicemanager.worker.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public abstract class InPlaceMessageRouter implements Processor<MessageRoutingInfo>
{
    private static Logger                                     logger        = LoggerFactory.getLogger( InPlaceMessageRouter.class );

    private boolean                                           isInitialized = false;
    private long                                              queueTimeoutInMillis;


    // constructors
    // ---------------------------------------------------------------------------
    public InPlaceMessageRouter()
    {
    }

    public InPlaceMessageRouter(long aQueueTimeoutInMillis)
    {
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
            if ( getQueueTimeoutInMillis() < 1 )
            {
                missing = ValidationUtil.appendMessage( missing, "Missing valid queue timeout: " + getQueueTimeoutInMillis() + ". " );
            }

            // fail out with appropriate message if missing anything
            if ( missing != null )
            {
                logger.error( missing );
                throw new IllegalStateException( missing );
            }

            setInitialized( true );
        }
    }

    public void destroy()
    {
        setQueueTimeoutInMillis( 0 );
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


    // routing logic
    // ---------------------------------------------------------------------------
    public void process( MessageRoutingInfo aInfo )
    {
        try
        {
            getServiceQueue( aInfo ).offer( aInfo, getQueueTimeoutInMillis(), TimeUnit.MILLISECONDS );
        }
        catch ( Exception e )
        {
            logger.error( "[" + this + "] - Could not route message: " + aInfo, e );
        }
    }

    public abstract BlockingQueue<MessageRoutingInfo> getServiceQueue( MessageRoutingInfo aInfo );
}
