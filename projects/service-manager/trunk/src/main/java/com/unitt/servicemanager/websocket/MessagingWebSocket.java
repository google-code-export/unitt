package com.unitt.servicemanager.websocket;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unitt.commons.foundation.lifecycle.Destructable;
import com.unitt.commons.foundation.lifecycle.Initializable;
import com.unitt.servicemanager.util.ValidationUtil;


public abstract class MessagingWebSocket implements Initializable, Destructable
{
    private static Logger     logger = LoggerFactory.getLogger( MessagingWebSocket.class );

    private ServerWebSocket   serverWebSocket;
    private boolean           isInitialized;
    private String            socketId;
    private MessageSerializerRegistry serializers;
    private long              queueTimeoutInMillis;


    // constructors
    // ---------------------------------------------------------------------------
    public MessagingWebSocket()
    {
        this( null, 30000, null );
    }

    public MessagingWebSocket( MessageSerializerRegistry aSerializers, long aQueueTimeoutInMillis, ServerWebSocket aServerWebSocket )
    {
        this( aSerializers, aQueueTimeoutInMillis, aServerWebSocket, null );
    }

    public MessagingWebSocket( MessageSerializerRegistry aSerializers, long aQueueTimeoutInMillis, ServerWebSocket aServerWebSocket, String aSocketId )
    {
        setSerializerRegistry( aSerializers );
        setQueueTimeoutInMillis( aQueueTimeoutInMillis );
        setServerWebSocket( aServerWebSocket );
        setSocketId( aSocketId );
    }


    // lifecycle logic
    // ---------------------------------------------------------------------------
    public void destroy()
    {
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
        //init
        if ( getSocketId() == null )
        {
            setSocketId( UUID.randomUUID().toString() );
        }
        if (getQueueTimeoutInMillis() == 0)
        {
            setQueueTimeoutInMillis( 30000 );
        }
        
        String missing = null;

        // validate we have all properties
        if ( getSerializerRegistry() == null )
        {
            ValidationUtil.appendMessage( missing, "Missing serializer registry. " );
        }
        if ( getServerWebSocket() == null )
        {
            ValidationUtil.appendMessage( missing, "Missing server web socket. " );
        }
        if ( getHeaderQueue() == null )
        {
            ValidationUtil.appendMessage( missing, "Missing header queue: " + getSocketId() + ". " );
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
            MessageSerializer serializer = getSerializerRegistry().getSerializer(aResponse.getHeader().getSerializerType());
            byte[] headerBytes = serializer.serializeHeader( aResponse.getHeader() );
            byte[] bodyBytes = serializer.serializeBody( aResponse.getBody() );
            output.write( intToBytes( headerBytes.length ) );
            output.write( headerBytes );
            output.write( bodyBytes );
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
                //do nothing
            }
        }
    }

    public void onMessage( byte[] aData )
    {
        int headerLength = bytesToShort( aData , 0);
        short serializerType = bytesToShort( aData, 2 );
        int bodyLength = aData.length - headerLength - 4;
        if ( bodyLength > 0 && headerLength > 0 && aData.length > bodyLength && aData.length > headerLength )
        {
            // assemble message parts
            byte[] headerBytes = new byte[headerLength];
            System.arraycopy( aData, 4, headerBytes, 0, headerLength );
            byte[] bodyBytes = new byte[bodyLength];
            System.arraycopy( aData, 4 + headerLength, bodyBytes, 0, bodyLength );

            // create message objects
            MessageSerializer serializer = getSerializerRegistry().getSerializer(serializerType);
            MessageRoutingInfo header = serializer.deserializeHeader( headerBytes );
            header.setSerializerType( serializerType );
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

    /**
     * Converts a 2 byte array of bytes to a short
     * 
     * @param b
     *            an array of 2 bytes
     * @return a short representing the short
     */
    public static final short bytesToShort( byte[] b, int aOffset )
    {
        short i = 0;
        i |= b[aOffset] & 0xFF;
        i <<= 8;
        i |= b[aOffset + 1] & 0xFF;
        return i;
    }

    /**
     * Converts a 4 byte array of bytes to an int
     * 
     * @param b
     *            an array of 4 bytes
     * @return an int representing the int
     */
    public static final int bytesToInt( byte[] b, int aOffset )
    {
        int i = 0;
        i |= b[aOffset] & 0xFF;
        i <<= 8;
        i |= b[aOffset + 1] & 0xFF;
        i <<= 8;
        i |= b[aOffset + 2] & 0xFF;
        i <<= 8;
        i |= b[aOffset + 3] & 0xFF;
        return i;
    }

    public static final byte[] intToBytes( int aValue )
    {
        return new byte[] { (byte) ( aValue >>> 24 & 0xff ), (byte) ( aValue >> 16 & 0xff ), (byte) ( aValue >> 8 & 0xff ), (byte) ( aValue & 0xff ) };
    }
}
