package com.unitt.framework.websocket;


import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import com.unitt.framework.websocket.WebSocketConnectConfig.WebSocketVersion;


/**
 * @author Josh Morris
 */
public class WebSocketHandshake
{
    private WebSocketConnectConfig clientConfig;
    private String                 clientSecKey;
    private byte[]                 clientHandshakeBytes;
    private WebSocketConnectConfig serverConfig;
    private String                 serverSecKey;
    private byte[]                 serverHandshakeBytes;
    private String                 expectedServerSecKey;


    // constructors
    // ---------------------------------------------------------------------------
    public WebSocketHandshake( WebSocketConnectConfig aClientConfig )
    {
        setClientConfig( aClientConfig );
        generateSecKeys();
    }

    public WebSocketHandshake( byte[] aClientHandshakeBytes, WebSocketConnectConfig aServerConfig )
    {
        setServerConfig( aServerConfig );
        setClientConfig( aClientHandshakeBytes );
        generateSecKeys();
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public WebSocketConnectConfig getClientConfig()
    {
        return clientConfig;
    }

    protected void setClientConfig( WebSocketConnectConfig aClientConfig )
    {
        clientConfig = aClientConfig;
    }

    protected void setClientConfig( byte[] aClientHandshakeBytes )
    {
        parseClientHandshakeBytes( aClientHandshakeBytes );
    }

    protected String getClientSecKey()
    {
        return clientSecKey;
    }

    protected void setClientSecKey( String aClientSecKey )
    {
        clientSecKey = aClientSecKey;
    }

    public WebSocketConnectConfig getServerConfig()
    {
        return serverConfig;
    }

    protected void setServerConfig( WebSocketConnectConfig aServerConfig )
    {
        serverConfig = aServerConfig;
    }

    protected void setServerConfig( byte[] aServerHandshakeBytes )
    {
        parseServerHandshakeBytes( aServerHandshakeBytes );
    }

    protected String getExpectedServerSecKey()
    {
        return expectedServerSecKey;
    }

    protected void setExpectedServerSecKey( String aExpectedServerSecKey )
    {
        expectedServerSecKey = aExpectedServerSecKey;
    }

    protected String getServerSecKey()
    {
        return serverSecKey;
    }

    protected void setServerSecKey( String aServerSecKey )
    {
        serverSecKey = aServerSecKey;
    }

    protected void setClientHandshakeBytes( byte[] aClientHandshakeBytes )
    {
        clientHandshakeBytes = aClientHandshakeBytes;
    }

    protected void setServerHandshakeBytes( byte[] aServerHandshakeBytes )
    {
        serverHandshakeBytes = aServerHandshakeBytes;
    }


    // handshake logic
    // ---------------------------------------------------------------------------
    protected void parseClientHandshakeBytes( byte[] aBytes )
    {
        // init
        Charset charset = Charset.forName( "US-ASCII" );
        setClientHandshakeBytes( aBytes );

        String handshake = new String( aBytes, charset );
        // make sure this is a http 1.1 GET request
        if ( handshake.startsWith( "GET" ) && handshake.contains( "HTTP/1.1" ) )
        {
            // loop through headers, filling out config
            WebSocketConnectConfig config = new WebSocketConnectConfig();
            String[] lines = handshake.split( "\r\n" );
            boolean connect = false;
            boolean upgrade = false;
            String secKey = null;
            for ( String line : lines )
            {
                String[] header = line.split( ":" );
                if ( header != null && header.length == 2 )
                {
                    // get header
                    String key = header[0] != null ? header[0].trim() : "";
                    String value = header[1] != null ? header[1].trim() : "";

                    // apply value, if we care about this header
                    if ( "Upgrade".equalsIgnoreCase( key ) )
                    {
                        // this must be websocket
                        upgrade = value.equalsIgnoreCase( "websocket" );
                    }
                    else if ( "Connection".equalsIgnoreCase( key ) )
                    {
                        // this must be websocket
                        connect = value.equalsIgnoreCase( "upgrade" );
                    }
                    else if ( "Sec-WebSocket-Protocol".equalsIgnoreCase( key ) )
                    {
                        //set available & selected protocols
                        List<String> availableProtocols = new ArrayList<String>();
                        String[] protocols = value.split( "," );
                        for ( String protocol : protocols )
                        {
                            if ( protocol != null )
                            {
                                String cleanProtocol = protocol.trim();
                                if ( cleanProtocol.length() > 0 )
                                {
                                    // can we choose this protocol, if we are
                                    // missing one
                                    if ( config.getSelectedProtocol() == null && containsCaseInsensitiveValue( value, getServerConfig().getAvailableProtocols() ) )
                                    {
                                        config.setSelectedProtocol( cleanProtocol );
                                    }
                                    availableProtocols.add( cleanProtocol );
                                }
                            }
                        }
                        if (!availableProtocols.isEmpty())
                        {
	                        config.setAvailableProtocols( availableProtocols );
                        }
                    }
                    else if ( "Sec-WebSocket-Key".equalsIgnoreCase( key ) )
                    {
                        secKey = value;
                    }
                    else if ( "Sec-WebSocket-Version".equalsIgnoreCase( key ) )
                    {
                        config.setWebSocketVersion( WebSocketVersion.fromSpecVersionValue(value) );
                    }
                    else if ( "Sec-WebSocket-Origin".equalsIgnoreCase( key ) )
                    {
                        //@todo: perform browser resource validation
                        config.setOrigin( value );
                    }
                    else if ( "Host".equalsIgnoreCase( key ) )
                    {
                        config.setHost(value);
                    }
                }
            }

            // verify connect/upgrade
            if ( connect && upgrade )
            {
                //verify that if the client requested a protocol - we chose one
                if (config.getAvailableProtocols() == null || config.getSelectedProtocol() != null)
                {
                    // apply parsed values
                    setClientConfig( config );
                    setClientSecKey( secKey );
                }
            }
        }
    }

    protected void parseServerHandshakeBytes( byte[] aBytes )
    {
        // init
        Charset charset = Charset.forName( "US-ASCII" );
        setServerHandshakeBytes( aBytes );

        String handshake = new String( aBytes, charset );
        // only allowed status is 101
        if ( handshake.startsWith( "HTTP/1.1 101" ) )
        {
            // loop through headers, filling out config
            WebSocketConnectConfig config = new WebSocketConnectConfig();
            String[] lines = handshake.split( "\r\n" );
            boolean connect = false;
            boolean upgrade = false;
            String secKey = null;
            for ( String line : lines )
            {
                String[] header = line.split( ":" );
                if ( header != null && header.length == 2 )
                {
                    // get header
                    String key = header[0] != null ? header[0].trim() : "";
                    String value = header[1] != null ? header[1].trim() : "";

                    // apply value, if we care about this header
                    if ( "Upgrade".equalsIgnoreCase( key ) )
                    {
                        // this must be websocket
                        upgrade = value.equalsIgnoreCase( "websocket" );
                    }
                    else if ( "Connection".equalsIgnoreCase( key ) )
                    {
                        // this must be websocket
                        connect = value.equalsIgnoreCase( "upgrade" );
                    }
                    else if ( "Sec-WebSocket-Protocol".equalsIgnoreCase( key ) )
                    {
                        config.setSelectedProtocol( value );
                    }
                    else if ( "Sec-WebSocket-Accept".equalsIgnoreCase( key ) )
                    {
                        secKey = value;
                    }
                }
            }

            // verify connect/upgrade
            if ( connect && upgrade )
            {
                // apply parsed values
                setServerSecKey( secKey );
                setServerConfig( config );
            }
        }
    }

    protected void generateSecKeys()
    {
        // init
        Charset charset = Charset.forName( "US-ASCII" );
        String temp = null;

        // determine client sec key, if it doesnt exist
        if ( getClientSecKey() == null )
        {
            temp = Long.toHexString( System.currentTimeMillis() );
            setClientSecKey( new String( Base64.encodeBase64( temp.getBytes( charset ) ), charset ) );
        }

        // determine server sec key
        temp = getClientSecKey() + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        byte[] bytes = DigestUtils.sha( temp );
        setExpectedServerSecKey( new String( Base64.encodeBase64( bytes ), charset ) );
    }

    public boolean verifyServerHandshake( byte[] aServerHandshakeBytes )
    {
        setServerConfig( aServerHandshakeBytes );

        if ( getServerConfig() != null )
        {
            // validate sec key, if configured to do so
            if ( getClientConfig().isVerifySecurityKey() )
            {
                return getExpectedServerSecKey().equalsIgnoreCase( getServerSecKey() );
            }

            return true;
        }

        return false;
    }

    public boolean verifyClientHandshake()
    {
        return getClientConfig() != null;
    }

    public byte[] getClientHandshakeBytes()
    {
        if ( clientHandshakeBytes == null )
        {
            StringBuilder output = new StringBuilder();
            output.append( "GET " + getResourcePath( getClientConfig().getUrl() ) + " HTTP/1.1\r\n" );
            output.append( "Upgrade: WebSocket\r\n" );
            output.append( "Connection: Upgrade\r\n" );
            output.append( "Host: " + getClientConfig().getUrl().getHost() + "\r\n" );
            output.append( "Sec-WebSocket-Origin: " + getClientConfig().getOrigin() + "\r\n" );
            if(getClientConfig().getAvailableProtocols() != null)
            {
                output.append( "Sec-WebSocket-Protocol: " + createCommaDelimitedList( getClientConfig().getAvailableProtocols() ) + "\r\n" );
            }
            output.append( "Sec-WebSocket-Key: " + getClientSecKey() + "\r\n" );
            output.append( "Sec-WebSocket-Version: " + getClientConfig().getWebSocketVersion().getSpecVersionValue() + "\r\n" );
            output.append( "\r\n" );
            clientHandshakeBytes = output.toString().getBytes( Charset.forName( "US-ASCII" ) );
        }

        return clientHandshakeBytes;
    }

    public byte[] getServerHandshakeBytes()
    {
        if ( serverHandshakeBytes == null )
        {
            StringBuilder output = new StringBuilder();
            output.append( "GET " + getResourcePath( getServerConfig().getUrl() ) + " HTTP/1.1\r\n" );
            output.append( "Upgrade: WebSocket\r\n" );
            output.append( "Connection: Upgrade\r\n" );
            output.append( "Sec-WebSocket-Protocol: " + getServerConfig().getSelectedProtocol() + "\r\n" );
            output.append( "Sec-WebSocket-Accept: " + getExpectedServerSecKey() + "\r\n" );
            output.append( "\r\n" );
            serverHandshakeBytes = output.toString().getBytes( Charset.forName( "US-ASCII" ) );
        }

        return serverHandshakeBytes;
    }

    protected String getResourcePath( URI aUrl )
    {
        if ( aUrl.getQuery() != null )
        {
            return aUrl.getPath() + "?" + aUrl.getQuery();
        }

        return aUrl.getPath();
    }

    protected String createCommaDelimitedList( List<?> aItems )
    {
        StringBuilder output = new StringBuilder();

        boolean isFirst = true;
        for ( Object item : aItems )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                output.append( "," );
            }
            output.append( item );
        }

        return output.toString();
    }

    protected boolean containsCaseInsensitiveValue( String aValue, List<String> aValuesToSearch )
    {
        for ( String itemToQuery : aValuesToSearch )
        {
            if ( itemToQuery != null && itemToQuery.equalsIgnoreCase( aValue ) )
            {
                return true;
            }
        }

        return false;
    }
}