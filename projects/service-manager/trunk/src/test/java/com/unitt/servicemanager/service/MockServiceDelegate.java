package com.unitt.servicemanager.service;


import com.unitt.servicemanager.serializer.JacksonJsonSerializer;
import com.unitt.servicemanager.websocket.MessageResponse;
import com.unitt.servicemanager.websocket.MessageSerializer;
import com.unitt.servicemanager.websocket.MessageSerializerRegistry;

import java.lang.reflect.Method;
import java.util.Arrays;


public class MockServiceDelegate extends ServiceDelegate
{
    public static final int                                SERVICE_ARG_COUNT = 101;
    public static final String                             SERVICE_ARG_VALUE = "TestValue";

    public MockServiceDelegate( Object aService )
    {
        super( aService, 10000, null, 1 );

        setPullsBody(new MockBodyHandler());
        setPushesResponse(new MockResponseHandler());
        setPullsRequests(new MockRequestHandler());
        setSerializerRegistry(new MessageSerializerRegistry(Arrays.asList(new MessageSerializer[] {new JacksonJsonSerializer()})));
    }

    @Override
    public Object[] getArguments( MessageResponse aResponse, Method aMethod )
    {
        return new Object[] { SERVICE_ARG_COUNT, SERVICE_ARG_VALUE };
    }
}
