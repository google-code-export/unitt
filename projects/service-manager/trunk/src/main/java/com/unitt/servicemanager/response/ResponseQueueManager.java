package com.unitt.servicemanager.response;


import com.unitt.commons.foundation.lifecycle.Destructable;
import com.unitt.commons.foundation.lifecycle.Initializable;
import com.unitt.servicemanager.routing.HasServerId;
import com.unitt.servicemanager.routing.Pulls;
import com.unitt.servicemanager.util.LifecycleHelper;
import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.MessageResponse;
import com.unitt.servicemanager.websocket.MessagingWebSocket;
import com.unitt.servicemanager.worker.DelegateMaster;
import com.unitt.servicemanager.worker.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;


public class ResponseQueueManager implements Initializable, Destructable, Processor<MessageResponse> {
    private static Logger logger = LoggerFactory.getLogger(ResponseQueueManager.class);

    protected boolean isInitialized;
    private long queueTimeoutInMillis;
    private Map<String, MessagingWebSocket> sockets;
    private UndeliverableMessageHandler undeliverableMessageHandler;
    private int numberOfWorkers;
    private String serverId;
    private Pulls<MessageResponse> pullsResponse;
    private DelegateMaster<MessageResponse> workers;


    // constructors
    // ---------------------------------------------------------------------------
    public ResponseQueueManager() {
        this(null, 0, 0, null, null);
    }

    public ResponseQueueManager(String aServerId, long aQueueTimeoutInMillis, int aNumberOfWorkers, Map<String, MessagingWebSocket> aSockets, UndeliverableMessageHandler aUndeliverableMessageHandler) {
        setServerId(aServerId);
        setQueueTimeoutInMillis(aQueueTimeoutInMillis);
        setNumberOfWorkers(aNumberOfWorkers);
        setSockets(aSockets);
        setUndeliverableMessageHandler(aUndeliverableMessageHandler);
    }


    // lifecycle logic
    // ---------------------------------------------------------------------------
    public void initialize() {
        if (!isInitialized()) {
            String missing = null;

            // validate we have all properties
            if (getQueueTimeoutInMillis() < 1) {
                missing = ValidationUtil.appendMessage(missing, "Missing valid queue timeout: " + getQueueTimeoutInMillis() + ". ");
            }
            if (getNumberOfWorkers() < 1) {
                missing = ValidationUtil.appendMessage(missing, "Missing number of Threads: " + getNumberOfWorkers() + ". ");
            }
            if (getServerId() == null) {
                try {
                    serverId = InetAddress.getLocalHost().getHostName() + "::" + System.currentTimeMillis();
                } catch (Exception e) {
                    serverId = "Unknown IP Address::" + System.currentTimeMillis();
                }
            }
            if (getPullsResponse() == null) {
                missing = ValidationUtil.appendMessage(missing, "Missing pulls response handler. ");
            } else if (getPullsResponse() instanceof HasServerId) {
                ((HasServerId) getPullsResponse()).setServerId(getServerId());
            }

            // fail out with appropriate message if missing anything
            if (missing != null) {
                logger.error(missing);
                throw new IllegalStateException(missing);
            }

            // apply values
            LifecycleHelper.initialize(getPullsResponse());
            if (workers == null) {
                workers = new DelegateMaster<MessageResponse>(getClass().getSimpleName(), getPullsResponse(), this, getQueueTimeoutInMillis(), getNumberOfWorkers());
            }
            workers.startup();
            if (sockets == null) {
                sockets = new HashMap<String, MessagingWebSocket>();
            }

            logger.info("Starting " + getNumberOfWorkers() + " workers for server response queue manager: server=" + getServerId() + ", pullsResponse=" + getPullsResponse());

            // mark as complete
            setInitialized(true);
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void destroy() {
        try {
            if (sockets != null) {
                sockets.clear();
            }
            setSockets(null);
        } catch (Exception e) {
            logger.error("An error occurred clearing the socket map: " + this, e);
        }
        setNumberOfWorkers(0);
        setQueueTimeoutInMillis(0);
        try {
            if (workers != null) {
                workers.shutdown();
            }
            workers = null;
        } catch (Exception e) {
            logger.error("An error occurred shutting down the workers.", e);
        }
        LifecycleHelper.destroy(getPullsResponse());
        setPullsResponse(null);
        setUndeliverableMessageHandler(null);
        setInitialized(false);
    }

    protected void setInitialized(boolean aIsInitialized) {
        isInitialized = aIsInitialized;
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public long getQueueTimeoutInMillis() {
        return queueTimeoutInMillis;
    }

    public void setQueueTimeoutInMillis(long aQueueTimeoutInMillis) {
        queueTimeoutInMillis = aQueueTimeoutInMillis;
    }

    public Pulls<MessageResponse> getPullsResponse() {
        return pullsResponse;
    }

    public void setPullsResponse(Pulls<MessageResponse> aPullsResponse) {
        pullsResponse = aPullsResponse;
    }

    public DelegateMaster<MessageResponse> getWorkers() {
        return workers;
    }

    public int getNumberOfWorkers() {
        return numberOfWorkers;
    }

    public void setNumberOfWorkers(int aNumberOfThreads) {
        numberOfWorkers = aNumberOfThreads;
    }

    protected Map<String, MessagingWebSocket> getSockets() {
        return sockets;
    }

    protected void setSockets(Map<String, MessagingWebSocket> aSockets) {
        sockets = aSockets;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String aServerId) {
        serverId = aServerId;
    }

    public UndeliverableMessageHandler getUndeliverableMessageHandler() {
        return undeliverableMessageHandler;
    }

    public void setUndeliverableMessageHandler(UndeliverableMessageHandler aUndeliverableMessageHandler) {
        undeliverableMessageHandler = aUndeliverableMessageHandler;
    }


    // service logic
    // ---------------------------------------------------------------------------
    public void addSocket(MessagingWebSocket aSocket) {
        getSockets().put(aSocket.getSocketId(), aSocket);
    }

    public void removeSocket(MessagingWebSocket aSocket) {
        getSockets().remove(aSocket.getSocketId());
    }

    public MessagingWebSocket getSocket(String aSocketId) {
        return getSockets().get(aSocketId);
    }


    // response writer logic
    // ---------------------------------------------------------------------------
    public void process(MessageResponse aResponse) {
        // send the message down the websocket
        try {
            String socketId = aResponse.getHeader().getWebSocketId();
            MessagingWebSocket socket = getSocket(socketId);
            if (socket != null) {
                socket.send(aResponse);
            }
        } catch (Exception e) {
            logger.error("[" + this + "] - Could not write message: " + aResponse, e);
        }

        // it didn't write, send to dead letter queue, if any
        if (getUndeliverableMessageHandler() != null) {
            getUndeliverableMessageHandler().handleUndeliverableMessage(aResponse);
        }
    }
}
