package com.unitt.servicemanager.websocket.server.jetty;


import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.MessagingWebSocketManager;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;


public class MessagingWebSocketServlet extends WebSocketServlet
{
    private static final long     serialVersionUID = -3908380017337516205L;
    private static Logger        logger = LoggerFactory.getLogger( MessagingWebSocketServlet.class );
    
    private MessagingWebSocketManager factory;
    
    
    // getters & setters
    // ---------------------------------------------------------------------------
    public MessagingWebSocketManager getFactory()
    {
        return factory;
    }

    public void setFactory( MessagingWebSocketManager aFactory )
    {
        factory = aFactory;
    }
    

    // servlet logic
    // ---------------------------------------------------------------------------
    @Override
    public WebSocket doWebSocketConnect( HttpServletRequest aRequest, String aProtocol )
    {
        return new ServerSocketAdapter(aRequest, aProtocol, getFactory());
    }
    

    @Override
    public void init() throws ServletException
    {
        String missing = null;
        
        // validate we have all properties
        if ( getFactory() == null )
        {
            missing = ValidationUtil.appendMessage( missing, "Missing factory. " );
        }

        // fail out with appropriate message if missing anything
        if ( missing != null )
        {
            logger.error( missing );
            throw new IllegalStateException( missing );
        }

        // init response queue mgr
        if ( !getFactory().isInitialized() )
        {
            getFactory().initialize();
        }
        
        super.init();
    }
}
