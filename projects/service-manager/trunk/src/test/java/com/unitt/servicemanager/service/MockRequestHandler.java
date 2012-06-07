package com.unitt.servicemanager.service;

import com.unitt.servicemanager.routing.Pulls;
import com.unitt.servicemanager.routing.Pushes;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;

import java.util.LinkedList;

public class MockRequestHandler implements Pushes<MessageRoutingInfo>, Pulls<MessageRoutingInfo> {
    private LinkedList<MessageRoutingInfo> infos = new LinkedList<MessageRoutingInfo>();

    public MessageRoutingInfo pull(long aQueueTimeoutInMillis) {
        return infos.removeFirst();
    }

    public void push(MessageRoutingInfo aHeader, long aQueueTimeoutInMillis) {
        infos.addLast(aHeader);
    }
}
