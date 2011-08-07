package com.unitt.servicemanager.websocket;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unitt.commons.foundation.lifecycle.Destructable;
import com.unitt.commons.foundation.lifecycle.Initializable;
import com.unitt.servicemanager.response.ResponseQueueManager;
import com.unitt.servicemanager.util.ValidationUtil;


public abstract class MessagingWebSocketManager implements Initializable, Destructable
{
    private static Logger             logger = LoggerFactory.getLogger( MessagingWebSocketManager.class );

    private MessageSerializerRegistry serializerRegistry;
    private long                      queueTimeout;
    private ResponseQueueManager      responseQueueManager;
    private ServerWebSocket           serverSocket;
    protected boolean                 isInitialized;


    // constructors
    // ---------------------------------------------------------------------------
    public MessagingWebSocketManager()
    {
        this( null, 30000, null );
    }

    public MessagingWebSocketManager( MessageSerializerRegistry aSerializerRegistry, long aQueueTimeout, ResponseQueueManager aResponseQueueManager )
    {
        setQueueTimeout( aQueueTimeout );
        setSerializerRegistry( aSerializerRegistry );
        setResponseQueueManager( aResponseQueueManager );
    }


    // lifecycle logic
    // ---------------------------------------------------------------------------
    public void initialize()
    {
        String missing = null;

        // validate we have all properties
        if ( getResponseQueueManager() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing response queue manager. " );
        }
        if ( getSerializerRegistry() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing serializer registry. " );
        }
        if (getQueueTimeout() == 0)
        {
            setQueueTimeout( 30000 );
        }

        // fail out with appropriate message if missing anything
        if ( missing != null )
        {
            logger.error( missing );
            throw new IllegalStateException( missing );
        }

        // init response queue mgr
        if ( !getResponseQueueManager().isInitialized() )
        {
            getResponseQueueManager().initialize();
        }

        setInitialized( true );
    }

    public boolean isInitialized()
    {
        return isInitialized;
    }

    public void destroy()
    {
        if ( getResponseQueueManager() != null )
        {
            getResponseQueueManager().destroy();
        }
        setResponseQueueManager( null );
        setInitialized( false );
    }

    protected void setInitialized( boolean aIsInitialized )
    {
        isInitialized = aIsInitialized;
    }


    // websocket logic
    // ---------------------------------------------------------------------------
    public MessagingWebSocket createWebSocket()
    {
        MessagingWebSocket webSocket = internalCreateWebSocket();
        webSocket.initialize();
        getResponseQueueManager().addSocket( webSocket );
        logger.info( "Opened socket: {0}.", webSocket.getSocketId() );
        return webSocket;
    }

    public void destroyWebSocket( MessagingWebSocket aWebSocket )
    {
        String socketId = aWebSocket.getSocketId();
        getResponseQueueManager().removeSocket( aWebSocket );
        aWebSocket.destroy();
        logger.info( "Closed socket: {0}", socketId );
    }

    protected abstract MessagingWebSocket internalCreateWebSocket();


    // getters & setters
    // ---------------------------------------------------------------------------
    public MessageSerializerRegistry getSerializerRegistry()
    {
        return serializerRegistry;
    }

    public void setSerializerRegistry( MessageSerializerRegistry aSerializerRegistry )
    {
        serializerRegistry = aSerializerRegistry;
    }

    public long getQueueTimeout()
    {
        return queueTimeout;
    }

    public void setQueueTimeout( long aQueueTimeout )
    {
        queueTimeout = aQueueTimeout;
    }

    public ResponseQueueManager getResponseQueueManager()
    {
        return responseQueueManager;
    }

    public void setResponseQueueManager( ResponseQueueManager aResponseQueueManager )
    {
        responseQueueManager = aResponseQueueManager;
    }

    public ServerWebSocket getServerSocket()
    {
        return serverSocket;
    }

    public void setServerSocket( ServerWebSocket aServerSocket )
    {
        serverSocket = aServerSocket;
    }
}
