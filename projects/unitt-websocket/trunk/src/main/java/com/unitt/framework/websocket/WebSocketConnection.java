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
    private int maxPayloadSize = 4 * 1024;;


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
        if (aConnectConfig.getMaxPayloadSize() > 0)
        {
            setMaxPayloadSize( aConnectConfig.getMaxPayloadSize() );
        }
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

    protected int getMaxPayloadSize()
    {
        return maxPayloadSize;
    }

    protected void setMaxPayloadSize( int aMaxPayloadSize )
    {
        if (aMaxPayloadSize > 0)
        {
            maxPayloadSize = aMaxPayloadSize;
        }
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

    public void onReceivedData( byte[] aData )
    {
        if ( getState() != WebSocketState.Disconnected )
        {
            handleMessageData( aData );
        }
    }


    // websocket interface
    // ---------------------------------------------------------------------------
    public void close( String aMessage )
    {
        sendClose( aMessage );
    }

    public void open()
    {
        getNetwork().connect( getConnectConfig() );
    }

    public void ping( String aMessage )
    {
        sendPing( getSingleMessageBytes( aMessage ) );
    }

    public void sendMessage( byte[] aMessage )
    {
        sendBinary( aMessage );
    }

    public void sendMessage( String aMessage )
    {
        sendText( aMessage );
    }


    // observer handling logic
    // ---------------------------------------------------------------------------
    protected void sendOpenToObserver(String aProtocol, List<String> aExtensions)
    {
        if (getObserver() != null)
        {
            try
            {
                getObserver().onOpen( aProtocol, aExtensions );
            }
            catch ( Exception e )
            {
                logger.warn("Observer threw an exception while handling open: protocol=" + aProtocol + ", extensions=" + aExtensions, e);
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
    
    protected void sendCloseToObserver(Exception exception, String aMessage)
    {
        if (getObserver() != null)
        {
            try
            {
                getObserver().onClose( exception, getCloseMessage() );
            }
            catch ( Exception e )
            {
                logger.warn("Observer threw an exception while handling close: message=" + aMessage + ", exception=" + exception, e);
            }
        }
        else
        {
            logger.warn( "Missing observer. Cannot send close." );
        }
    }
    
    protected void sendPongToObserver(String aMessage)
    {
        if (getObserver() != null)
        {
            try
            {
                getObserver().onPong( aMessage );
            }
            catch ( Exception e )
            {
                logger.warn("Observer threw an exception while handling pong: message=" + aMessage, e);
            }
        }
        else
        {
            logger.warn( "Missing observer. Cannot send pong." );
        }
    }
    
    protected void sendBinaryMessageToObserver(byte[] aMessage)
    {
        if (getObserver() != null)
        {
            try
            {
                getObserver().onBinaryMessage( aMessage );
            }
            catch ( Exception e )
            {
                logger.warn("Observer threw an exception while handling binary message: " + aMessage.length + " bytes.", e);
            }
        }
        else
        {
            logger.warn( "Missing observer. Cannot send binary message." );
        }
    }
    
    protected void sendTextMessageToObserver(String aMessage)
    {
        if (getObserver() != null)
        {
            try
            {
                getObserver().onTextMessage( aMessage );
            }
            catch ( Exception e )
            {
                logger.warn("Observer threw an exception while handling text message: " + aMessage.length() + " characters.", e);
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

    protected void handleCompleteFragment(WebSocketFragment aFragment)
    {
        //if we are not in continuation and its final, dequeue
        if (aFragment.isFinal() && aFragment.getOpCode() != MessageOpCode.CONTINUATION)
        {
            pendingFragments.poll();
        }
        
        //continue to process
        switch (aFragment.getOpCode()) 
        {
            case CONTINUATION:
                if (aFragment.isFinal())
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
                if (aFragment.isFinal())
                {
                    sendTextMessageToObserver( new String(aFragment.getPayloadData(), utf8Charset ) );
                }
                break;
            case BINARY:
                if (aFragment.isFinal())
                {
                    sendBinaryMessageToObserver( aFragment.getPayloadData() );
                }
                break;
            case CLOSE:
                handleClose(aFragment);
                break;
            case PING:
                handlePing(aFragment.getPayloadData());
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

    protected void handleClose(WebSocketFragment aFragment)
    {
        if (isClosing())
        {
            closeSocket();
        }
        else
        {
            setClosing(true);
            close(getMessageFromBytes(aFragment.getPayloadData()));
        }
    }

    protected void handlePing(byte[] aMessage)
    {
        sendMessage(aMessage, MessageOpCode.PONG);
        sendPongToObserver( getMessageFromBytes(aMessage) );
    }

    protected void handleMessageData(byte[] aData)
    {
        //grab last fragment, use if not complete
        WebSocketFragment fragment = pendingFragments.peek();
        if (fragment == null || fragment.isValid())
        {
            //assign web socket fragment since the last one was complete
            fragment =  new WebSocketFragment( aData );
            pendingFragments.offer( fragment );
        }
        else if (fragment != null)
        {
            fragment.appendFragment( aData );
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
            if (aData.length > fragment.getMessageLength())
            {
                handleMessageData( WebSocketUtil.copySubArray( aData, fragment.getMessageLength(), aData.length - fragment.getMessageLength() ) );
            }
        }
    }
    
    protected String getMessageFromBytes(byte[] aMessage)
    {
        if (aMessage != null && aMessage.length > 0)
        {
            return new String(aMessage, utf8Charset );
        }
        
        return null;
    }
    
    protected byte[] getSingleMessageBytes(String aMessage)
    {
        if (aMessage != null)
        {
            byte[] results = aMessage.getBytes( utf8Charset );
            if (results.length <= getMaxPayloadSize())
            {
                return results;
            }
        }
        
        return null;
    }
    
    protected void sendClose(String aMessage)
    {
        sendMessage(new WebSocketFragment(MessageOpCode.CLOSE, true, sendWithMask(), getSingleMessageBytes(aMessage)));
    }

    protected void sendText(String aMessage)
    {
        System.out.println("Sending text message: " + !isClosing());
        //no reason to grab data if we won't send it anyways
        if (!isClosing())
        {
            sendMessage(aMessage.getBytes( utf8Charset ), MessageOpCode.TEXT);
        }
    }

    protected void sendBinary(byte[] aMessage)
    {
        sendMessage(aMessage, MessageOpCode.BINARY);
    }

    protected void sendPing(byte[] aMessage)
    {
        sendMessage(aMessage, MessageOpCode.PING);
    }

    protected void sendMessage(byte[] aMessage, MessageOpCode aOpCode)
    {
        System.out.println("Creating fragment & sending: " + !isClosing());
        if (!isClosing())
        {
            int messageLength = aMessage.length;
            System.out.println("Message length = " + messageLength);
            if (messageLength <= getMaxPayloadSize())
            {
                System.out.println("Sending one fragment.");
                //create and send fragment
                WebSocketFragment fragment = new WebSocketFragment( aOpCode, true, sendWithMask(), aMessage );
                sendMessage( fragment );
            }
            else
            {
                System.out.println("Length is greater than max payload size: " + getMaxPayloadSize());
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
                        fragment = new WebSocketFragment( aOpCode, false, sendWithMask(), WebSocketUtil.copySubArray( aMessage, i * getMaxPayloadSize(), fragmentLength ) );
                    }
                    else if (i == fragmentCount - 1)
                    {
                        fragmentLength = messageLength % getMaxPayloadSize();
                        fragment = new WebSocketFragment( MessageOpCode.CONTINUATION, true, sendWithMask(), WebSocketUtil.copySubArray( aMessage, i * getMaxPayloadSize(), fragmentLength ) );
                    }
                    else
                    {
                        fragment = new WebSocketFragment( MessageOpCode.CONTINUATION, false, sendWithMask(), WebSocketUtil.copySubArray( aMessage, i * getMaxPayloadSize(), fragmentLength ) );
                    }
                    fragments.add(fragment);
                }
                System.out.println("Sending " + fragments.size() + " fragments.");
                //send fragments
                for (WebSocketFragment fragment : fragments) 
                {
                    sendMessage(fragment);
                }
            }  
        }
    }

    protected void sendMessage(WebSocketFragment aFragment)
    {
        System.out.println("Sending Fragment: " + aFragment.getOpCode().name());
        if (!isClosing() || aFragment.getOpCode() == MessageOpCode.CLOSE)
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
}
