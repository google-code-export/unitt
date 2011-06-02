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
package com.unitt.commons.authentication;


import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.unitt.commons.security.SecurityContext;


@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
public class AuthenticationManagerTest
{
    @Autowired
    protected ApplicationContext    applicationContext;

    protected AuthenticationManager manager;
    protected MockProvider          provider;

    @Before
    public void setUp() throws Exception
    {
        manager = (AuthenticationManager) applicationContext.getBean( "AuthenticationManager" );
        provider = (MockProvider) applicationContext.getBean( "Provider" );
    }

    @After
    public void tearDown() throws Exception
    {
        manager = null;
        provider = null;
    }

    @Test
    public void testSuccess() throws Exception
    {
        MockToken token = new MockToken(true, true);
        SecurityContext context = manager.authenticate( token );
        Assert.assertNotNull( "Did not authenticate.", context );
    }

    @Test
    public void testBadCredentials()
    {
        boolean failed = false;
        try
        {
            MockToken token = new MockToken(true, false);
            manager.authenticate( token );
        }
        catch (BadCredentialsException e)
        {
            failed = true;
        }
        Assert.assertTrue( "Did not fail with the correct exception", failed );
    }
    
    @Test
    public void testMissingProvider() throws Exception
    {
        boolean failed = false;
        try
        {
            MockToken token = new MockToken(false, true);
            manager.authenticate( token );
        }
        catch (IllegalArgumentException e)
        {
            failed = true;
        }
        Assert.assertTrue( "Did not fail with the correct exception", failed );
    }
}
