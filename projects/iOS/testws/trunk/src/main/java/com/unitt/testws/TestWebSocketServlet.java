package com.unitt.testws;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;


public class TestWebSocketServlet extends WebSocketServlet
{
    private static final long     serialVersionUID = -3908380017337516205L;


    // servlet logic
    // ---------------------------------------------------------------------------
    @Override
    public WebSocket doWebSocketConnect( HttpServletRequest aRequest, String aProtocol )
    {
        return new TestWebSocket();
    }

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        getServletContext().getNamedDispatcher( "default" ).forward( request, response );
    }
}
