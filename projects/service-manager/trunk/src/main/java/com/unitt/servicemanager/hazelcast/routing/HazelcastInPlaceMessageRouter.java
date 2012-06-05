package com.unitt.servicemanager.hazelcast.routing;


import com.hazelcast.core.HazelcastInstance;
import com.unitt.servicemanager.routing.InPlaceMessageRouter;
import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;


public class HazelcastInPlaceMessageRouter extends InPlaceMessageRouter
{
    private static Logger     logger        = LoggerFactory.getLogger( HazelcastInPlaceMessageRouter.class );

    private HazelcastInstance hazelcastClient;
    private boolean           isInitialized = false;

    public HazelcastInPlaceMessageRouter()
    {
        // default
    }

    public HazelcastInPlaceMessageRouter(long aQueueTimeoutInMillis, HazelcastInstance aHazelcastClient)
    {
        super(aQueueTimeoutInMillis);
        setHazelcastClient(aHazelcastClient);
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


    // service logic
    // ---------------------------------------------------------------------------
    @Override
    public void process(MessageRoutingInfo aInfo) {
        logger.debug("Routing to \"" + getServiceQueueName(aInfo) + "\" for: " + aInfo);
        super.process(aInfo);
    }

    @Override
    public BlockingQueue<MessageRoutingInfo> getServiceQueue( MessageRoutingInfo aInfo )
    {
        if ( aInfo != null )
        {
            String queueName = getServiceQueueName(aInfo);
            if ( queueName != null )
            {
                return getHazelcastClient().getQueue( queueName );
            }
        }

        logger.error( "Could not determine service queue for routing info: " + aInfo );
        return null;
    }

    protected String getServiceQueueName( MessageRoutingInfo aInfo )
    {
        if ( aInfo != null )
        {
            return aInfo.getServiceName();
        }

        return null;
    }
}
