package com.unitt.framework.websocket;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.slf4j.LoggerFactory;

import com.unitt.framework.websocket.WebSocketConnectConfig.WebSocketVersion;
import com.unitt.framework.websocket.WebSocketFragment.MessageOpCode;


/**
 * @author Josh Morris
 */
public abstract class WebSocketConnection implements WebSocket, NetworkSocketObserver
{
    public enum WebSocketState
    {
        NeedsHandshake, Connected, Disconnecting, Disconnected
    };

    protected static final Charset   utf8Charset      = Charset.forName( "UTF-8" );
    private static org.slf4j.Logger  logger           = LoggerFactory.getLogger( WebSocketConnection.class );

    private WebSocketObserver        observer;
    private WebSocketConnectConfig   connectConfig;
    private NetworkSocketFacade      network;
    private WebSocketHandshake       handshake;
    private String                   closeMessage;
    private int                      closeStatus;
    private WebSocketState           state            = WebSocketState.Disconnected;
    private Queue<WebSocketFragment> pendingFragments = new ArrayDeque<WebSocketFragment>();
    private boolean                  isClosing        = false;


    // constructors
    // ---------------------------------------------------------------------------
    public WebSocketConnection()
    {
        // default
    }

    public WebSocketConnection( WebSocketObserver aObserver, NetworkSocketFacade aServer, WebSocketConnectConfig aConnectConfig )
    {
        super();
        observer = aObserver;
        network = aServer;
        connectConfig = aConnectConfig;
        network.setObserver( this );
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public WebSocketConnectConfig getConnectConfig()
    {
        return connectConfig;
    }

    public void setConnectConfig( WebSocketConnectConfig aConnectConfig )
    {
        connectConfig = aConnectConfig;
    }

    public WebSocketObserver getObserver()
    {
        return observer;
    }

    public void setObserver( WebSocketObserver aObserver )
    {
        observer = aObserver;
    }

    public NetworkSocketFacade getNetwork()
    {
        return network;
    }

    public void setNetwork( NetworkSocketFacade aServer )
    {
        network = aServer;
    }

    protected WebSocketHandshake getHandshake()
    {
        return handshake;
    }

    protected void setHandshake( WebSocketHandshake aHandshake )
    {
        handshake = aHandshake;
    }

    protected boolean throwsErrorOnInvalidUtf8()
    {
        return WebSocketVersion.Version10.equals( getConnectConfig().getWebSocketVersion() );
    }

    protected int getDefaultStatusCode( Exception aException )
    {
        if ( aException != null )
        {
            return WebSocketCloseStatusNormalButMissingStatus;
        }

        return WebSocketCloseStatusAbnormalButMissingStatus;
    }

    protected int getMaxPayloadSize()
    {
        return connectConfig.getMaxPayloadSize();
    }

    protected WebSocketState getState()
    {
        return state;
    }

    protected void setState( WebSocketState aState )
    {
        state = aState;
    }

    protected String getCloseMessage()
    {
        return closeMessage;
    }

    protected void setCloseMessage( String aCloseMessage )
    {
        closeMessage = aCloseMessage;
    }

    protected int getCloseStatus()
    {
        return closeStatus;
    }

    protected void setCloseStatus( int aCloseStatus )
    {
        closeStatus = aCloseStatus;
    }

    protected boolean isClosing()
    {
        return isClosing;
    }

    protected void setClosing( boolean aIsClosing )
    {
        isClosing = aIsClosing;
    }

    protected boolean sendWithMask()
    {
        return false;
    }


    // network socket observer interface
    // ---------------------------------------------------------------------------
    public abstract void onConnect();

    public void onDisconnect( Exception exception )
    {
        sendCloseToObserver( getCloseStatus(), getCloseMessage(), exception );
    }

    public void onReceivedData( byte[] aData )
    {
        if ( getState() != WebSocketState.Disconnected )
        {
            handleMessageData( aData );
        }
    }


    // websocket interface
    // ---------------------------------------------------------------------------
    public void close( int aStatus, String aMessage )
    {
        sendClose( aStatus, aMessage );
    }

    public void close()
    {
        close( 0, null );
    }

    public void open()
    {
        getNetwork().connect( getConnectConfig() );
    }

    public void ping( String aMessage )
    {
        try
        {
            sendPing( getSingleMessageBytes( aMessage ) );
        }
        catch ( CharacterCodingException e )
        {
            logger.error( "An error occurred while encoding to UTF8 to send text in ping message.", e );
            sendErrorToObserver( e );
            close( WebSocketCloseStatusInvalidUtf8, null );
        }
    }

    public void sendMessage( byte[] aMessage )
    {
        sendBinary( aMessage );
    }

    public void sendMessage( String aMessage )
    {
        sendText( aMessage );
    }

    public WebSocketReadyState getReadyState()
    {
        switch ( state )
        {
            case NeedsHandshake:
                return WebSocketReadyState.CONNECTING;
            case Connected:
                return WebSocketReadyState.OPEN;
            case Disconnecting:
                return WebSocketReadyState.CLOSING;
            case Disconnected:
                return WebSocketReadyState.CLOSED;
        }

        return WebSocketReadyState.CLOSED;
    }


    // observer handling logic
    // ---------------------------------------------------------------------------
    protected void sendOpenToObserver( String aProtocol, List<String> aExtensions )
    {
        if ( getObserver() != null )
        {
            try
            {
                getObserver().onOpen( aProtocol, aExtensions );
            }
            catch ( Exception e )
            {
                logger.warn( "Observer threw an exception while handling open: protocol=" + aProtocol + ", extensions=" + aExtensions, e );
            }
        }
        else
        {
            logger.warn( "Missing observer. Cannot send open." );
        }
    }

    protected void sendErrorToObserver( Exception exception )
    {
        if ( getObserver() != null )
        {
            try
            {
                getObserver().onError( exception );
            }
            catch ( Exception e )
            {
                logger.warn( "Observer threw an exception while handling error: exception=" + exception, e );
            }
        }
        else
        {
            logger.warn( "Missing observer. Cannot send error." );
        }
    }

    protected void sendCloseToObserver( int aStatusCode, String aCloseMessage, Exception aException )
    {
        if ( getObserver() != null )
        {
            try
            {
                int statusCode = aStatusCode;
                if ( statusCode <= 0 )
                {
                    statusCode = getDefaultStatusCode(aException);
                }
                getObserver().onClose( statusCode, aCloseMessage, aException );
            }
            catch ( Exception e )
            {
                logger.warn( "Observer threw an exception while handling close: status=" + aStatusCode + ", message=" + aCloseMessage + ", exception=" + aException, e );
            }
        }
        else
        {
            logger.warn( "Missing observer. Cannot send close." );
        }
    }

    protected void sendPongToObserver( String aMessage )
    {
        if ( getObserver() != null )
        {
            try
            {
                getObserver().onPong( aMessage );
            }
            catch ( Exception e )
            {
                logger.warn( "Observer threw an exception while handling pong: message=" + aMessage, e );
            }
        }
        else
        {
            logger.warn( "Missing observer. Cannot send pong." );
        }
    }

    protected void sendBinaryMessageToObserver( byte[] aMessage )
    {
        if ( getObserver() != null )
        {
            try
            {
                getObserver().onBinaryMessage( aMessage );
            }
            catch ( Exception e )
            {
                logger.warn( "Observer threw an exception while handling binary message: " + aMessage.length + " bytes.", e );
            }
        }
        else
        {
            logger.warn( "Missing observer. Cannot send binary message." );
        }
    }

    protected void sendTextMessageToObserver( String aMessage )
    {
        if ( getObserver() != null )
        {
            try
            {
                getObserver().onTextMessage( aMessage );
            }
            catch ( Exception e )
            {
                logger.warn( "Observer threw an exception while handling text message: " + aMessage.length() + " characters.", e );
            }
        }
        else
        {
            logger.warn( "Missing observer. Cannot send text message." );
        }
    }


    // websocket internal logic
    // ---------------------------------------------------------------------------
    protected void closeSocket()
    {
        setState( WebSocketState.Disconnecting );
        getNetwork().disconnect();
    }

    protected void handleCompleteFragment( WebSocketFragment aFragment )
    {
        // if we are not in continuation and its final, dequeue
        if ( aFragment.isFinal() && aFragment.getOpCode() != MessageOpCode.CONTINUATION )
        {
            pendingFragments.poll();
        }

        // continue to process
        switch ( aFragment.getOpCode() )
        {
            case CONTINUATION:
                if ( aFragment.isFinal() )
                {
                    try
                    {
                        handleCompleteFragments();
                    }
                    catch ( IOException e )
                    {
                        logger.error( "Could not handle complete fragments.", e );
                        sendErrorToObserver( e );
                    }
                }
                break;
            case TEXT:
                if ( aFragment.isFinal() )
                {
                    try
                    {
                        sendTextMessageToObserver( convertFromBytesToString( aFragment.getPayloadData() ) );
                    }
                    catch ( CharacterCodingException e )
                    {
                        logger.error( "An error occurred while decoding from UTF8 to receive a text message.", e );
                        sendErrorToObserver( e );
                        close( WebSocketCloseStatusInvalidUtf8, null );
                    }
                }
                break;
            case BINARY:
                if ( aFragment.isFinal() )
                {
                    sendBinaryMessageToObserver( aFragment.getPayloadData() );
                }
                break;
            case CLOSE:
                handleClose( aFragment );
                break;
            case PING:
                handlePing( aFragment.getPayloadData() );
                break;
        }
    }

    protected void handleCompleteFragments() throws IOException
    {
        WebSocketFragment fragment = pendingFragments.poll();
        if ( fragment != null )
        {
            // init
            ByteArrayOutputStream messageData = new ByteArrayOutputStream();
            MessageOpCode messageOpCode = fragment.getOpCode();

            // loop through, constructing single message
            while ( fragment != null )
            {
                messageData.write( fragment.getPayloadData() );
                fragment = pendingFragments.poll();
            }
            
            // handle final message contents
            switch ( messageOpCode )
            {
                case TEXT:
                    sendTextMessageToObserver( convertFromBytesToString( messageData.toByteArray() ) );
                    break;
                case BINARY:
                    sendBinaryMessageToObserver( messageData.toByteArray() );
                    break;
            }
        }
    }

    protected void handleClose( WebSocketFragment aFragment )
    {
        // parse close message
        boolean hasInvalidUtf8 = false;

        try
        {
            byte[] data = aFragment.getPayloadData();
            if ( data != null )
            {
                if ( data.length >= 2 )
                {
                    closeStatus = WebSocketUtil.convertBytesToShort( data, 0 );
                    if ( data.length > 2 )
                    {
                        closeMessage = convertFromBytesToString( WebSocketUtil.copySubArray( data, 2, data.length - 2 ) );
                    }
                }
            }
        }
        catch ( CharacterCodingException e )
        {
            logger.error( "An error occurred while decoding from UTF8 to get text in close message.", e );
            sendErrorToObserver( e );
            hasInvalidUtf8 = true;
        }

        // actually close
        if ( isClosing() )
        {
            closeSocket();
        }
        else
        {
            setClosing( true );
            if ( hasInvalidUtf8 )
            {
                close( WebSocketCloseStatusInvalidUtf8, null );
            }
            else
            {
                close( 0, null );
            }
        }
    }

    protected void handlePing( byte[] aMessage )
    {
        try
        {
            String message = getMessageFromBytes( aMessage );
            sendMessage( aMessage, MessageOpCode.PONG );
            sendPongToObserver( message );
        }
        catch ( CharacterCodingException e )
        {
            logger.error( "An error occurred while decoding from UTF8 to receive text in ping.", e );
            sendErrorToObserver( e );
            close( WebSocketCloseStatusInvalidUtf8, null );
        }
    }

    protected void handleMessageData( byte[] aData )
    {
        // grab last fragment, use if not complete
        WebSocketFragment fragment = pendingFragments.peek();
        if ( fragment == null || fragment.isValid() )
        {
            // assign web socket fragment since the last one was complete
            fragment = new WebSocketFragment( aData );
            
            pendingFragments.offer( fragment );
        }
        else if ( fragment != null )
        {
            fragment.appendFragment( aData );
            if ( fragment.canBeParsed() )
            {
                fragment.parseContent();
            }
        }


        // if we have a complete fragment, handle it
        if ( fragment.isValid() )
        {
            // handle complete fragment
            handleCompleteFragment( fragment );

            // if we have extra data, handle it
            if ( aData.length > fragment.getMessageLength() )
            {
                handleMessageData( WebSocketUtil.copySubArray( aData, fragment.getMessageLength(), aData.length - fragment.getMessageLength() ) );
            }
        }
    }

    protected String getMessageFromBytes( byte[] aMessage ) throws CharacterCodingException
    {
        if ( aMessage != null && aMessage.length > 0 )
        {
            return convertFromBytesToString( aMessage );
        }

        return null;
    }

    protected byte[] getCloseMessageBytes( Integer aStatus, String aMessage ) throws CharacterCodingException
    {
        byte[] results = null;

        if ( aMessage != null )
        {
            byte[] temp = convertFromStringToBytes( aMessage );
            if ( temp.length + 2 <= getMaxPayloadSize() )
            {
                byte[] statusBytes = new byte[] { new Integer(aStatus%0x100).byteValue(), new Integer(aStatus/0x100).byteValue() };
                results = WebSocketUtil.appendArray( statusBytes, temp );
            }
        }
        else if ( aStatus != null && aStatus > 0 )
        {
            results = new byte[] { new Integer(aStatus%0x100).byteValue(), new Integer(aStatus/0x100).byteValue() };
        }

        return results;
    }

    protected byte[] getSingleMessageBytes( String aMessage ) throws CharacterCodingException
    {
        if ( aMessage != null )
        {
            byte[] results = convertFromStringToBytes( aMessage );
            if ( results.length <= getMaxPayloadSize() )
            {
                return results;
            }
        }

        return null;
    }

    protected void sendClose( int aStatus, String aMessage )
    {
        try
        {
            setClosing( true );
            System.out.println("Closing with (" + aStatus + "): " + aMessage);
            System.out.println("Message Bytes:");
            WebSocketFragment message = new WebSocketFragment( MessageOpCode.CLOSE, true, sendWithMask(), getCloseMessageBytes( aStatus, aMessage ) );
            byte[] messageBytes = message.getFragment();
            System.out.println("Fragment:");
            WebSocketUtil.printBytes( messageBytes );
            sendMessage( message );
        }
        catch ( CharacterCodingException e )
        {
            logger.error( "An error occurred while encoding UTF8 to send text in close message.", e );
            sendErrorToObserver( e );
            close( WebSocketCloseStatusInvalidUtf8, null );
        }
    }

    protected void sendText( String aMessage )
    {
        // no reason to grab data if we won't send it anyways
        if ( !isClosing() )
        {
            try
            {
                sendMessage( convertFromStringToBytes( aMessage ), MessageOpCode.TEXT );
            }
            catch ( CharacterCodingException e )
            {
                logger.error( "An error occurred while encoding UTF8 to send text message.", e );
                sendErrorToObserver( e );
                close( WebSocketCloseStatusInvalidUtf8, null );
            }
        }
    }

    protected void sendBinary( byte[] aMessage )
    {
        sendMessage( aMessage, MessageOpCode.BINARY );
    }

    protected void sendPing( byte[] aMessage )
    {
        sendMessage( aMessage, MessageOpCode.PING );
    }

    protected void sendMessage( byte[] aMessage, MessageOpCode aOpCode )
    {
        if ( !isClosing() )
        {
            int messageLength = aMessage.length;
            if ( messageLength <= getMaxPayloadSize() )
            {
                // create and send fragment
                WebSocketFragment fragment = new WebSocketFragment( aOpCode, true, sendWithMask(), aMessage );
                sendMessage( fragment );
            }
            else
            {
                List<WebSocketFragment> fragments = new ArrayList<WebSocketFragment>();
                int fragmentCount = messageLength / getMaxPayloadSize();
                if ( messageLength % getMaxPayloadSize() > 0 )
                {
                    fragmentCount++;
                }

                // build fragments
                for ( int i = 0; i < fragmentCount; i++ )
                {
                    WebSocketFragment fragment = null;
                    int fragmentLength = getMaxPayloadSize();
                    if ( i == 0 )
                    {
                        fragment = new WebSocketFragment( aOpCode, false, sendWithMask(), WebSocketUtil.copySubArray( aMessage, i * getMaxPayloadSize(), fragmentLength ) );
                    }
                    else if ( i == fragmentCount - 1 )
                    {
                        fragmentLength = messageLength % getMaxPayloadSize();
                        if (fragmentLength == 0)
                        {
                            fragmentLength = getMaxPayloadSize();
                        }
                        fragment = new WebSocketFragment( MessageOpCode.CONTINUATION, true, sendWithMask(), WebSocketUtil.copySubArray( aMessage, i * getMaxPayloadSize(), fragmentLength ) );
                    }
                    else
                    {
                        fragment = new WebSocketFragment( MessageOpCode.CONTINUATION, false, sendWithMask(), WebSocketUtil.copySubArray( aMessage, i * getMaxPayloadSize(), fragmentLength ) );
                    }
                    fragments.add( fragment );
                }
                // send fragments
                for ( WebSocketFragment fragment : fragments )
                {
                    sendMessage( fragment );
                }
            }
        }
    }

    protected void sendMessage( WebSocketFragment aFragment )
    {
        if ( !isClosing() || aFragment.getOpCode() == MessageOpCode.CLOSE )
        {
            try
            {
                getNetwork().write( aFragment.getFragment() );
            }
            catch ( IOException e )
            {
                sendErrorToObserver( e );
            }
        }
    }


    // charset logic
    // ---------------------------------------------------------------------------
    protected String convertFromBytesToString( byte[] aData ) throws CharacterCodingException
    {
        if ( aData != null )
        {
            if ( aData.length > 0 )
            {
                if ( throwsErrorOnInvalidUtf8() )
                {
                    CharsetDecoder decoder = utf8Charset.newDecoder();
                    CharBuffer buffer = decoder.decode( ByteBuffer.wrap( aData ) );
                    return buffer.toString();
                }
            }
            else
            {
                return "";
            }

            return new String( aData, utf8Charset );
        }

        return null;
    }

    protected byte[] convertFromStringToBytes( String aData ) throws CharacterCodingException
    {
        if ( aData != null )
        {
            if ( aData.length() > 0 )
            {
                if ( throwsErrorOnInvalidUtf8() )
                {
                    CharsetEncoder encoder = utf8Charset.newEncoder();
                    ByteBuffer buffer = encoder.encode( CharBuffer.wrap( aData ) );
                    return buffer.array();
                }
            }
            else
            {
                return new byte[] {};
            }

            return aData.getBytes( utf8Charset );
        }

        return null;
    }
}
