package com.unitt.servicemanager.websocket;


import com.unitt.commons.foundation.lifecycle.Destructable;
import com.unitt.commons.foundation.lifecycle.Initializable;
import com.unitt.servicemanager.util.ByteUtil;
import com.unitt.servicemanager.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;


public abstract class MessagingWebSocket implements Initializable, Destructable
{
    private static Logger             logger = LoggerFactory.getLogger( MessagingWebSocket.class );

    private ServerWebSocket           serverWebSocket;
    private boolean                   isInitialized;
    private String                    socketId;
    private String serverId;
    private MessageSerializerRegistry serializers;
    private long                      queueTimeoutInMillis;


    // constructors
    // ---------------------------------------------------------------------------
    public MessagingWebSocket()
    {
        this( null, null, 30000, null );
    }

    public MessagingWebSocket( String aServerId, MessageSerializerRegistry aSerializers, long aQueueTimeoutInMillis, ServerWebSocket aServerWebSocket )
    {
        this( aServerId, aSerializers, aQueueTimeoutInMillis, aServerWebSocket, null );
    }

    public MessagingWebSocket( String aServerId, MessageSerializerRegistry aSerializers, long aQueueTimeoutInMillis, ServerWebSocket aServerWebSocket, String aSocketId )
    {
        setServerId(aServerId);
        setSerializerRegistry( aSerializers );
        setQueueTimeoutInMillis( aQueueTimeoutInMillis );
        setServerWebSocket( aServerWebSocket );
        setSocketId( aSocketId );
    }


    // lifecycle logic
    // ---------------------------------------------------------------------------
    public void destroy()
    {
        setServerId(null);
        setSerializerRegistry( null );
        setServerWebSocket( null );
        setSocketId( null );
        setQueueTimeoutInMillis( 0 );
    }

    public boolean isInitialized()
    {
        return isInitialized;
    }

    public void initialize()
    {
        // init
        if ( getSocketId() == null )
        {
            setSocketId( UUID.randomUUID().toString() );
        }
        if ( getQueueTimeoutInMillis() == 0 )
        {
            setQueueTimeoutInMillis( 30000 );
        }

        String missing = null;

        // validate we have all properties
        if ( getSerializerRegistry() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing serializer registry. " );
        }
        if ( getServerWebSocket() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing server web socket. " );
        }
        if ( getServerId() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing server id. " );
        }

        // fail out with appropriate message if missing anything
        if ( missing != null )
        {
            logger.error( missing );
            throw new IllegalStateException( missing );
        }

        setInitialized( true );
    }

    protected void setInitialized( boolean aIsInitialized )
    {
        isInitialized = aIsInitialized;
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public String getSocketId()
    {
        return socketId;
    }

    public void setSocketId( String aSocketId )
    {
        socketId = aSocketId;
    }

    public String getServerId()
    {
        return serverId;
    }

    public void setServerId( String aServerId )
    {
        serverId = aServerId;
    }

    public MessageSerializerRegistry getSerializers()
    {
        return serializers;
    }

    public void setSerializers( MessageSerializerRegistry aSerializers )
    {
        serializers = aSerializers;
    }

    public MessageSerializerRegistry getSerializerRegistry()
    {
        return serializers;
    }

    public void setSerializerRegistry( MessageSerializerRegistry aSerializers )
    {
        serializers = aSerializers;
    }

    public long getQueueTimeoutInMillis()
    {
        return queueTimeoutInMillis;
    }

    public void setQueueTimeoutInMillis( long aQueueTimeoutInMillis )
    {
        queueTimeoutInMillis = aQueueTimeoutInMillis;
    }

    public ServerWebSocket getServerWebSocket()
    {
        return serverWebSocket;
    }

    public void setServerWebSocket( ServerWebSocket aServerWebSocket )
    {
        serverWebSocket = aServerWebSocket;
    }


    // web socket logic
    // ---------------------------------------------------------------------------
    public void send( MessageResponse aResponse )
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try
        {
            MessageSerializer serializer = getSerializerRegistry().getSerializer( aResponse.getHeader().getSerializerType() );
            byte[] headerBytes = serializer.serializeHeader( aResponse.getHeader() );
            byte[] bodyBytes = aResponse.getBodyBytes();
            if (bodyBytes == null && aResponse.getBody() != null)
            {
                bodyBytes = serializer.serializeBody( aResponse.getBody() );
            }
            output.write( ByteUtil.convertShortToBytes( new Integer( headerBytes.length ).shortValue() ) );
            output.write( ByteUtil.convertShortToBytes( aResponse.getHeader().getSerializerType() ) );
            output.write( headerBytes );
            if (bodyBytes != null)
            {
                output.write( bodyBytes );
            }
            byte[] bytesOut = output.toByteArray();
            getServerWebSocket().sendMessage( bytesOut );
        }
        catch ( Exception e )
        {
            logger.error( "An error occurred sending the message response: " + aResponse, e );
        }
        finally
        {
            try
            {
                output.close();
            }
            catch ( IOException e )
            {
                // do nothing
            }
        }
    }

    public void onMessage( byte[] aData )
    {
        short headerLength = ByteUtil.convertBytesToShort( aData, 0 );
        short serializerType = ByteUtil.convertBytesToShort( aData, 2 );
        int bodyLength = aData.length - headerLength - 4;
        if ( bodyLength > 0 && headerLength > 0 && aData.length > bodyLength && aData.length > headerLength )
        {
            // assemble message parts
            byte[] headerBytes = new byte[headerLength];
            System.arraycopy( aData, 4, headerBytes, 0, headerLength );
            byte[] bodyBytes = new byte[bodyLength];
            System.arraycopy( aData, 4 + headerLength, bodyBytes, 0, bodyLength );

            // create message objects
            MessageSerializer serializer = getSerializerRegistry().getSerializer( serializerType );
            if (serializer == null)
            {
                logger.debug("Could not find serializer: " + serializerType);
            }
            MessageRoutingInfo header = serializer.deserializeHeader( headerBytes );
            header.setSerializerType( serializerType );
            header.setWebSocketId( getSocketId() );
            header.setServerId( getServerId() );
            SerializedMessageBody body = new SerializedMessageBody( bodyBytes );

            // put body bytes in map
            pushBody( header.getUid(), body );

            // push message header into routing queue
            if ( !pushHeader( header ) )
            {
                removeBody( header.getUid() );
            }
        }
    }

    public boolean pushHeader( MessageRoutingInfo aHeader )
    {
        try
        {
            return getHeaderQueue().offer( aHeader, getQueueTimeoutInMillis(), TimeUnit.MILLISECONDS );
        }
        catch ( Exception e )
        {
            logger.error( "Could not push header to be routed: " + aHeader, e );
            return false;
        }
    }

    public void pushBody( String aUid, SerializedMessageBody aBody )
    {
        getBodyMap().put( aUid, aBody );
    }

    public void removeBody( String aUid )
    {
        getBodyMap().remove( aUid );
    }

    public abstract ConcurrentMap<String, SerializedMessageBody> getBodyMap();

    public abstract BlockingQueue<MessageRoutingInfo> getHeaderQueue();
}
