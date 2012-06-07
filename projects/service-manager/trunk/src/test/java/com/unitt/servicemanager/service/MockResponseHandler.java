package com.unitt.servicemanager.service;

import com.unitt.servicemanager.routing.Pulls;
import com.unitt.servicemanager.routing.Pushes;
import com.unitt.servicemanager.websocket.MessageResponse;

import java.util.LinkedList;

public class MockResponseHandler  implements Pushes<MessageResponse>, Pulls<MessageResponse> {
    private LinkedList<MessageResponse> infos = new LinkedList<MessageResponse>();

    public MessageResponse pull(long aQueueTimeoutInMillis) {
        return infos.removeFirst();
    }

    public void push(MessageResponse aHeader, long aQueueTimeoutInMillis) {
        infos.addLast(aHeader);
    }
}
