package com.unitt.framework.websocket;


import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
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
    protected List<HandshakeHeader> parseHeaders( String aHandshake )
    {
        List<HandshakeHeader> headers = new ArrayList<HandshakeHeader>();

        String[] lines = aHandshake.split( "\r\n" );
        for ( String line : lines )
        {
            String[] header = line.split( ":" );
            if ( header != null && header.length == 2 )
            {
                // get header
                String key = header[0] != null ? header[0].trim() : "";
                String value = header[1] != null ? header[1].trim() : "";
                if ( key.length() > 0 && value.length() > 0 )
                {
                    headers.add( new HandshakeHeader( key, value ) );
                }
            }
        }

        return headers;
    }
    
    protected String buildHandshake(List<HandshakeHeader> aHeaders, String aResource)
    {
        StringBuilder output = new StringBuilder();
        output.append( "GET " + aResource + " HTTP/1.1\r\n" );
        for (HandshakeHeader header : aHeaders)
        {
            output.append( header.getKey() + ": " + header.getValue() + "\r\n" );
        }
        output.append( "\r\n" );
        return output.toString();
    }
    
    protected HandshakeHeader findHeader(String aCaseInsensitiveKey, List<HandshakeHeader> aHeaders)
    {
        for (HandshakeHeader header : aHeaders)
        {
            if (aCaseInsensitiveKey.equalsIgnoreCase( header.getKey() ))
            {
                return header;
            }
        }
        
        return null;
    }

    protected void parseClientHandshakeBytes( byte[] aBytes )
    {
        // init
        Charset charset = Charset.forName( "US-ASCII" );
        setClientHandshakeBytes( aBytes );
        String handshake = new String( aBytes, charset );
        // make sure this is a http 1.1 GET request
        if ( handshake.startsWith( "GET" ) && handshake.contains( "HTTP/1.1" ) )
        {
            //create headers & validate handshake
            boolean connect = false;
            boolean upgrade = false;
            String secKey = null;
            WebSocketConnectConfig config = new WebSocketConnectConfig();
            List<HandshakeHeader> headers = parseHeaders( handshake );
            config.setClientHeaders( headers );
            HandshakeHeader header = findHeader("Upgrade", headers);
            if (header != null && header.getValue() != null)
            {
                upgrade = header.getValue().equalsIgnoreCase( "websocket" );
            }
            header = findHeader("Connection", headers);
            if (header != null && header.getValue() != null)
            {
                connect = header.getValue().equalsIgnoreCase( "upgrade" );
            }
            header = findHeader("Sec-WebSocket-Protocol", headers);
            if (header != null && header.getValue() != null)
            {
                // set available & selected protocols
                List<String> availableProtocols = new ArrayList<String>();
                String[] protocols = header.getValue().split( "," );
                for ( String protocol : protocols )
                {
                    if ( protocol != null )
                    {
                        String cleanProtocol = protocol.trim();
                        if ( cleanProtocol.length() > 0 )
                        {
                            // can we choose this protocol, if we are
                            // missing one
                            if ( config.getSelectedProtocol() == null && containsCaseInsensitiveValue( header.getValue(), getServerConfig().getAvailableProtocols() ) )
                            {
                                config.setSelectedProtocol( cleanProtocol );
                            }
                            availableProtocols.add( cleanProtocol );
                        }
                    }
                }
                if ( !availableProtocols.isEmpty() )
                {
                    config.setAvailableProtocols( availableProtocols );
                }
            }
            header = findHeader("Sec-WebSocket-Key", headers);
            if (header != null && header.getValue() != null)
            {
                secKey = header.getValue();
            }
            header = findHeader("Sec-WebSocket-Version", headers);
            if (header != null && header.getValue() != null)
            {
                config.setWebSocketVersion( WebSocketVersion.fromSpecVersionValue( header.getValue() ) );
            }
            header = findHeader("Sec-WebSocket-Origin", headers);
            if (header != null && header.getValue() != null)
            {
                // @todo: perform browser resource validation
                config.setOrigin( header.getValue() );
            }
            header = findHeader("Host", headers);
            if (header != null && header.getValue() != null)
            {
                config.setHost( header.getValue() );
            }
            header = findHeader("Sec-WebSocket-Extensions", headers);
            if (header != null && header.getValue() != null)
            {
                // set available & selected protocols
                List<String> availableExtensions = new ArrayList<String>();
                List<String> selectedExtensions = new ArrayList<String>();
                String[] extensions = header.getValue().split( "," );
                for ( String extension : extensions )
                {
                    if ( extension != null )
                    {
                        String cleanExtension = extension.trim();
                        if ( cleanExtension.length() > 0 )
                        {
                            if ( containsCaseInsensitiveValue( header.getValue(), getServerConfig().getAvailableExtensions() ) )
                            {
                                selectedExtensions.add( cleanExtension );
                            }
                            availableExtensions.add( cleanExtension );
                        }
                    }
                }
                if ( !availableExtensions.isEmpty() )
                {
                    config.setAvailableProtocols( availableExtensions );
                }
                if ( !selectedExtensions.isEmpty() )
                {
                    config.setSelectedExtensions( selectedExtensions );
                }
            }

            // verify connect/upgrade
            if ( connect && upgrade )
            {
                // verify that if the client requested a protocol - we chose one
                if ( config.getAvailableProtocols() == null || config.getSelectedProtocol() != null )
                {
                    // apply parsed values
                    setClientConfig( config );
                    getServerConfig().setClientHeaders( Collections.unmodifiableList( config.getClientHeaders() ) );
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
        if ( handshake.contains( "HTTP/1.1 101" ) )
        {
            // build headers and validate
            boolean connect = false;
            boolean upgrade = false;
            String secKey = null;
            WebSocketConnectConfig config = new WebSocketConnectConfig();
            List<HandshakeHeader> headers = parseHeaders( handshake );
            config.setServerHeaders( headers );
            HandshakeHeader header = findHeader("Upgrade", headers);
            if (header != null && header.getValue() != null)
            {
                upgrade = header.getValue().equalsIgnoreCase( "websocket" );
            }
            header = findHeader("Connection", headers);
            if (header != null && header.getValue() != null)
            {
                connect = header.getValue().equalsIgnoreCase( "upgrade" );
            }
            header = findHeader("Sec-WebSocket-Protocol", headers);
            if (header != null && header.getValue() != null)
            {
                config.setSelectedProtocol( header.getValue() );
            }
            header = findHeader("Sec-WebSocket-Accept", headers);
            if (header != null && header.getValue() != null)
            {
                secKey = header.getValue();
            }
            header = findHeader("Sec-WebSocket-Extensions", headers);
            if (header != null && header.getValue() != null)
            {
                // set available & selected protocols
                List<String> selectedExtensions = new ArrayList<String>();
                String[] extensions = header.getValue().split( "," );
                for ( String extension : extensions )
                {
                    if ( extension != null )
                    {
                        String cleanExtension = extension.trim();
                        if ( cleanExtension.length() > 0 )
                        {
                            selectedExtensions.add( cleanExtension );
                        }
                    }
                }
                if ( !selectedExtensions.isEmpty() )
                {
                    config.setSelectedExtensions( selectedExtensions );
                }
            }

            // verify connect/upgrade
            if ( connect && upgrade )
            {
                // apply parsed values
                setServerSecKey( secKey );
                setServerConfig( config );
                getClientConfig().setServerHeaders( Collections.unmodifiableList( config.getServerHeaders() ) );
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
            //build handshake info
            String resourcePath = getResourcePath( getClientConfig().getUrl() );
            List<HandshakeHeader> headers = getClientConfig().getClientHeaders();
            if (headers == null)
            {
                headers = new ArrayList<HandshakeHeader>();
                getClientConfig().setClientHeaders( headers );
            }
            headers.add(new HandshakeHeader( "Upgrade", "WebSocket" ));
            headers.add(new HandshakeHeader( "Connection", "Upgrade" ));
            headers.add(new HandshakeHeader( "Host", getClientConfig().getHost() ));
            headers.add(new HandshakeHeader( "Sec-WebSocket-Origin", getClientConfig().getOrigin() ));
            if ( getClientConfig().getAvailableProtocols() != null )
            {
                headers.add(new HandshakeHeader( "Sec-WebSocket-Protocol", createCommaDelimitedList( getClientConfig().getAvailableProtocols() ) ));
            }
            if ( getClientConfig().getAvailableExtensions() != null )
            {
                headers.add(new HandshakeHeader( "Sec-WebSocket-Extensions", createCommaDelimitedList( getClientConfig().getAvailableExtensions() ) ));
            }
            headers.add(new HandshakeHeader( "Sec-WebSocket-Key", getClientSecKey() ));
            headers.add(new HandshakeHeader( "Sec-WebSocket-Version", getClientConfig().getWebSocketVersion().getSpecVersionValue() ));
            String handshake = buildHandshake( headers, resourcePath );
            clientHandshakeBytes = handshake.getBytes( Charset.forName( "US-ASCII" ) );
        }

        return clientHandshakeBytes;
    }

    public byte[] getServerHandshakeBytes()
    {
        if ( serverHandshakeBytes == null )
        {
            //build handshake info
            String resourcePath = getResourcePath( getClientConfig().getUrl() );
            List<HandshakeHeader> headers = getServerConfig().getServerHeaders();
            if (headers == null)
            {
                headers = new ArrayList<HandshakeHeader>();
                getServerConfig().setServerHeaders( headers );
            }
            headers.add(new HandshakeHeader( "Upgrade", "WebSocket" ));
            headers.add(new HandshakeHeader( "Connection", "Upgrade" ));
            headers.add(new HandshakeHeader( "Sec-WebSocket-Protocol", getServerConfig().getSelectedProtocol() ));
            headers.add(new HandshakeHeader( "Sec-WebSocket-Extensions", createCommaDelimitedList(getServerConfig().getSelectedExtensions()) ));
            headers.add(new HandshakeHeader( "Sec-WebSocket-Accept", getExpectedServerSecKey() ));
            String handshake = buildHandshake( headers, resourcePath );
            serverHandshakeBytes = handshake.getBytes( Charset.forName( "US-ASCII" ) );
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