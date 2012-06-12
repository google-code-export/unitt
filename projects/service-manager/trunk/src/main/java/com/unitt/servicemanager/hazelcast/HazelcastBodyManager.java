package com.unitt.servicemanager.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.unitt.servicemanager.routing.PullsBody;
import com.unitt.servicemanager.routing.PutsBody;
import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;
import com.unitt.servicemanager.websocket.SerializedMessageBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class HazelcastBodyManager implements PullsBody, PutsBody {
    private static Logger logger = LoggerFactory.getLogger(HazelcastBodyManager.class);

    private HazelcastInstance hazelcastClient;
    protected boolean isInitialized;


    // constructors
    // ---------------------------------------------------------------------------
    public HazelcastBodyManager() {
        // default
    }

    public HazelcastBodyManager(HazelcastInstance aHazelcastClient) {
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


    // pull/put body logic
    // ---------------------------------------------------------------------------
    public SerializedMessageBody pull(MessageRoutingInfo aHeader, long aQueueTimeoutInMillis) {
        if (logger.isDebugEnabled()) {
            logger.debug("Pulling body from map(" + getBodyMapName(aHeader) + ") for: " + aHeader);
        }
        Map<String, SerializedMessageBody> map = getBodyMap(aHeader);
        if (map != null) {
            return map.get(aHeader.getUid());
        }
        return null;
    }

    public void put(MessageRoutingInfo aHeader, SerializedMessageBody aBody, long aQueueTimeoutInMillis) {
        if (logger.isDebugEnabled()) {
            logger.debug("Putting body into map(" + getBodyMapName(aHeader) + ") for: " + aHeader);
        }
        Map<String, SerializedMessageBody> map = getBodyMap(aHeader);
        if (map != null) {
            map.put(aHeader.getUid(), aBody);
        }
    }

    protected Map<String, SerializedMessageBody> getBodyMap(MessageRoutingInfo aInfo) {
        return getHazelcastClient().getMap( getBodyMapName(aInfo) );
    }

    protected String getBodyMapName(MessageRoutingInfo aInfo) {
        return "body:" + aInfo.getWebSocketId();
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public HazelcastInstance getHazelcastClient() {
        return hazelcastClient;
    }

    public void setHazelcastClient(HazelcastInstance aClient) {
        hazelcastClient = aClient;
    }


    // Object overrides
    // ---------------------------------------------------------------------------
    @Override
    public String toString() {
        return "HazelcastBodyManager{isInitialized=" + isInitialized + ", matching='body:<websocketId>'}";
    }
}
