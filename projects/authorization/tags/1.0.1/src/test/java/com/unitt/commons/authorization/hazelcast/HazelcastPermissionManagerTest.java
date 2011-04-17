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
package com.unitt.commons.authorization.hazelcast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.unitt.commons.authorization.Assignable;
import com.unitt.commons.authorization.AssignedPermission;
import com.unitt.commons.authorization.AuthorizationManager;
import com.unitt.commons.authorization.InsufficentPrivilegesException;
import com.unitt.commons.authorization.Permissable;
import com.unitt.commons.authorization.ReservedPermission;


@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
public class HazelcastPermissionManagerTest implements ReservedPermission
{
    @Autowired
    protected ApplicationContext   applicationContext;

    protected AuthorizationManager manager;
    protected MockPermissionDao    dao;

    @Before
    public void setUp() throws Exception
    {
        manager = (AuthorizationManager) applicationContext.getBean( "AuthorizationManager" );
        dao = (MockPermissionDao) applicationContext.getBean( "PermissionDao" );
    }

    @After
    public void tearDown() throws Exception
    {
        manager = null;
        dao.reset();
        dao = null;
    }

    @Test
    public void testHasPermission()
    {
        Assert.assertTrue( "Did not find existing permission", manager.hasPermission( PERMISSION_CHPERMS, MockPermissionDao.EXISTING_PERMISSABLE, Arrays.asList( new Assignable[] { MockPermissionDao.EXISTING_ASSIGNABLE } ) ) );
        Assert.assertTrue( "Found non-existent permission", !manager.hasPermission( PERMISSION_READ, MockPermissionDao.EXISTING_PERMISSABLE, Arrays.asList( new Assignable[] { MockPermissionDao.EXISTING_ASSIGNABLE } ) ) );
    }

    @Test
    public void testChangePermission()
    {
        Permissable permissable = new Permissable(1L, 1L);
        Assignable assignable = new Assignable(1L, 1L);
        List<Assignable> assignables = new ArrayList<Assignable>();
        assignables.add(assignable);
        
        //add permission
        try
        {
            manager.applyPermission( assignables, PERMISSION_READ, true, permissable, assignables );
        }
        catch ( InsufficentPrivilegesException e )
        {
            Assert.fail("Did not have sufficient privileges.");
        }
        Assert.assertTrue( "Did not find new permission", manager.hasPermission( PERMISSION_READ, permissable, assignables));
        
        //remove permission
        try
        {
            manager.applyPermission( assignables, PERMISSION_READ, false, permissable, assignables );
        }
        catch ( InsufficentPrivilegesException e )
        {
            Assert.fail("Did not have sufficient privileges.");
        }
        Assert.assertTrue( "Did not remove new permission", !manager.hasPermission( PERMISSION_READ, permissable, assignables));
    }
    
    @Test
    public void testGetPermissions()
    {
        //verify non-specific perms
        List<AssignedPermission> permissions = manager.getPermissions( MockPermissionDao.EXISTING_PERMISSABLE );
        Assert.assertEquals("Did not find the correct number of permissions.", 1, permissions.size());
        
        //verify specific perms
        permissions = manager.getPermissions( PERMISSION_CHPERMS, MockPermissionDao.EXISTING_PERMISSABLE );
        Assert.assertEquals("Did not find the correct number of permissions.", 1, permissions.size());
        
        //verify missing specific perms
        permissions = manager.getPermissions( PERMISSION_DELETE, MockPermissionDao.EXISTING_PERMISSABLE );
        Assert.assertEquals("Did not find the correct number of permissions.", 0, permissions.size());
    }
    
    @Test
    public void testRemovePermissions()
    {
        manager.removeAllPermissions( Arrays.asList( new Assignable[] { MockPermissionDao.EXISTING_ASSIGNABLE } ), MockPermissionDao.EXISTING_ASSIGNABLE );
        Assert.assertTrue("Did not remove all permissions.", dao.isEmpty());
    }
}
