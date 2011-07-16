package com.unitt.servicemanager.service;


import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unitt.servicemanager.websocket.MessageRoutingInfo;


public class ServiceDelegateJob implements Runnable, Serializable
{
    private static final long         serialVersionUID = -5305820677043247554L;
    private static Logger             logger           = LoggerFactory.getLogger( ServiceDelegateJob.class );

    private MessageRoutingInfo        info;
    private transient ServiceDelegate serviceDelegate;


    // constructors
    // ---------------------------------------------------------------------------
    public ServiceDelegateJob()
    {
        // default
    }

    public ServiceDelegateJob( MessageRoutingInfo aInfo )
    {
        info = aInfo;
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public MessageRoutingInfo getInfo()
    {
        return info;
    }

    public void setInfo( MessageRoutingInfo aInfo )
    {
        info = aInfo;
    }

    public ServiceDelegate getServiceDelegate()
    {
        return serviceDelegate;
    }

    public void setServiceDelegate( ServiceDelegate aServiceDelegate )
    {
        serviceDelegate = aServiceDelegate;
    }


    // worker logic
    // ---------------------------------------------------------------------------
    public void run()
    {
        if ( getServiceDelegate() != null )
        {
            getServiceDelegate().executeServiceMethod( getInfo() );
        }
        else
        {
            logger.error( "Job is missing service delegate to delegate call to: " + this );
        }
    }
}
