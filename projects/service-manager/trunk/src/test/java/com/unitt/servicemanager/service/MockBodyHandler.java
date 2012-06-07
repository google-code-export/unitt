package com.unitt.servicemanager.service;

import com.unitt.servicemanager.routing.PullsBody;
import com.unitt.servicemanager.routing.PutsBody;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;
import com.unitt.servicemanager.websocket.SerializedMessageBody;

import java.util.HashMap;
import java.util.Map;

public class MockBodyHandler implements PutsBody, PullsBody {
    private Map<String, SerializedMessageBody> bodies = new HashMap<String, SerializedMessageBody>();

    public SerializedMessageBody pull(MessageRoutingInfo aHeader, long aQueueTimeoutInMillis) {
        return bodies.get(aHeader.getUid());
    }

    public void put(MessageRoutingInfo aHeader, SerializedMessageBody aBody, long aQueueTimeoutInMillis) {
        bodies.put(aHeader.getUid(), aBody);
    }
}
