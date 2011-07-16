package com.unitt.servicemanager.response;


import com.unitt.servicemanager.websocket.MessageResponse;


public interface ResponseWriter
{
    public boolean write( MessageResponse aResponse );
}
