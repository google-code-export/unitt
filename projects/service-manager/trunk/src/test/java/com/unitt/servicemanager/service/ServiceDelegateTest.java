package com.unitt.servicemanager.service;

import com.unitt.servicemanager.websocket.MessageResponse;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;
import com.unitt.servicemanager.websocket.MessageRoutingInfo.MessageResultType;
import com.unitt.servicemanager.websocket.MessageSerializer;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

public class ServiceDelegateTest
{
    protected MockServiceDelegate serviceDelegate;
    protected MockService service;

    @Before
    public void setUp() throws Exception
    {
        String[] strings = new String[]{"Test", "NoTest"};
        service = new MockService();
        serviceDelegate = new MockServiceDelegate( service );
        serviceDelegate.initialize();
    }

    @After
    public void tearDown() throws Exception
    {
        serviceDelegate.destroy();
        serviceDelegate = null;
        service = null;
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testExecuteServiceMethod()
    {
        //create routing info
        MessageRoutingInfo routing = new MessageRoutingInfo();
        routing.setSerializerType(MessageSerializer.SERIALIZER_TYPE_JSON);
        routing.setMethodSignature( "getNumberOfValues#int,java.lang.String" );
        routing.setServiceName( "myServiceName" );
        
        //execute service
        serviceDelegate.process( routing );
        MessageResponse response = null;
        if (serviceDelegate.getPushesResponse() instanceof MockResponseHandler) {
            response = ((MockResponseHandler) serviceDelegate.getPushesResponse()).pull(0);
        }

        //verify results
        Assert.assertNotNull( "Missing response", response );
        Assert.assertEquals( "Did not set the correct result type.", MessageResultType.CompleteSuccess, response.getHeader().getResultType() );
        Assert.assertTrue("Missing response result.", response.getBody() != null || response.getBodyBytes() != null);
    }

    @Test
    public void testFindMethod()
    {
        String methodSignature = "getNumberOfValues#int,java.lang.String";
        Method method = serviceDelegate.findMethod( methodSignature );
        Assert.assertNotNull( "Did not find method.", method );
        Assert.assertEquals( "Did not find right method.", "getNumberOfValues", method.getName() );
    }

}
