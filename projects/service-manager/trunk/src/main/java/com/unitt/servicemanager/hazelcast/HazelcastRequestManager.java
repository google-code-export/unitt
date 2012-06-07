package com.unitt.servicemanager.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.unitt.servicemanager.routing.Pulls;
import com.unitt.servicemanager.routing.Pushes;
import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class HazelcastRequestManager implements Pulls<MessageRoutingInfo>, Pushes<MessageRoutingInfo> {
    private static Logger logger = LoggerFactory.getLogger(HazelcastRequestManager.class);

    private HazelcastInstance hazelcastClient;
    private String queueName;
    protected boolean isInitialized;


    // constructors
    // ---------------------------------------------------------------------------
    public HazelcastRequestManager() {
        // default
    }

    public HazelcastRequestManager(HazelcastInstance aHazelcastClient) {
        setHazelcastClient(aHazelcastClient);
    }


    // lifecycle logic
    // ---------------------------------------------------------------------------
    public void initialize() {
        String missing = null;

        // init
        if (getHazelcastClient() == null) {
            missing = ValidationUtil.appendMessage(missing, "Missing hazelcast client. ");
        }
        if (getQueueName() == null) {
            missing = ValidationUtil.appendMessage(missing, "Missing queue name. ");
        }

        // fail out with appropriate message if missing anything
        if (missing != null) {
            logger.error(missing);
            throw new IllegalStateException(missing);
        }

        setInitialized(true);
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    protected void setInitialized(boolean aInitialized) {
        isInitialized = aInitialized;
    }

    public void destroy() {
        setHazelcastClient(null);
        setInitialized(false);
    }


    // pulls/pushes message response
    // ---------------------------------------------------------------------------
    public MessageRoutingInfo pull(long aQueueTimeoutInMillis) {
        try {
            return getQueue().poll(aQueueTimeoutInMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            //do nothing
        }
        return null;
    }

    public void push(MessageRoutingInfo aRequest, long aQueueTimeoutInMillis) {
        try {
            getQueue().offer(aRequest, aQueueTimeoutInMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            //do nothing
        }
    }

    protected BlockingQueue<MessageRoutingInfo> getQueue() {
        return getHazelcastClient().getQueue(getQueueName());
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public HazelcastInstance getHazelcastClient() {
        return hazelcastClient;
    }

    public void setHazelcastClient(HazelcastInstance aClient) {
        hazelcastClient = aClient;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String aQueueName) {
        queueName = aQueueName;
    }
}
