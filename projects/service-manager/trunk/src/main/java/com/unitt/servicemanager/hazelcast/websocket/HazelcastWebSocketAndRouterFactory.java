package com.unitt.servicemanager.hazelcast.websocket;


import com.hazelcast.core.HazelcastInstance;
import com.unitt.servicemanager.response.ResponseQueueManager;
import com.unitt.servicemanager.routing.InPlaceMessageRouter;
import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.MessageSerializerRegistry;
import com.unitt.servicemanager.websocket.MessagingWebSocket;
import com.unitt.servicemanager.websocket.MessagingWebSocketManager;
import com.unitt.servicemanager.websocket.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HazelcastWebSocketAndRouterFactory extends MessagingWebSocketManager
{
    private static Logger     logger = LoggerFactory.getLogger( HazelcastWebSocketAndRouterFactory.class );

    private HazelcastInstance hazelcastClient;
    private InPlaceMessageRouter router;


    // constructors
    // ---------------------------------------------------------------------------
    public HazelcastWebSocketAndRouterFactory()
    {
        this( null, null, null );
    }

    public HazelcastWebSocketAndRouterFactory(MessageSerializerRegistry aSerializers, HazelcastInstance aHazelcastClient, ResponseQueueManager aResponseQueueManager)
    {
        super( aSerializers, aResponseQueueManager );
        setHazelcastClient( aHazelcastClient );
    }


    // lifecycle logic
    // ---------------------------------------------------------------------------
    @Override
    public void initialize()
    {
        String missing = null;

        // validate we have all properties
        if ( getHazelcastClient() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing hazelcast client. " );
        }
        if ( getResponseQueueManager() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing response queue manager. " );
        }
        if ( getSerializerRegistry() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing serializer registry. " );
        }
        if ( getRouter() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing router. " );
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

    @Override
    public void destroy()
    {
        setHazelcastClient(null);
        super.destroy();
    }


    // websocket logic
    // ---------------------------------------------------------------------------
    @Override
    public MessagingWebSocket internalCreateWebSocket( ServerWebSocket aServerWebSocket )
    {
        logger.debug( "Creating web socket with router." );
        HazelcastWebSocketAndRouter socket = new HazelcastWebSocketAndRouter( getResponseQueueManager().getServerId(), getSerializerRegistry(), aServerWebSocket, getHazelcastClient() );
        socket.setRouter(getRouter());
        return socket;
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

    public InPlaceMessageRouter getRouter() {
        return router;
    }

    public void setRouter(InPlaceMessageRouter aRouter) {
        router = aRouter;
    }
}
