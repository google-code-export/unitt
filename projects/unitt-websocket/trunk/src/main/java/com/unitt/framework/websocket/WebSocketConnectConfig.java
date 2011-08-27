package com.unitt.framework.websocket;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Josh Morris
 */
public class WebSocketConnectConfig
{
    public enum WebSocketVersion
    {
        Version07("7"), Version08("8"), Version10("10");
        
        private String specVersionValue;
        
        private WebSocketVersion(String aSpecVersionValue)
        {
            specVersionValue = aSpecVersionValue;
        }
        
        public String getSpecVersionValue()
        {
            return specVersionValue;
        }
        
        public static WebSocketVersion fromSpecVersionValue(String aSpecVersionValue)
        {
            for (WebSocketVersion wsv : WebSocketVersion.values())
            {
                if (wsv.getSpecVersionValue().equals(aSpecVersionValue))
                {
                    return wsv;
                }
            }
            
            return null;
        }
    };
    
    private URI url;
    private String host;
    private String origin;
    private long timeoutInMillis;
    private boolean verifyTlsDomain;
    private List<String> availableProtocols;
    private String selectedProtocol;
    private boolean verifySecurityKey;
    private int maxPayloadSize;
    private String proxyHost;
    private int proxyPort = -1;
    private WebSocketVersion webSocketVersion = WebSocketVersion.Version07;
    
    
    // constructors
    // ---------------------------------------------------------------------------
    public WebSocketConnectConfig()
    {
        //default
    }

    
    // getters & setters
    // ---------------------------------------------------------------------------
    public URI getUrl()
    {
        return url;
    }

    public void setUrl( URI aUrl )
    {
        url = aUrl;
    }

    public String getOrigin()
    {
        return origin;
    }

    public void setOrigin( String aOrigin )
    {
        origin = aOrigin;
    }

    public boolean isSecure()
    {
        return getUrl() != null && getUrl().getScheme().equalsIgnoreCase( "wss" );
    }

    public long getTimeoutInMillis()
    {
        return timeoutInMillis;
    }

    public void setTimeoutInMillis( long aTimeoutInMillis )
    {
        timeoutInMillis = aTimeoutInMillis;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost( String aHost )
    {
        host = aHost;
    }

    public boolean isVerifyTlsDomain()
    {
        return verifyTlsDomain;
    }

    public void setVerifyTlsDomain( boolean aVerifyTlsDomain )
    {
        verifyTlsDomain = aVerifyTlsDomain;
    }

    public List<String> getAvailableProtocols()
    {
        return availableProtocols;
    }
    
    public void setAvailableProtocol(String availableProtocol)
    {
        availableProtocols = new ArrayList<String>();
        availableProtocols.add(availableProtocol);
    }

    public void setAvailableProtocols( List<String> availableProtocols )
    {
        this.availableProtocols = availableProtocols;
    }

    public String getSelectedProtocol()
    {
        return selectedProtocol;
    }

    public void setSelectedProtocol( String aSelectedProtocol )
    {
        selectedProtocol = aSelectedProtocol;
    }

    public boolean isVerifySecurityKey()
    {
        return verifySecurityKey;
    }

    public void setVerifySecurityKey( boolean aVerifyHandshake )
    {
        verifySecurityKey = aVerifyHandshake;
    }

    public int getMaxPayloadSize()
    {
        return maxPayloadSize;
    }

    public void setMaxPayloadSize( int aMaxPayloadSize )
    {
        maxPayloadSize = aMaxPayloadSize;
    }

    public WebSocketVersion getWebSocketVersion()
    {
        return webSocketVersion;
    }

    public void setWebSocketVersion( WebSocketVersion aWebSocketVersion )
    {
        webSocketVersion = aWebSocketVersion;
    }

    public String getProxyHost()
    {
        return proxyHost;
    }

    public void setProxyHost( String aProxyHost )
    {
        proxyHost = aProxyHost;
    }

    public int getProxyPort()
    {
        if (proxyPort < 0 && getUrl() != null)
        {
            proxyPort = getUrl().getPort();
        }
        
        return proxyPort;
    }

    public void setProxyPort( int aProxyPort )
    {
        proxyPort = aProxyPort;
    }
    
    public boolean hasProxy()
    {
        return getProxyHost() != null;
    }
}
