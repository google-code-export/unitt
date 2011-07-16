package com.unitt.servicemanager.response;

import com.unitt.servicemanager.websocket.MessageResponse;

public interface UndeliverableMessageHandler
{
    public void handleUndeliverableMessage( MessageResponse aResponse );
}
