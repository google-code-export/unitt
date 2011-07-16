package com.unitt.servicemanager.routing;


import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unitt.servicemanager.websocket.MessageRoutingInfo;


public class MessageRouterJob implements Serializable, Runnable
{
    private static final long       serialVersionUID = -5305820677043247554L;
    private static Logger           logger           = LoggerFactory.getLogger( MessageRouterJob.class );

    private MessageRoutingInfo      info;
    private transient MessageRouter messageRouter;


    // constructors
    // ---------------------------------------------------------------------------
    public MessageRouterJob()
    {
        // default
    }

    public MessageRouterJob( MessageRoutingInfo aInfo )
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

    public MessageRouter getMessageRouter()
    {
        return messageRouter;
    }

    public void setMessageRouter( MessageRouter aMessageRouter )
    {
        messageRouter = aMessageRouter;
    }


    // worker logic
    // ---------------------------------------------------------------------------
    public void run()
    {
        if ( getMessageRouter() != null )
        {
            getMessageRouter().route( getInfo() );
        }
        else
        {
            logger.error( "Job is missing service executor to delegate call to: " + this );
        }
    }
}
