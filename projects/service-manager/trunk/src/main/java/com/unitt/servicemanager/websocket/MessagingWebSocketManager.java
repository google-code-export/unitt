package com.unitt.servicemanager.websocket;


import com.unitt.commons.foundation.lifecycle.Destructable;
import com.unitt.commons.foundation.lifecycle.Initializable;
import com.unitt.servicemanager.response.ResponseQueueManager;
import com.unitt.servicemanager.routing.PullsBody;
import com.unitt.servicemanager.routing.Pushes;
import com.unitt.servicemanager.routing.PutsBody;
import com.unitt.servicemanager.util.LifecycleHelper;
import com.unitt.servicemanager.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MessagingWebSocketManager implements Initializable, Destructable
{
    private static Logger             logger = LoggerFactory.getLogger( MessagingWebSocketManager.class );

    private MessageSerializerRegistry serializerRegistry;
    private ResponseQueueManager      responseQueueManager;
    private long queueTimeoutInMillis;
    private Pushes<MessageRoutingInfo> pushesHeader;
    private PullsBody pullsBody;
    private PutsBody putsBody;
    protected boolean                 isInitialized;


    // constructors
    // ---------------------------------------------------------------------------
    public MessagingWebSocketManager()
    {
    }

    public MessagingWebSocketManager(MessageSerializerRegistry aSerializerRegistry, ResponseQueueManager aResponseQueueManager, long aQueueTimeoutInMillis, Pushes<MessageRoutingInfo> aPushesHeader, PullsBody aPullsBody, PutsBody aPutsBody) {
        serializerRegistry = aSerializerRegistry;
        responseQueueManager = aResponseQueueManager;
        queueTimeoutInMillis = aQueueTimeoutInMillis;
        pushesHeader = aPushesHeader;
        pullsBody = aPullsBody;
        putsBody = aPutsBody;
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
        if ( getQueueTimeoutInMillis() <= 0 )
        {
            setQueueTimeoutInMillis(30000);
        }
        if ( getPullsBody() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing pulls body handler. " );
        }
        if ( getPutsBody() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing puts body handler. " );
        }
        if ( getPushesHeader() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing pushes header handler. " );
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

        // init handlers/managers
        LifecycleHelper.initialize(getResponseQueueManager());
        LifecycleHelper.initialize(getPullsBody());
        LifecycleHelper.initialize(getPushesHeader());
        LifecycleHelper.initialize(getPutsBody());

        logger.info("Started " + getClass().getSimpleName() + ": pullsBody=" + getPullsBody() + ", putsBody=" + getPutsBody() + ", pushesHeader=" + getPushesHeader() + ", responseManager=" + getResponseQueueManager());

        setInitialized( true );
    }

    public boolean isInitialized()
    {
        return isInitialized;
    }

    public void destroy()
    {
        LifecycleHelper.destroy(getResponseQueueManager());
        setResponseQueueManager( null );
        LifecycleHelper.destroy(getPullsBody());
        setPullsBody(null);
        LifecycleHelper.destroy(getPushesHeader());
        setPushesHeader(null);
        LifecycleHelper.destroy(getPutsBody());
        setPutsBody(null);
        setQueueTimeoutInMillis(0);
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
        webSocket.setPullsBody(getPullsBody());
        webSocket.setQueueTimeoutInMillis(getQueueTimeoutInMillis());
        webSocket.setPutsBody(getPutsBody());
        webSocket.setPushesHeader(getPushesHeader());
        webSocket.setSerializerRegistry(getSerializerRegistry());
        webSocket.initialize();
        getResponseQueueManager().addSocket( webSocket );
        return webSocket;
    }

    public void destroyWebSocket( MessagingWebSocket aWebSocket )
    {
        getResponseQueueManager().removeSocket( aWebSocket );
        LifecycleHelper.destroy(aWebSocket);
    }

    protected MessagingWebSocket internalCreateWebSocket( ServerWebSocket aServerWebSocket ) {
        return new MessagingWebSocket();
    }


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

    public long getQueueTimeoutInMillis() {
        return queueTimeoutInMillis;
    }

    public void setQueueTimeoutInMillis(long aQueueTimeoutInMillis) {
        queueTimeoutInMillis = aQueueTimeoutInMillis;
    }

    public Pushes<MessageRoutingInfo> getPushesHeader() {
        return pushesHeader;
    }

    public void setPushesHeader(Pushes<MessageRoutingInfo> aPushesHeader) {
        pushesHeader = aPushesHeader;
    }

    public PullsBody getPullsBody() {
        return pullsBody;
    }

    public void setPullsBody(PullsBody aPullsBody) {
        pullsBody = aPullsBody;
    }

    public PutsBody getPutsBody() {
        return putsBody;
    }

    public void setPutsBody(PutsBody aPutsBody) {
        putsBody = aPutsBody;
    }
}
