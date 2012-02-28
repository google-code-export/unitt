package com.unitt.framework.websocket;

/**
 * @author Josh Morris
 */
public interface WebSocket
{
	//indicates a normal closure, meaning whatever purpose the connection was established for has been fulfilled
    public static final int WebSocketCloseStatusNormal = 1000; 
	//indicates that an endpoint is "going away", such as a server going down, or a browser having navigated away from a page
    public static final int WebSocketCloseStatusEndpointGone = 1001; 
    //indicates that an endpoint is terminating the connection due to a protocol error
    public static final int WebSocketCloseStatusProtocolError = 1002; 
	//indicates that an endpoint is terminating the connection because it has received a type of data it cannot accept (e.g. an endpoint that understands only text data MAY send this if it receives a binary message)
    public static final int WebSocketCloseStatusInvalidDataType = 1003; 
	//indicates that an endpoint is terminating the connection because it has received a message that is too large (prior to rfc6455)
    public static final int WebSocketCloseStatusLegacyMessageTooLarge = 1004;
    
    //rev08 & later
	//designated for use in applications expecting a status code to indicate that no status code was actually present
    public static final int WebSocketCloseStatusNormalButMissingStatus = 1005; 
	//designated for use in applications expecting a status code to indicate that the connection was closed abnormally, e.g. without sending or receiving a Close control frame.
    public static final int WebSocketCloseStatusAbnormalButMissingStatus = 1006; 
    
    //rev10 & later
	//indicates that an endpoint is terminating the connection because it has received data that was supposed to be UTF-8 (such as in a text frame) that was in fact not valid UTF-8
    public static final int WebSocketCloseStatusInvalidUtf8 = 1007;
    public static final int WebSocketCloseStatusInvalidData = 1007;

    //indicates that an endpoint is terminating the connection because it has received a message that violates its policy.  This is a generic status code that can be returned when there is no other more suitable status code or if there is a need to hide specific details about the policy.
    public static final int WebSocketCloseStatusViolatesPolicy = 1008;
    //indicates that an endpoint is terminating the connection because it has received a message that is too large
    public static final int WebSocketCloseStatusMessageTooLarge = 1009;
    //indicates that an endpoint (client) is terminating the connection because it has expected the server to negotiate one or more extension, but the server didn't return them in the response message of the WebSocket handshake
    public static final int WebSocketCloseStatusMissingExtensions = 1010; 
    //indicates that a server is terminating the connection because it encountered an unexpected condition that prevented it from fulfilling the request
    public static final int WebSocketCloseStatusServerError = 1011; 
    //indicate that the connection was closed due to a failure to perform a TLS handshake
    public static final int WebSocketCloseStatusTlsHandshakeError = 1015;


    public enum WebSocketReadyState 
    {
        CONNECTING, //The connection has not yet been established.
        OPEN, //The WebSocket connection is established and communication is possible.
        CLOSING, //The connection is going through the closing handshake.
        CLOSED //The connection has been closed or could not be opened
    };
    
    public WebSocketReadyState getReadyState();
    
    public void open();
    public void close();
    public void close(int aStatus, String aMessage);
    public void ping(String aMessage);
    public void sendMessage(byte[] aMessage);
    public void sendMessage(String aMessage);
    
}
