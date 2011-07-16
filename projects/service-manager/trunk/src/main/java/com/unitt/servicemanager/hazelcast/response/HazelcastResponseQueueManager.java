package com.unitt.servicemanager.hazelcast.response;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.unitt.servicemanager.response.ResponseQueueManager;
import com.unitt.servicemanager.response.UndeliverableMessageHandler;
import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.MessageResponse;
import com.unitt.servicemanager.websocket.MessagingWebSocket;

public class HazelcastResponseQueueManager extends ResponseQueueManager
{
    private static Logger        logger = LoggerFactory.getLogger( HazelcastResponseQueueManager.class );

    private HazelcastInstance    hazelcastClient;

    public HazelcastResponseQueueManager()
    {
        //default
    }

    public HazelcastResponseQueueManager( String aSocketQueueName, ThreadPoolExecutor aExecutor, Map<String, MessagingWebSocket> aSockets, HazelcastInstance aHazelcastClient, UndeliverableMessageHandler aUndeliverableMessageHandler )
    {
        super( aSocketQueueName, aExecutor, aSockets, aUndeliverableMessageHandler );
        setHazelcastClient( aHazelcastClient );
    }
    
        
    // lifecycle logic
    // ---------------------------------------------------------------------------
    public void initialize()
    {
        if ( getSockets() == null )
        {
            setSockets( new HashMap<String, MessagingWebSocket>() );
        }
        
        String missing = null;
        
        //validate we have all properties
        if (hazelcastClient == null)
        {
            ValidationUtil.appendMessage( missing, "Missing hazelcast client. ");
        }
        if (getSockets() == null)
        {
            ValidationUtil.appendMessage( missing, "Missing sockets map. ");
        }
        
        //fail out with appropriate message if missing anything
        if (missing != null)
        {
            logger.error(missing);
            throw new IllegalStateException( missing );
        }
        
        setInitialized( true );
    }

    public void destroy()
    {
        //clear hazelcast
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
    public BlockingQueue<MessageResponse> getSocketQueue()
    {
        String queueName = getSocketQueueName();
        if (queueName != null)
        {
            return getHazelcastClient().getQueue( queueName );
        }

        logger.error( "Could not determine socket queue: queueName=" + queueName );
        return null;
    }
}
