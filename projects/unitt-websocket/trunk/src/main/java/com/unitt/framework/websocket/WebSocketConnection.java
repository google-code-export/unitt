package com.unitt.framework.websocket;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.slf4j.LoggerFactory;

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

    protected static final Charset utf8Charset = Charset.forName( "UTF-8" );
    private static org.slf4j.Logger          logger = LoggerFactory.getLogger( WebSocketConnection.class );

    private WebSocketObserver      observer;
    private WebSocketConnectConfig connectConfig;
    private NetworkSocketFacade    network;
    private WebSocketHandshake     handshake;
    private String                 closeMessage;
    private WebSocketState         state = WebSocketState.Disconnected;
    private Queue<WebSocketFragment> pendingFragments = new ArrayDeque<WebSocketFragment>();
    private boolean isClosing = false;
    private int maxPayloadSize;


    // constructors
    // ---------------------------------------------------------------------------
    public WebSocketConnection()
    {
        // default
    }

    public WebSocketConnection( WebSocketObserver observer, NetworkSocketFacade server, WebSocketConnectConfig connectConfig )
    {
        super();
        this.observer = observer;
        this.network = server;
        this.connectConfig = connectConfig;
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public WebSocketConnectConfig getConnectConfig()
    {
        return connectConfig;
    }

    public void setConnectConfig( WebSocketConnectConfig connectConfig )
    {
        this.connectConfig = connectConfig;
    }

    public WebSocketObserver getObserver()
    {
        return observer;
    }

    public void setObserver( WebSocketObserver observer )
    {
        this.observer = observer;
    }

    public NetworkSocketFacade getNetwork()
    {
        return network;
    }

    public void setNetwork( NetworkSocketFacade server )
    {
        this.network = server;
    }

    protected WebSocketHandshake getHandshake()
    {
        return handshake;
    }

    protected void setHandshake( WebSocketHandshake aHandshake )
    {
        handshake = aHandshake;
    }

    protected int getMaxPayloadSize()
    {
        return maxPayloadSize;
    }

    protected void setMaxPayloadSize( int aMaxPayloadSize )
    {
        maxPayloadSize = aMaxPayloadSize;
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

    protected void setCloseMessage( String closeMessage )
    {
        this.closeMessage = closeMessage;
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
        sendCloseToObserver( exception, getCloseMessage() );
    }

    public void onReceivedData( byte[] data )
    {
        if ( getState() != WebSocketState.Disconnected )
        {
            handleMessageData( data );
        }
    }


    // websocket interface
    // ---------------------------------------------------------------------------
    public void close( String message )
    {
        sendClose( message );
    }

    public void open()
    {
        getNetwork().connect( getConnectConfig() );
    }

    public void ping( String message )
    {
        sendPing( getSingleMessageBytes( message ) );
    }

    public void sendMessage( byte[] message )
    {
        sendBinary( message );
    }

    public void sendMessage( String message )
    {
        sendText( message );
    }


    // observer handling logic
    // ---------------------------------------------------------------------------
    protected void sendOpenToObserver(String protocol, List<String> extensions)
    {
        if (getObserver() != null)
        {
            try
            {
                getObserver().onOpen( protocol, extensions );
            }
            catch ( Exception e )
            {
                logger.warn("Observer threw an exception while handling open: protocol=" + protocol + ", extensions=" + extensions, e);
            }
        }
        else
        {
            logger.warn( "Missing observer. Cannot send open." );
        }
    }
    
    protected void sendErrorToObserver(Exception exception)
    {
        if (getObserver() != null)
        {
            try
            {
                getObserver().onError( exception );
            }
            catch ( Exception e )
            {
                logger.warn("Observer threw an exception while handling error: exception=" + exception, e);
            }
        }
        else
        {
            logger.warn( "Missing observer. Cannot send error." );
        }
    }
    
    protected void sendCloseToObserver(Exception exception, String message)
    {
        if (getObserver() != null)
        {
            try
            {
                getObserver().onClose( exception, getCloseMessage() );
            }
            catch ( Exception e )
            {
                logger.warn("Observer threw an exception while handling close: message=" + message + ", exception=" + exception, e);
            }
        }
        else
        {
            logger.warn( "Missing observer. Cannot send close." );
        }
    }
    
    protected void sendPongToObserver(String message)
    {
        if (getObserver() != null)
        {
            try
            {
                getObserver().onPong( message );
            }
            catch ( Exception e )
            {
                logger.warn("Observer threw an exception while handling pong: message=" + message, e);
            }
        }
        else
        {
            logger.warn( "Missing observer. Cannot send pong." );
        }
    }
    
    protected void sendBinaryMessageToObserver(byte[] message)
    {
        if (getObserver() != null)
        {
            try
            {
                getObserver().onBinaryMessage( message );
            }
            catch ( Exception e )
            {
                logger.warn("Observer threw an exception while handling binary message: " + message.length + " bytes.", e);
            }
        }
        else
        {
            logger.warn( "Missing observer. Cannot send binary message." );
        }
    }
    
    protected void sendTextMessageToObserver(String message)
    {
        if (getObserver() != null)
        {
            try
            {
                getObserver().onTextMessage( message );
            }
            catch ( Exception e )
            {
                logger.warn("Observer threw an exception while handling text message: " + message.length() + " characters.", e);
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
        setState(WebSocketState.Disconnecting);
        getNetwork().disconnect();
    }

    protected void handleCompleteFragment(WebSocketFragment fragment)
    {
        //if we are not in continuation and its final, dequeue
        if (fragment.isFinal() && fragment.getOpCode() != MessageOpCode.CONTINUATION)
        {
            pendingFragments.poll();
        }
        
        //continue to process
        switch (fragment.getOpCode()) 
        {
            case CONTINUATION:
                if (fragment.isFinal())
                {
                    try
                    {
                        handleCompleteFragments();
                    }
                    catch ( IOException e )
                    {
                        logger.error("Could not handle complete fragments.", e);
                        sendErrorToObserver( e );
                    }
                }
                break;
            case TEXT:
                if (fragment.isFinal())
                {
                    sendTextMessageToObserver( new String(fragment.getPayloadData(), utf8Charset ) );
                }
                break;
            case BINARY:
                if (fragment.isFinal())
                {
                    sendBinaryMessageToObserver( fragment.getPayloadData() );
                }
                break;
            case CLOSE:
                handleClose(fragment);
                break;
            case PING:
                handlePing(fragment.getPayloadData());
                break;
        }
    }

    protected void handleCompleteFragments() throws IOException
    {
        WebSocketFragment fragment = pendingFragments.poll();
        if (fragment != null)
        {
            //init
            ByteArrayOutputStream messageData = new ByteArrayOutputStream();
            MessageOpCode messageOpCode = fragment.getOpCode();
        
            //loop through, constructing single message
            while (fragment != null) 
            {
                messageData.write( fragment.getPayloadData() );
                fragment = pendingFragments.poll();
            }
            
            //handle final message contents        
            switch (messageOpCode) 
            {            
                case TEXT:
                    sendTextMessageToObserver( new String(messageData.toByteArray(), utf8Charset ) );
                    break;
                case BINARY:
                    sendBinaryMessageToObserver( messageData.toByteArray() );
                    break;
            }
        }
    }

    protected void handleClose(WebSocketFragment fragment)
    {
        if (isClosing())
        {
            closeSocket();
        }
        else
        {
            setClosing(true);
            close(getMessageFromBytes(fragment.getPayloadData()));
        }
    }

    protected void handlePing(byte[] message)
    {
        sendMessage(message, MessageOpCode.PONG);
        sendPongToObserver( getMessageFromBytes(message) );
    }

    protected void handleMessageData(byte[] data)
    {
        //grab last fragment, use if not complete
        WebSocketFragment fragment = pendingFragments.peek();
        if (fragment == null || fragment.isValid())
        {
            //assign web socket fragment since the last one was complete
            fragment =  new WebSocketFragment( data );
            pendingFragments.offer( fragment );
        }
        else if (fragment != null)
        {
            fragment.appendFragment( data );
            if (fragment.isValid()) 
            {
                fragment.parseContent();
            }
        }
        
        
        //if we have a complete fragment, handle it
        if (fragment.isValid()) 
        {
            //handle complete fragment
            handleCompleteFragment(fragment);
            
            //if we have extra data, handle it
            if (data.length > fragment.getMessageLength())
            {
                handleMessageData( WebSocketUtil.copySubArray( data, fragment.getMessageLength(), data.length - fragment.getMessageLength() ) );
            }
        }
    }
    
    protected String getMessageFromBytes(byte[] message)
    {
        if (message != null && message.length > 0)
        {
            return new String(message, utf8Charset );
        }
        
        return null;
    }
    
    protected byte[] getSingleMessageBytes(String message)
    {
        if (message != null)
        {
            byte[] results = message.getBytes( utf8Charset );
            if (results.length <= getMaxPayloadSize())
            {
                return results;
            }
        }
        
        return null;
    }
    
    protected void sendClose(String message)
    {
        sendMessage(new WebSocketFragment(MessageOpCode.CLOSE, true, sendWithMask(), getSingleMessageBytes(message)));
    }

    protected void sendText(String message)
    {
        //no reason to grab data if we won't send it anyways
        if (!isClosing())
        {
            sendMessage(message.getBytes( utf8Charset ), MessageOpCode.TEXT);
        }
    }

    protected void sendBinary(byte[] message)
    {
        sendMessage(message, MessageOpCode.BINARY);
    }

    protected void sendPing(byte[] message)
    {
        sendMessage(message, MessageOpCode.PING);
    }

    protected void sendMessage(byte[] message, MessageOpCode opCode)
    {
        if (!isClosing())
        {
            int messageLength = message.length;
            if (messageLength <= getMaxPayloadSize())
            {
                //create and send fragment
                WebSocketFragment fragment = new WebSocketFragment( opCode, true, sendWithMask(), message );
                sendMessage( fragment );
            }
            else
            {
                List<WebSocketFragment> fragments = new ArrayList<WebSocketFragment>();
                int fragmentCount = messageLength / getMaxPayloadSize();
                if (messageLength % getMaxPayloadSize() > 0)
                {
                    fragmentCount++;
                }
                
                //build fragments
                for (int i = 0; i < fragmentCount; i++)
                {
                    WebSocketFragment fragment = null;
                    int fragmentLength = getMaxPayloadSize();
                    if (i == 0)
                    {
                        fragment = new WebSocketFragment( opCode, false, sendWithMask(), WebSocketUtil.copySubArray( message, i * getMaxPayloadSize(), fragmentLength ) );
                    }
                    else if (i == fragmentCount - 1)
                    {
                        fragmentLength = messageLength % getMaxPayloadSize();
                        fragment = new WebSocketFragment( MessageOpCode.CONTINUATION, true, sendWithMask(), WebSocketUtil.copySubArray( message, i * getMaxPayloadSize(), fragmentLength ) );
                    }
                    else
                    {
                        fragment = new WebSocketFragment( MessageOpCode.CONTINUATION, false, sendWithMask(), WebSocketUtil.copySubArray( message, i * getMaxPayloadSize(), fragmentLength ) );
                    }
                    fragments.add(fragment);
                }
                
                //send fragments
                for (WebSocketFragment fragment : fragments) 
                {
                    sendMessage(fragment);
                }
            }  
        }
    }

    protected void sendMessage(WebSocketFragment fragment)
    {
        if (!isClosing() || fragment.getOpCode() == MessageOpCode.CLOSE)
        {
            try
            {
                getNetwork().write( fragment.getFragment() );
            }
            catch ( IOException e )
            {
                sendErrorToObserver( e );
            }
        }
    }
}
