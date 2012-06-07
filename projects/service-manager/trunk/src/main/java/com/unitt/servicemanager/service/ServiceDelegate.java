package com.unitt.servicemanager.service;


import com.unitt.servicemanager.routing.Pulls;
import com.unitt.servicemanager.routing.PullsBody;
import com.unitt.servicemanager.routing.Pushes;
import com.unitt.servicemanager.util.ValidationUtil;
import com.unitt.servicemanager.websocket.*;
import com.unitt.servicemanager.websocket.MessageRoutingInfo.MessageResultType;
import com.unitt.servicemanager.worker.DelegateMaster;
import com.unitt.servicemanager.worker.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ServiceDelegate implements Processor<MessageRoutingInfo> {
    private static Logger logger = LoggerFactory.getLogger(ServiceDelegate.class);

    private long queueTimeoutInMillis;
    private boolean isInitialized = false;
    private Map<String, Method> cachedMethods;
    private Object service;
    private MessageSerializerRegistry serializers;
    private int numberOfWorkers;
    private DelegateMaster<MessageRoutingInfo> workers;
    private Pulls<MessageRoutingInfo> pullsRequests;
    private PullsBody pullsBody;
    private Pushes<MessageResponse> pushesResponse;


    // constructors
    // ---------------------------------------------------------------------------
    public ServiceDelegate() {
        this(null, 0, null, 0);
    }

    public ServiceDelegate(Object aService, long aQueueTimeoutInMillis, MessageSerializerRegistry aReqistry, int aNumberOfThreads) {
        setNumberOfWorkers(aNumberOfThreads);
        setService(aService);
        setQueueTimeoutInMillis(aQueueTimeoutInMillis);
        setSerializerRegistry(aReqistry);
    }


    // lifecycle logic
    // ---------------------------------------------------------------------------
    public boolean isInitialized() {
        return isInitialized;
    }

    public void initialize() {
        if (!isInitialized()) {
            String missing = null;

            // validate we have all properties
            if (getService() == null) {
                missing = ValidationUtil.appendMessage(missing, "Missing service instance. ");
            }
            if (getQueueTimeoutInMillis() < 1) {
                missing = ValidationUtil.appendMessage(missing, "Missing valid queue timeout: " + getQueueTimeoutInMillis() + ". ");
            }
            if (getNumberOfWorkers() < 1) {
                missing = ValidationUtil.appendMessage(missing, "Missing number of Threads: " + getNumberOfWorkers() + ". ");
            }
            if (getSerializerRegistry() == null) {
                missing = ValidationUtil.appendMessage(missing, "Missing serializer registry. ");
            }

            // fail out with appropriate message if missing anything
            if (missing != null) {
                logger.error(missing);
                throw new IllegalStateException(missing);
            }

            // apply values
            setCachedMethods(new HashMap<String, Method>());
            if (workers == null) {
                workers = new DelegateMaster<MessageRoutingInfo>(getClass().getSimpleName(), getPullsRequests(), this, getQueueTimeoutInMillis(), getNumberOfWorkers());
            }

            setInitialized(true);
        }
    }

    public void destroy() {
        stop();
        setNumberOfWorkers(0);
        setQueueTimeoutInMillis(0);
        setSerializers(null);
        setService(null);
        setInitialized(false);
        workers = null;
    }

    public void start() {
        if (workers == null) {
            if (!isInitialized()) {
                initialize();
            }
            if (workers == null) {
                throw new IllegalStateException("Missing workers. Cannot start.");
            }
        }
        workers.startup();
    }

    public void stop() {
        try {
            if (workers != null) {
                workers.shutdown();
            }
        } catch (Exception e) {
            logger.error("An error occurred shutting down the workers.", e);
        }
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

    public Pulls<MessageRoutingInfo> getPullsRequests() {
        return pullsRequests;
    }

    public void setPullsRequests(Pulls<MessageRoutingInfo> aPullsRequests) {
        pullsRequests = aPullsRequests;
    }

    public PullsBody getPullsBody() {
        return pullsBody;
    }

    public void setPullsBody(PullsBody aPullsBody) {
        pullsBody = aPullsBody;
    }

    public Pushes<MessageResponse> getPushesResponse() {
        return pushesResponse;
    }

    public void setPushesResponse(Pushes<MessageResponse> aPushesResponse) {
        pushesResponse = aPushesResponse;
    }

    public Object getService() {
        return service;
    }

    public void setService(Object aService) {
        service = aService;
    }

    public MessageSerializerRegistry getSerializerRegistry() {
        return serializers;
    }

    public void setSerializerRegistry(MessageSerializerRegistry aSerializers) {
        serializers = aSerializers;
    }

    public int getNumberOfWorkers() {
        return numberOfWorkers;
    }

    public void setNumberOfWorkers(int aNumberOfThreads) {
        numberOfWorkers = aNumberOfThreads;
    }

    public MessageSerializerRegistry getSerializers() {
        return serializers;
    }

    public void setSerializers(MessageSerializerRegistry aSerializers) {
        serializers = aSerializers;
    }

    protected Map<String, Method> getCachedMethods() {
        return cachedMethods;
    }

    protected void setCachedMethods(Map<String, Method> aCachedMethods) {
        cachedMethods = aCachedMethods;
    }


    // service method execution logic
    // ---------------------------------------------------------------------------
    public void process(MessageRoutingInfo aInfo) {
        MessageResponse response = new MessageResponse();
        response.setHeader(aInfo);
        try {
            // get cached method
            Method method = getCachedMethod(aInfo.getMethodSignature());

            // create and cache if we didnt have a cached one
            if (method == null) {
                method = findMethod(aInfo.getMethodSignature());
                if (method != null) {
                    cacheMethod(aInfo.getMethodSignature(), method);
                }
            }

            // if we are missing the method, throw an exception
            if (method == null) {
                throw new UnsupportedOperationException("[" + aInfo.getServiceName() + "] - Cannot find method[" + aInfo.getMethodSignature() + "] on service class: " + getService().getClass().getName());
            }

            // execute method & apply result
            Object[] args = getArguments(response, method);
            if (methodPushesResults(method)) {
                method.invoke(getService(), args);
                response = null;
            } else {
                Object result = method.invoke(getService(), args);
                response.getHeader().setResultType(MessageResultType.CompleteSuccess);
                response.setBody(result);
            }
        } catch (Exception e) {
            // apply exception to result
            logger.error("[" + aInfo.getServiceName() + "] - Could not execute service method: " + aInfo + " on service class: " + getService().getClass().getName(), e);
            response.getHeader().setResultType(MessageResultType.Error);
            response.setBody(e);
        }

        // push message response into appropriate response queue
        if (response != null) {
            sendResponse(response);
        }
    }

    protected void sendResponse(MessageResponse aResponse) {
        try {
            //@todo: remove need for body attribute
            MessageSerializer serializer = getSerializerRegistry().getSerializer(aResponse.getHeader().getSerializerType());
            if (serializer == null) {
                throw new IllegalArgumentException("Missing serializer: " + aResponse.getHeader().getSerializerType());
            }
            if (aResponse.getBody() != null) {
                aResponse.setBodyBytes(serializer.serializeBody(aResponse.getBody()));
                aResponse.setBody(null);
            }
            getPushesResponse().push(aResponse, getQueueTimeoutInMillis());
        } catch (Exception e) {
            logger.error("[" + this + "] - Could not route message response: " + aResponse.getHeader(), e);
        }
    }

    protected int getIndexOfPartialResults(Method aMethod) {
        Class<?>[] params = aMethod.getParameterTypes();
        //search for a PushesResults parameter
        for (int i = 0; i < params.length; i++) {
            if (params[i].isAssignableFrom(PushesResults.class)) {
                return i;
            }
        }

        return -1;
    }

    protected boolean methodPushesResults(Method aMethod) {
        return getIndexOfPartialResults(aMethod) >= 0;
    }

    public Method getCachedMethod(String aMethodSignature) {
        return getCachedMethods().get(aMethodSignature);
    }

    public void cacheMethod(String aMethodSignature, Method aMethod) {
        getCachedMethods().put(aMethodSignature, aMethod);
    }

    public Object[] getArguments(MessageResponse aResponse, Method aMethod) {
        //@todo: create callback & push into parameters
        // grab arguments & deserialize
        MessageRoutingInfo info = aResponse.getHeader();
        SerializedMessageBody body = getPullsBody().pull(info, getQueueTimeoutInMillis());
        MessageSerializer serializer = getSerializerRegistry().getSerializer(info.getSerializerType());
        DeserializedMessageBody args = serializer.deserializeBody(info, body.getContents());
        if (args != null && args.getServiceMethodArguments() != null) {
            int partialResultsIndex = getIndexOfPartialResults(aMethod);
            Object[] results = new Object[aMethod.getParameterTypes().length];
            List<Object> argValues = args.getServiceMethodArguments();
            for (int i = 0; i < results.length; i++) {
                if (i == partialResultsIndex) {
                    results[i] = new PushesResultsImpl(this, aResponse);
                } else if (partialResultsIndex >= 0 && i > partialResultsIndex) {
                    results[i] = argValues.get(i + 1);
                } else {
                    results[i] = argValues.get(i);
                }

                //if argument should be a date - do conversion from long
                if (aMethod.getParameterTypes()[i].equals(java.util.Date.class)) {
                    if (results[i] instanceof Number) {
                        results[i] = new Date(((Number) results[i]).longValue());
                    }
                }
            }
            return results;
        }
        return new Object[]{};
    }

    public Method findMethod(String aSignature) {
        // grab method info
        int index = aSignature.indexOf("#");
        String methodName = aSignature.substring(0, index);
        String[] parameterTypeNames = aSignature.substring(index + 1).split(",");
        Class<?>[] parameterTypes = new Class[parameterTypeNames.length];
        for (int i = 0; i < parameterTypeNames.length; i++) {
            try {
                parameterTypes[i] = getClassFromName(parameterTypeNames[i]);
            } catch (ClassNotFoundException e) {
                logger.error("Could not find class[" + parameterTypeNames[i] + "] for a parameter in method: " + aSignature);
            }
        }

        // we have method info - find method
        try {
            return getService().getClass().getMethod(methodName, parameterTypes);
        } catch (Exception e) {
            logger.error("Could not find method:[" + aSignature + "]  on service class: " + getService().getClass().getName());
        }

        return null;
    }

    protected Class<?> getClassFromName(String aClassname) throws ClassNotFoundException {
        if ("boolean".equals(aClassname)) {
            return boolean.class;
        } else if ("byte".equals(aClassname)) {
            return byte.class;
        } else if ("short".equals(aClassname)) {
            return short.class;
        } else if ("int".equals(aClassname)) {
            return int.class;
        } else if ("long".equals(aClassname)) {
            return long.class;
        } else if ("double".equals(aClassname)) {
            return double.class;
        } else if ("float".equals(aClassname)) {
            return float.class;
        }

        return Class.forName(aClassname);
    }
}
