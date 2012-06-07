package com.unitt.servicemanager.routing;

import com.unitt.servicemanager.websocket.MessageRoutingInfo;
import com.unitt.servicemanager.websocket.SerializedMessageBody;

public interface PullsBody {
    SerializedMessageBody pull(MessageRoutingInfo aHeader, long aQueueTimeoutInMillis);
}
