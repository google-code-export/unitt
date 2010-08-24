/*
 * Copyright 2009 UnitT Software Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitt.commons.session.hazelcast;


import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.unitt.commons.session.SessionManager;


@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
public class HazelcastProviderTest
{
    @Autowired
    protected ApplicationContext applicationContext;
    
    protected SessionManager manager;

    @Before
    public void setUp() throws Exception
    {
        manager = (SessionManager) applicationContext.getBean( "SessionService" );
    }

    @After
    public void tearDown() throws Exception
    {
        manager = null;
    }

    @Test
    public void testSession()
    {
        String sessionId = "TestSession1";
        String key = "key1";
        String value = "value1";
        
        Assert.assertNull( "Value is pre-existing.", manager.getValue( sessionId, key ));
        manager.putValue( sessionId, key, value );
        String testValue = (String) manager.getValue( sessionId, key );
        Assert.assertNotNull("Did not save the value", testValue);
        Assert.assertEquals("Did not save the value correctly", value, testValue);
        manager.removeValue( sessionId, key);
        testValue = (String) manager.getValue( sessionId, key );
        Assert.assertNull( "Did not remove the value", testValue );
    }
}
