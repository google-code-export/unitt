package com.unitt.framework.websocket;


import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import com.unitt.framework.websocket.WebSocketConnectConfig.WebSocketVersion;
import org.slf4j.LoggerFactory;


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

    private static org.slf4j.Logger  logger           = LoggerFactory.getLogger(WebSocketHandshake.class);


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

    protected List<HandshakeHeader> findHeaders(String aCaseInsensitiveKey, List<HandshakeHeader> aHeaders)
    {
        List<HandshakeHeader> results = new ArrayList<HandshakeHeader>();
        for (HandshakeHeader header : aHeaders)
        {
            if (aCaseInsensitiveKey.equalsIgnoreCase( header.getKey() ))
            {
                results.add(header);
            }
        }
        return results;
    }

    //@todo: send 400 error with available versions on mismatch
    protected void parseClientHandshakeBytes( byte[] aBytes )
    {
        // init
        Charset charset = Charset.forName( "US-ASCII" );
        setClientHandshakeBytes(aBytes);
        
        String handshake = "";
		try
        {
			handshake = new String( aBytes, charset.name() );
		}
        catch (UnsupportedEncodingException e)
        {
            logger.error("An error occurred during encoding", e);
		}
        // make sure this is a http 1.1 GET request
        if ( handshake.startsWith( "GET" ) && handshake.contains( "HTTP/1.1" ) )
        {
            //create headers & validate handshake
            boolean connect = false;
            boolean upgrade = false;
            String secKey = null;
            WebSocketConnectConfig config = new WebSocketConnectConfig();
            List<HandshakeHeader> headers = parseHeaders( handshake );
            config.setClientHeaders(headers);
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
            List<HandshakeHeader> matches = findHeaders("Sec-WebSocket-Protocol", headers);
            List<String> availableProtocols = new ArrayList<String>();
            for (HandshakeHeader match : matches)
            {
                if (match != null && match.getValue() != null)
                {
                    // set available & selected protocols
                    String[] protocols = match.getValue().split( "," );
                    for ( String protocol : protocols )
                    {
                        if ( protocol != null )
                        {
                            String cleanProtocol = protocol.trim();
                            if ( cleanProtocol.length() > 0 )
                            {
                                // can we choose this protocol, if we are
                                // missing one
                                if ( config.getSelectedProtocol() == null && containsCaseInsensitiveValue(match.getValue(), getServerConfig().getAvailableProtocols()) )
                                {
                                    config.setSelectedProtocol( cleanProtocol );
                                }
                                availableProtocols.add( cleanProtocol );
                            }
                        }
                    }
                }
            }
            if ( !availableProtocols.isEmpty() )
            {
                config.setAvailableProtocols( availableProtocols );
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
                config.setOrigin(header.getValue());
            }
            header = findHeader("Origin", headers);
            if (header != null && header.getValue() != null)
            {
                // @todo: perform browser resource validation
                config.setOrigin(header.getValue());
            }
            header = findHeader("Host", headers);
            if (header != null && header.getValue() != null)
            {
                config.setHost( header.getValue() );
            }
            matches = findHeaders("Sec-WebSocket-Extensions", headers);
            List<List<String>> headerExtensions = new ArrayList<List<String>>();
            for (HandshakeHeader match : matches)
            {
                if (match != null && match.getValue() != null)
                {
                    headerExtensions.addAll(getNestedLists(match.getValue()));
                }
            }
            List<String> selectedExtensions = getFirstCaseInsensitiveMatch(getServerConfig().getAvailableExtensions(), headerExtensions);
            if ( !headerExtensions.isEmpty() )
            {
                config.setAvailableExtensions( headerExtensions );
            }
            if ( selectedExtensions != null && !selectedExtensions.isEmpty() )
            {
                config.setSelectedExtensions( selectedExtensions );
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
        String handshake = "";
		try
        {
			handshake = new String( aBytes, charset.name() );
		}
        catch (UnsupportedEncodingException e)
        {
            logger.error("An error occurred during encoding", e);
		}

        //@todo: handle server 400 error and look for other versions
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
                List<String> selectedExtensions = getTrimmedValues(Arrays.asList(header.getValue().split(",")));
                if (containsCaseInsensitiveValues(selectedExtensions, config.getAvailableExtensions()))
                {
                    if ( selectedExtensions != null && !selectedExtensions.isEmpty() )
                    {
                        config.setSelectedExtensions( selectedExtensions );
                    }
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
            try
            {
				setClientSecKey( new String( Base64.encodeBase64( temp.getBytes( charset.name() ) ), charset.name() ) );
			}
            catch (UnsupportedEncodingException e)
            {
                logger.error("An error occurred during encoding", e);
			}
        }

        // determine server sec key
        temp = getClientSecKey() + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        byte[] bytes = DigestUtils.sha( temp );
        try
        {
			setExpectedServerSecKey( new String( Base64.encodeBase64( bytes ), charset.name() ) );
		}
        catch (UnsupportedEncodingException e)
        {
            logger.error("An error occurred during encoding", e);
		}
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
                getClientConfig().setClientHeaders(headers);
            }
            headers.add(new HandshakeHeader( "Upgrade", "WebSocket" ));
            headers.add(new HandshakeHeader( "Connection", "Upgrade" ));
            headers.add(new HandshakeHeader( "Host", getClientConfig().getHost() ));
            if (getClientConfig().getUseOrigin())
            {
                if (getClientConfig().getWebSocketVersion().equals(WebSocketVersion.Version07) || getClientConfig().getWebSocketVersion().equals(WebSocketVersion.Version08) || getClientConfig().getWebSocketVersion().equals(WebSocketVersion.Version10))
                {
                    headers.add(new HandshakeHeader( "Sec-WebSocket-Origin", getClientConfig().getOrigin() ));
                }
                else
                {
                    headers.add(new HandshakeHeader( "Origin", getClientConfig().getOrigin() ));
                }
            }
            if ( getClientConfig().getAvailableProtocols() != null )
            {
                headers.add(new HandshakeHeader( "Sec-WebSocket-Protocol", createCommaDelimitedList( getClientConfig().getAvailableProtocols() ) ));
            }
            if ( getClientConfig().getAvailableExtensions() != null )
            {
                headers.add(new HandshakeHeader( "Sec-WebSocket-Extensions", createNestedCommaDelimitedList(getClientConfig().getAvailableExtensions()) ));
            }
            headers.add(new HandshakeHeader( "Sec-WebSocket-Key", getClientSecKey() ));
            headers.add(new HandshakeHeader( "Sec-WebSocket-Version", getClientConfig().getWebSocketVersion().getSpecVersionValue() ));
            String handshake = buildHandshake( headers, resourcePath );
            try
            {
				clientHandshakeBytes = handshake.getBytes( "US-ASCII" );
			}
            catch (UnsupportedEncodingException e)
            {
                logger.error("An error occurred during decoding", e);
			}
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
            try
            {
				serverHandshakeBytes = handshake.getBytes( "US-ASCII" );
			}
            catch (UnsupportedEncodingException e)
            {
                logger.error("An error occurred during decoding", e);
			}
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
    
    protected List<List<String>> getNestedLists(String aValue)
    {
        List<List<String>> results = new ArrayList<List<String>>();
        for (String items : aValue.split(";"))
        {
            List<String> resultItem = new ArrayList<String>();
            for (String item : items.split(","))
            {
                 resultItem.add(item.trim());
            }
            if (!resultItem.isEmpty())
            {
                results.add(resultItem);
            }
        }
        return results;
    }

    protected String createNestedCommaDelimitedList(List<List<String>> aAvailableExtensions) {

        StringBuilder output = new StringBuilder();

        boolean isFirst = true;
        for ( List<String> items : aAvailableExtensions )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                output.append( ";" );
            }
            output.append( createCommaDelimitedList(items) );
        }

        return output.toString();
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
    
    protected List<String> getFirstCaseInsensitiveMatch(List<List<String>> aValues, List<List<String>> aValuesToSearch)
    {
        for (List<String> items : aValues)
        {
            if (containsCaseInsensitiveValues(items, aValuesToSearch))
            {
                return items;
            }
        }
        
        return null;
    }

    protected boolean containsCaseInsensitiveValues( List<String> aValues, List<List<String>> aValuesToSearch )
    {
        if (aValues == null && aValuesToSearch == null)
        {
            return true;
        }
        else if (aValues != null && !aValues.isEmpty() && aValuesToSearch != null && !aValuesToSearch.isEmpty())
        {
            for ( List<String> itemToQuery : aValuesToSearch )
            {
                if (!listsMatchCaseInsensitiveValues(aValues, itemToQuery))
                {
                    return false;
                }
            }
    
            return true;
        }
        
        return false;
    }
    
    protected boolean listsMatchCaseInsensitiveValues(List<String> aValues, List<String> aOtherValues)
    {
        if (aValues != null && aOtherValues != null)
        {
            if (aValues.size() == aOtherValues.size())
            {
                for (int i = 0; i < aValues.size(); i++)
                {
                    if (!aValues.get(i).trim().equalsIgnoreCase(aOtherValues.get(i).trim()))
                    {
                        return false;
                    }
                }
                return true;
            }
        }
        else if (aValues == null && aOtherValues == null)
        {
            return true;
        }
        
        return false;
    }
    
    protected List<String> getTrimmedValues(List<String> aValues)
    {
        List<String> results = new ArrayList<String>();
        for (String value : aValues)
        {
            results.add(value.trim());
        }
        return results;
    }
}
