package com.unitt.servicemanager.response;


import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unitt.servicemanager.websocket.MessageResponse;


public class ResponseWriterJob implements Serializable, Runnable
{
    private static Logger            logger           = LoggerFactory.getLogger( ResponseWriterJob.class );
    private static final long        serialVersionUID = -5305820677043247554L;

    private MessageResponse          response;
    private transient ResponseWriter responseWriter;


    // constructors
    // ---------------------------------------------------------------------------
    public ResponseWriterJob()
    {
        // default
    }

    public ResponseWriterJob( MessageResponse aResponse )
    {
        response = aResponse;
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public MessageResponse getResponse()
    {
        return response;
    }

    public void setResponse( MessageResponse aResponse )
    {
        response = aResponse;
    }

    public ResponseWriter getResponseWriter()
    {
        return responseWriter;
    }

    public void setResponseWriter( ResponseWriter aResponseWriter )
    {
        responseWriter = aResponseWriter;
    }


    // worker logic
    // ---------------------------------------------------------------------------
    public void run()
    {
        if ( getResponseWriter() != null )
        {
            getResponseWriter().write( getResponse() );
        }
        else
        {
            logger.error( "Job is missing service executor to delegate call to: " + this );
        }
    }
}
