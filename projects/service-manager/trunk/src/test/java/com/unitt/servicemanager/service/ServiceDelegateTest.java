package com.unitt.servicemanager.service;

import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.unitt.servicemanager.websocket.MessageResponse;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;
import com.unitt.servicemanager.websocket.MessageRoutingInfo.MessageResultType;

public class ServiceDelegateTest
{
    protected MockServiceDelegate serviceDelegate;
    protected MockService service;

    @Before
    public void setUp() throws Exception
    {
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
        routing.setMethodSignature( "getNumberOfValues#int,java.lang.String" );
        routing.setServiceName( "myServiceName" );
        
        //execute service
        serviceDelegate.executeServiceMethod( routing );
        MessageResponse response = null;
        try
        {
            response = serviceDelegate.getDestinationQueue( routing ).take();
        }
        catch ( InterruptedException e )
        {
            fail( "Take was interrupted." );
        }
        
        //verify results
        Assert.assertNotNull( "Missing response", response );
        Assert.assertEquals( "Did not set the correct result type.", MessageResultType.CompleteSuccess, response.getHeader().getResultType() );
        Assert.assertNotNull("Missing response result.", response.getBody());
        Assert.assertTrue( "Did not create the correct result.", response.getBody() instanceof List );
        List<String> results = (List<String>) response.getBody();
        Assert.assertEquals( "Did not create the correct number of elements in the results list.", MockServiceDelegate.SERVICE_ARG_COUNT, results.size() );
        for (String item : results)
        {
            Assert.assertEquals( "Did not create the correct element in the results list.", MockServiceDelegate.SERVICE_ARG_VALUE, item );
        }
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
