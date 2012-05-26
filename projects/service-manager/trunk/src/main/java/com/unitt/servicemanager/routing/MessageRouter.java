package com.unitt.servicemanager.routing;


import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;
import com.unitt.servicemanager.worker.DelegateMaster;
import com.unitt.servicemanager.worker.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public abstract class MessageRouter implements Processor<MessageRoutingInfo>
{
    private static Logger                                     logger        = LoggerFactory.getLogger( MessageRouter.class );

    private boolean                                           isInitialized = false;
    private long                                              queueTimeoutInMillis;
    private int                                               numberOfWorkers;
    private DelegateMaster<MessageRoutingInfo, MessageRouter> workers;


    // constructors
    // ---------------------------------------------------------------------------
    public MessageRouter()
    {
    }

    public MessageRouter( long aQueueTimeoutInMillis, int aNumberOfThreads )
    {
        setNumberOfWorkers( aNumberOfThreads );
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
            if ( getNumberOfWorkers() < 1 )
            {
                missing = ValidationUtil.appendMessage( missing, "Missing number of Threads: " + getNumberOfWorkers() + ". " );
            }

            // fail out with appropriate message if missing anything
            if ( missing != null )
            {
                logger.error( missing );
                throw new IllegalStateException( missing );
            }

            // apply values
            if ( workers == null )
            {
                workers = new DelegateMaster<MessageRoutingInfo, MessageRouter>( getClass().getSimpleName(), getRoutingQueue(), this, getQueueTimeoutInMillis(), getNumberOfWorkers() );
            }

            setInitialized( true );
        }
    }

    public void destroy()
    {
        setNumberOfWorkers( 0 );
        setQueueTimeoutInMillis( 0 );
        workers = null;
        setInitialized( false );
    }

    public void start() {
        if (workers == null) {
            if (!isInitialized()) {
                initialize();
            }
            if (workers == null) {
                throw new IllegalStateException("Missing workers. Cannot start.");
            }
        }
        workers.startup();
    }

    public void stop() {
        try
        {
            if ( workers != null )
            {
                workers.shutdown();
            }
        }
        catch ( Exception e )
        {
            logger.error( "An error occurred shutting down the workers.", e );
        }
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

    public DelegateMaster<MessageRoutingInfo, MessageRouter> getWorkers()
    {
        return workers;
    }

    public void setWorkers( DelegateMaster<MessageRoutingInfo, MessageRouter> aWorkers )
    {
        workers = aWorkers;
    }

    public int getNumberOfWorkers()
    {
        return numberOfWorkers;
    }

    public void setNumberOfWorkers( int aNumberOfThreads )
    {
        numberOfWorkers = aNumberOfThreads;
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

    public abstract BlockingQueue<MessageRoutingInfo> getRoutingQueue();
}
