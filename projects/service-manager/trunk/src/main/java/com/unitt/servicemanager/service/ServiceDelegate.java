package com.unitt.servicemanager.service;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unitt.servicemanager.routing.MessageRouterExecutor;
import com.unitt.servicemanager.websocket.MessageBody;
import com.unitt.servicemanager.websocket.MessageResponse;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;
import com.unitt.servicemanager.websocket.MessageSerializer;
import com.unitt.servicemanager.websocket.MessageSerializerRegistry;
import com.unitt.servicemanager.websocket.MessageRoutingInfo.MessageResultType;


public abstract class ServiceDelegate
{
    private static Logger       logger        = LoggerFactory.getLogger( MessageRouterExecutor.class );

    private long                queueTimeoutInMillis;
    private boolean             isInitialized = false;
    private Map<String, Method> cachedMethods;
    private Object              service;
    private MessageSerializerRegistry serializers;


    // constructors
    // ---------------------------------------------------------------------------
    public ServiceDelegate()
    {
        this( null, 0 );
    }

    public ServiceDelegate( Object aService, long aQueueTimeoutInMillis )
    {
        setService( aService );
        setQueueTimeoutInMillis( aQueueTimeoutInMillis );
    }


    // lifecycle logic
    // ---------------------------------------------------------------------------
    public boolean isInitialized()
    {
        return isInitialized;
    }

    public void initialize()
    {
        setCachedMethods( new HashMap<String, Method>() );
        setInitialized( true );
    }

    public void destroy()
    {
        setQueueTimeoutInMillis( 0 );
        setInitialized( false );
    }

    protected void setInitialized( boolean aIsInitialized )
    {
        isInitialized = aIsInitialized;
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public long getQueueTimeoutInMillis()
    {
        return queueTimeoutInMillis;
    }

    public void setQueueTimeoutInMillis( long aQueueTimeoutInMillis )
    {
        queueTimeoutInMillis = aQueueTimeoutInMillis;
    }

    public Object getService()
    {
        return service;
    }

    public void setService( Object aService )
    {
        service = aService;
    }

    public MessageSerializerRegistry getSerializerRegistry()
    {
        return serializers;
    }

    public void setSerializerRegistry( MessageSerializerRegistry aSerializers )
    {
        serializers = aSerializers;
    }

    protected Map<String, Method> getCachedMethods()
    {
        return cachedMethods;
    }

    protected void setCachedMethods( Map<String, Method> aCachedMethods )
    {
        cachedMethods = aCachedMethods;
    }


    // service method execution logic
    // ---------------------------------------------------------------------------
    public void executeServiceMethod( MessageRoutingInfo aInfo )
    {
        MessageResponse response = new MessageResponse();
        response.setHeader( aInfo );
        try
        {
            // get cached method
            Method method = getCachedMethod( aInfo.getMethodSignature() );

            // create and cache if we didnt have a cached one
            if ( method == null )
            {
                method = findMethod( aInfo.getMethodSignature() );
                if ( method != null )
                {
                    cacheMethod( aInfo.getMethodSignature(), method );
                }
            }

            // if we are missing the method, throw an exception
            if ( method == null )
            {
                throw new UnsupportedOperationException( "[" + aInfo.getServiceName() + "] - Cannot find method[" + aInfo.getMethodSignature() + "] on service class: " + getService().getClass().getName() );
            }

            // execute method & apply result
            Object[] args = getArguments( aInfo );
            Object result = method.invoke( getService(), args );
            response.getHeader().setResultType( MessageResultType.CompleteSuccess );
            response.setBody( result );
        }
        catch ( Exception e )
        {
            // apply exception to result
            logger.error( "[" + aInfo.getServiceName() + "] - Could not execute service method: " + aInfo + " on service class: " + getService().getClass().getName() , e );
            response.getHeader().setResultType( MessageResultType.Error );
            response.setBody( e );
        }

        // push message response into appropriate response queue
        try
        {
            getDestinationQueue( aInfo ).offer( response, getQueueTimeoutInMillis(), TimeUnit.MILLISECONDS );
        }
        catch ( Exception e )
        {
            logger.error( "[" + this + "] - Could not route message response: " + aInfo, e );
        }
    }

    public Method getCachedMethod( String aMethodSignature )
    {
        return getCachedMethods().get( aMethodSignature );
    }

    public void cacheMethod( String aMethodSignature, Method aMethod )
    {
        getCachedMethods().put( aMethodSignature, aMethod );
    }

    public Object[] getArguments( MessageRoutingInfo aInfo )
    {
        //grab arguments & deserialize
        MessageBody body = getBodyMap( aInfo ).remove( aInfo.getUid() );
        MessageSerializer serializer = getSerializerRegistry().getSerializer( aInfo.getSerializerType() );
        return serializer.deserializeBody( body.getContents() );
    }

    public Method findMethod( String aSignature )
    {
        //grab method info
        int index = aSignature.indexOf( "#" );
        String methodName = aSignature.substring( 0, index );
        String[] parameterTypeNames = aSignature.substring(index + 1).split( "," );
        Class<?>[] parameterTypes = new Class[parameterTypeNames.length];
        for ( int i = 0; i < parameterTypeNames.length; i++ )
        {
            try
            {
                parameterTypes[i] = getClassFromName( parameterTypeNames[i] );
            }
            catch ( ClassNotFoundException e )
            {
                logger.error( "Could not find class[" + parameterTypeNames[i] + "] for a parameter in method: " + aSignature );
            }
        }
        
        //we have method info - find method
        try
        {
            return getService().getClass().getMethod( methodName, parameterTypes );
        }
        catch ( Exception e )
        {
            logger.error( "Could not find method:[" + aSignature + "]  on service class: " + getService().getClass().getName()  );
        }
        
        return null;
    }
    
    protected Class<?> getClassFromName(String aClassname) throws ClassNotFoundException
    {
        if ("boolean".equals(aClassname))
        {
            return boolean.class;
        }
        else if ("byte".equals(aClassname))
        {
            return byte.class;
        }
        else if ("short".equals(aClassname))
        {
            return short.class;
        }
        else if ("int".equals(aClassname))
        {
            return int.class;
        }
        else if ("long".equals(aClassname))
        {
            return long.class;
        }
        else if ("double".equals(aClassname))
        {
            return double.class;
        }
        else if ("float".equals(aClassname))
        {
            return float.class;
        }
        
        return Class.forName(aClassname);
    }

    public abstract ConcurrentMap<String, MessageBody> getBodyMap( MessageRoutingInfo aInfo );

    public abstract BlockingQueue<MessageResponse> getDestinationQueue( MessageRoutingInfo aInfo );
}
