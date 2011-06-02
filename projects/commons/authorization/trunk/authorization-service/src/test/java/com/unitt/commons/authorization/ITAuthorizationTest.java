package com.unitt.commons.authorization;


import java.io.File;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.rioproject.cybernode.StaticCybernode;


/**
 * Testing the Authorization service
 * 
 * @author: Josh Morris
 */
public class ITAuthorizationTest extends ITAbstractAuthorizationTest
{
    protected static String        opstring = "src/test/opstring/authorization.groovy";
    protected static Authorization service;

    public ITAuthorizationTest()
    {
    }

    @BeforeClass
    public static void setup() throws Exception
    {
        System.out.println("Starting setup...");
        StaticCybernode cybernode = new StaticCybernode();
        System.out.println("Starting cybernode...");
        Map<String, Object> map = cybernode.activate( new File( opstring ) );
        System.out.println("Started cybernode...");
        for ( Map.Entry<String, Object> entry : map.entrySet() )
        {
            String name = entry.getKey();
            Object impl = entry.getValue();
            if ( name.equals( "Authorization" ) )
                service = (Authorization) impl;
        }
        System.out.println("Setup complete");
    }

    @Test
    public void test1()
    {
        testService( service );
    }
}
