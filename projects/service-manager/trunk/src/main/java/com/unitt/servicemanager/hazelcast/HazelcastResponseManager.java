package com.unitt.servicemanager.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.unitt.servicemanager.routing.HasServerId;
import com.unitt.servicemanager.routing.Pulls;
import com.unitt.servicemanager.routing.Pushes;
import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class HazelcastResponseManager implements Pulls<MessageResponse>, Pushes<MessageResponse>, HasServerId {
    private static Logger logger = LoggerFactory.getLogger(HazelcastResponseManager.class);

    private HazelcastInstance hazelcastClient;
    private String serverId;
    protected boolean isInitialized;


    // constructors
    // ---------------------------------------------------------------------------
    public HazelcastResponseManager() {
        // default
    }

    public HazelcastResponseManager(HazelcastInstance aHazelcastClient) {
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
        if (getServerId() == null) {
            try {
                serverId = InetAddress.getLocalHost().getHostName() + "::" + System.currentTimeMillis();
            } catch (Exception e) {
                serverId = "Unknown IP Address::" + System.currentTimeMillis();
            }
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
    public MessageResponse pull(long aQueueTimeoutInMillis) {
        try {
            MessageResponse result = getQueue().poll(aQueueTimeoutInMillis, TimeUnit.MILLISECONDS);
            if (result != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Pulled response from queue(" + getQueueName() + ")");
                }
            }
            return result;
        } catch (InterruptedException e) {
            //do nothing
        }
        return null;
    }

    public void push(MessageResponse aResponse, long aQueueTimeoutInMillis) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Pushing response into queue(" + getQueueName() + "): " + aResponse.getHeader());
            }
            getQueue().offer(aResponse, aQueueTimeoutInMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            //do nothing
        }
    }

    protected BlockingQueue<MessageResponse> getQueue() {
        return getHazelcastClient().getQueue(getQueueName());
    }

    protected String getQueueName() {
        return "outgoing:" + getServerId();
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public HazelcastInstance getHazelcastClient() {
        return hazelcastClient;
    }

    public void setHazelcastClient(HazelcastInstance aClient) {
        hazelcastClient = aClient;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String aServerId) {
        serverId = aServerId;
    }


    // Object overrides
    // ---------------------------------------------------------------------------

    @Override
    public String toString() {
        return "HazelcastResponseManager{ queueName=" + getQueueName() + ", isInitialized=" + isInitialized + ", serverId='" + serverId + '\'' + '}';
    }
}
