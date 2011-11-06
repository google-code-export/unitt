package com.unitt.servicemanager.service;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.unitt.servicemanager.response.ResponseWriterJob;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;
import com.unitt.servicemanager.websocket.SerializedMessageBody;


public class MockServiceDelegate extends ServiceDelegate
{
    public static final int                                SERVICE_ARG_COUNT = 101;
    public static final String                             SERVICE_ARG_VALUE = "TestValue";

    protected BlockingQueue<ResponseWriterJob>             destQueue;
    protected ConcurrentMap<String, SerializedMessageBody> bodyMap;

    public MockServiceDelegate( Object aService )
    {
        super( aService, 10000 );

        destQueue = new ArrayBlockingQueue<ResponseWriterJob>( 10 );
        bodyMap = new ConcurrentHashMap<String, SerializedMessageBody>();
    }

    @Override
    public Object[] getArguments( MessageRoutingInfo aInfo )
    {
        return new Object[] { SERVICE_ARG_COUNT, SERVICE_ARG_VALUE };
    }

    @Override
    public ConcurrentMap<String, SerializedMessageBody> getBodyMap( MessageRoutingInfo aInfo )
    {
        return bodyMap;
    }

    @Override
    public BlockingQueue<ResponseWriterJob> getDestinationQueue( MessageRoutingInfo aInfo )
    {
        return destQueue;
    }

}
