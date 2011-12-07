package com.unitt.servicemanager.websocket;


import java.io.Serializable;


public class MessageResponse implements Serializable
{
    private static final long  serialVersionUID = -682578985869800122L;

    private MessageRoutingInfo header;
    private Object             body;
    private byte[] bodyBytes;


    // constructors
    // ---------------------------------------------------------------------------
    public MessageResponse()
    {
        // default
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public MessageRoutingInfo getHeader()
    {
        return header;
    }

    public void setHeader( MessageRoutingInfo aHeader )
    {
        header = aHeader;
    }

    public Object getBody()
    {
        return body;
    }

    public void setBody( Object aBody )
    {
        body = aBody;
    }

    public byte[] getBodyBytes() {
        return bodyBytes;
    }

    public void setBodyBytes(byte[] aBodyBytes) {
        bodyBytes = aBodyBytes;
    }

    // Object overrides
    // ------------------------------------------------
    @Override
    public String toString()
    {
        return "MessageResponse [" + getHeader() + "]";
    }
}
