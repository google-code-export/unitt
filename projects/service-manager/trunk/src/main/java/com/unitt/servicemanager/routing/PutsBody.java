package com.unitt.servicemanager.routing;

import com.unitt.servicemanager.websocket.MessageRoutingInfo;
import com.unitt.servicemanager.websocket.SerializedMessageBody;

public interface PutsBody {
    void put(MessageRoutingInfo aHeader, SerializedMessageBody aBody, long aQueueTimeoutInMillis);
}
