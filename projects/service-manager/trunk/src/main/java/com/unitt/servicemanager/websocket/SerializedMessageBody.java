package com.unitt.servicemanager.websocket;

import java.io.Serializable;
import java.util.Date;

public class SerializedMessageBody implements Serializable
{
    private static final long serialVersionUID = 3985635432751666961L;
    
    private byte[] contents;
    private Date expiryTime;
    
    
    // constructors
    // ---------------------------------------------------------------------------
    public SerializedMessageBody()
    {
        //default
    }
    
    public SerializedMessageBody(byte[] aContents)
    {
        this(aContents, null);
    }
    
    public SerializedMessageBody(byte[] aContents, Date aExpiryTime)
    {
        setContents(aContents);
        setExpiryTime( aExpiryTime );
    }

    
    // getters & setters
    // ---------------------------------------------------------------------------
    public byte[] getContents()
    {
        return contents;
    }

    public void setContents( byte[] aContents )
    {
        contents = aContents;
    }

    public Date getExpiryTime()
    {
        return expiryTime;
    }

    public void setExpiryTime( Date aExpiryTime )
    {
        expiryTime = aExpiryTime;
    }
}
