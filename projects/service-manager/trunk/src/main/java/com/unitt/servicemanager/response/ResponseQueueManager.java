package com.unitt.servicemanager.response;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IQueue;
import com.unitt.commons.foundation.lifecycle.Destructable;
import com.unitt.commons.foundation.lifecycle.Initializable;
import com.unitt.servicemanager.websocket.MessageResponse;
import com.unitt.servicemanager.websocket.MessagingWebSocket;


public abstract class ResponseQueueManager implements Initializable, Destructable, ResponseWriter
{
    private static Logger                   logger = LoggerFactory.getLogger( ResponseQueueManager.class );

    protected boolean                       isInitialized;

    private ThreadPoolExecutor              executor;
    private Map<String, MessagingWebSocket> sockets;
    private String                          socketQueueName;
    private UndeliverableMessageHandler     undeliverableMessageHandler;


    // constructors
    // ---------------------------------------------------------------------------
    public ResponseQueueManager()
    {
        this( null, null, null, null );
    }

    public ResponseQueueManager( String aSocketQueueName, ThreadPoolExecutor aExecutor, Map<String, MessagingWebSocket> aSockets, UndeliverableMessageHandler aUndeliverableMessageHandler )
    {
        setExecutor( aExecutor );
        setSocketQueueName( aSocketQueueName );
        setSockets(aSockets);
        setUndeliverableMessageHandler( aUndeliverableMessageHandler );
    }


    // lifecycle logic
    // ---------------------------------------------------------------------------
    public void initialize()
    {
        if ( !isInitialized() )
        {
            if ( sockets == null )
            {
                sockets = new HashMap<String, MessagingWebSocket>();
            }
            setInitialized( true );
        }
    }

    public boolean isInitialized()
    {
        return isInitialized;
    }

    public void destroy()
    {
        try
        {
            if ( sockets != null )
            {
                sockets.clear();
            }
            setSockets( null );
        }
        catch ( Exception e )
        {
            logger.error( "An error occurred clearing the socket map: " + this, e );
        }
        try
        {
            IQueue<?> queue = (IQueue<?>) getSocketQueue();
            if ( queue != null )
            {
                queue.destroy();
            }
            setSocketQueueName( null );
        }
        catch ( Exception e )
        {
            logger.error( "An error occurred cleaning up the socket queue: " + this, e );
        }
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
    public ThreadPoolExecutor getExecutor()
    {
        return executor;
    }

    public void setExecutor( ThreadPoolExecutor aExecutor )
    {
        executor = aExecutor;
    }

    protected Map<String, MessagingWebSocket> getSockets()
    {
        return sockets;
    }

    protected void setSockets( Map<String, MessagingWebSocket> aSockets )
    {
        sockets = aSockets;
    }

    public UndeliverableMessageHandler getUndeliverableMessageHandler()
    {
        return undeliverableMessageHandler;
    }

    public void setUndeliverableMessageHandler( UndeliverableMessageHandler aUndeliverableMessageHandler )
    {
        undeliverableMessageHandler = aUndeliverableMessageHandler;
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
    public void addSocket( MessagingWebSocket aSocket )
    {
        getSockets().put( aSocket.getSocketId(), aSocket );
    }

    public void removeSocket( MessagingWebSocket aSocket )
    {
        getSockets().remove( aSocket.getSocketId() );
    }

    public MessagingWebSocket getSocket( String aSocketId )
    {
        return getSockets().get( aSocketId );
    }

    public abstract BlockingQueue<MessageResponse> getSocketQueue();


    // response writer logic
    // ---------------------------------------------------------------------------
    public boolean write( MessageResponse aResponse )
    {
        //send the message down the websocket
        try
        {
            String socketId = aResponse.getHeader().getWebsocketId();
            MessagingWebSocket socket = getSocket( socketId );
            if ( socket != null )
            {
                socket.send( aResponse );
                return true;
            }
        }
        catch ( Exception e )
        {
            logger.error( "[" + this + "] - Could not write message: " + aResponse, e );
            return false;
        }

        // it didn't write, send to dead letter queue, if any
        if (getUndeliverableMessageHandler() != null)
        {
            getUndeliverableMessageHandler().handleUndeliverableMessage( aResponse );
        }

        return false;
    }
}
