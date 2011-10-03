package com.unitt.framework.websocket;


import java.io.Serializable;


public class HandshakeHeader implements Serializable
{
    private static final long serialVersionUID = -871819639390864715L;
    
    protected String key;
    protected String value;


    // constructors
    // ---------------------------------------------------------------------------
    public HandshakeHeader()
    {
        // default
    }

    public HandshakeHeader( String aKey, String aValue )
    {
        super();
        key = aKey;
        value = aValue;
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public String getKey()
    {
        return key;
    }

    public void setKey( String aKey )
    {
        key = aKey;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String aValue )
    {
        value = aValue;
    }
}
