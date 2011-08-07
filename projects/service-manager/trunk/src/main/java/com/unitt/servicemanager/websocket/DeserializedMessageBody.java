package com.unitt.servicemanager.websocket;

import java.io.Serializable;
import java.util.List;

public class DeserializedMessageBody implements Serializable
{
    private static final long serialVersionUID = -7008244096427895639L;
    
    private List<Object> serviceMethodArguments;

    
    // getters & setters
    // ---------------------------------------------------------------------------
    public List<Object> getServiceMethodArguments()
    {
        return serviceMethodArguments;
    }

    public void setServiceMethodArguments( List<Object> aServiceMethodArguments )
    {
        serviceMethodArguments = aServiceMethodArguments;
    }
}
