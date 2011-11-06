package com.unitt.servicemanager.hazelcast.response;


import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.unitt.servicemanager.response.ResponseQueueManager;
import com.unitt.servicemanager.response.ResponseWriterExecutor;
import com.unitt.servicemanager.response.UndeliverableMessageHandler;
import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.MessagingWebSocket;


public class HazelcastResponseQueueManager extends ResponseQueueManager
{
    private static Logger     logger = LoggerFactory.getLogger( HazelcastResponseQueueManager.class );

    private HazelcastInstance hazelcastClient;
    private String            socketQueueName;

    public HazelcastResponseQueueManager()
    {
        // default
    }

    public HazelcastResponseQueueManager( int aCorePoolSize, int aMaxPoolSize, long aQueueKeepAliveTimeInMillis, Map<String, MessagingWebSocket> aSockets, UndeliverableMessageHandler aUndeliverableMessageHandler, String aSocketQueueName, HazelcastInstance aHazelcastClient )
    {
        super( aCorePoolSize, aMaxPoolSize, aQueueKeepAliveTimeInMillis, aSockets, aUndeliverableMessageHandler );
        setHazelcastClient( aHazelcastClient );
        setSocketQueueName( aSocketQueueName );
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

        // validate we have all properties
        if ( hazelcastClient == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing hazelcast client. " );
        }
        if ( getSockets() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing sockets map. " );
        }
        if ( socketQueueName == null )
        {
            try
            {
                socketQueueName = InetAddress.getLocalHost().getHostName() + "::" + System.currentTimeMillis();
            }
            catch ( Exception e )
            {
                socketQueueName = "Unknown IP Address::" + System.currentTimeMillis();
            }
        }

        // fail out with appropriate message if missing anything
        if ( missing != null )
        {
            logger.error( missing );
            throw new IllegalStateException( missing );
        }
        
        if (getExecutor() == null)
        {
            setExecutor( new ResponseWriterExecutor( getCorePoolSize(), getMaxPoolSize(), getQueueKeepAliveTimeInMillis(), TimeUnit.MILLISECONDS, getSocketQueue()) );
        }
        getExecutor().setWriter( this );

        setInitialized( true );
    }

    public void destroy()
    {
        super.destroy();
        // clear hazelcast
        try
        {
            IQueue<?> queue = (IQueue<?>) getSocketQueue();
            if ( queue != null )
            {
                queue.destroy();
            }
        }
        catch ( Exception e )
        {
            logger.error( "An error occurred cleaning up the socket queue: " + this, e );
        }
        setHazelcastClient( null );
        setSocketQueueName( null );
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

    public String getSocketQueueName()
    {
        return socketQueueName;
    }

    public void setSocketQueueName( String aSocketQueueName )
    {
        socketQueueName = aSocketQueueName;
    }


    // service logic
    // ---------------------------------------------------------------------------
    @Override
    public BlockingQueue<Runnable> getSocketQueue()
    {
        String queueName = getSocketQueueName();
        if ( queueName != null )
        {
            return getHazelcastClient().getQueue( queueName );
        }

        logger.error( "Could not determine socket queue: queueName=" + queueName );
        return null;
    }
}
