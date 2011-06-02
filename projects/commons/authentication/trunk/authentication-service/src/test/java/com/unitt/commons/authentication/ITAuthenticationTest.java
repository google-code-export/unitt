package com.unitt.commons.authentication;


import java.io.File;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.rioproject.cybernode.StaticCybernode;


/**
 * Testing the Authentication service
 * 
 * @author: Josh Morris
 */
public class ITAuthenticationTest extends ITAbstractAuthenticationTest
{
    protected static String         opstring = "src/test/opstring/authentication.groovy";
    protected static Authentication service;

    public ITAuthenticationTest()
    {
    }

    @BeforeClass
    public static void setup() throws Exception
    {
        System.out.println( "Starting setup..." );
        StaticCybernode cybernode = new StaticCybernode();
        System.out.println( "Starting cybernode..." );
        Map<String, Object> map = cybernode.activate( new File( opstring ) );
        System.out.println( "Started cybernode..." );
        for ( Map.Entry<String, Object> entry : map.entrySet() )
        {
            String name = entry.getKey();
            Object impl = entry.getValue();
            if ( name.equals( "Authentication" ) )
                service = (Authentication) impl;
        }
        System.out.println( "Setup complete" );
    }

    @Test
    public void test1()
    {
        testService( service );
    }
}
