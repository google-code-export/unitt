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
    private ResponseQueueManager      responseQueueManager;
    protected boolean                 isInitialized;


    // constructors
    // ---------------------------------------------------------------------------
    public MessagingWebSocketManager()
    {
        this( null, null );
    }

    public MessagingWebSocketManager( MessageSerializerRegistry aSerializerRegistry, ResponseQueueManager aResponseQueueManager )
    {
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
        setSerializerRegistry( null );
        setInitialized( false );
    }

    protected void setInitialized( boolean aIsInitialized )
    {
        isInitialized = aIsInitialized;
    }


    // websocket logic
    // ---------------------------------------------------------------------------
    public MessagingWebSocket createWebSocket( ServerWebSocket aServerWebSocket )
    {
        MessagingWebSocket webSocket = internalCreateWebSocket( aServerWebSocket );
        if ( webSocket.getServerWebSocket() == null )
        {
            webSocket.setServerWebSocket( aServerWebSocket );
        }
        if (webSocket.getServerId() == null)
        {
            webSocket.setServerId( getResponseQueueManager().getServerId() );
        }
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

    protected abstract MessagingWebSocket internalCreateWebSocket( ServerWebSocket aServerWebSocket );


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

    public ResponseQueueManager getResponseQueueManager()
    {
        return responseQueueManager;
    }

    public void setResponseQueueManager( ResponseQueueManager aResponseQueueManager )
    {
        responseQueueManager = aResponseQueueManager;
    }
}
