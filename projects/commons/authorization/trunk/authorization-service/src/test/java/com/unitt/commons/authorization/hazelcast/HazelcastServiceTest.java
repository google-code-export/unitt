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


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.unitt.commons.authorization.Assignable;
import com.unitt.commons.authorization.AssignedPermission;
import com.unitt.commons.authorization.ITAuthorizationTest;
import com.unitt.commons.authorization.InsufficentPrivilegesException;
import com.unitt.commons.authorization.Permissable;
import com.unitt.commons.authorization.ReservedPermission;


public class HazelcastServiceTest extends ITAuthorizationTest implements ReservedPermission
{
    @Test
    public void testHasPermission() throws RemoteException
    {
        Assert.assertTrue( "Did not find existing permission", service.hasPermission( PERMISSION_CHPERMS, MockPermissionDao.EXISTING_PERMISSABLE, Arrays.asList( new Assignable[] { MockPermissionDao.EXISTING_ASSIGNABLE } ) ) );
        Assert.assertTrue( "Found non-existent permission", !service.hasPermission( PERMISSION_READ, MockPermissionDao.EXISTING_PERMISSABLE, Arrays.asList( new Assignable[] { MockPermissionDao.EXISTING_ASSIGNABLE } ) ) );
    }

    @Test
    public void testChangePermission() throws RemoteException
    {
        Permissable permissable = new Permissable(1L, 1L);
        Assignable assignable = new Assignable(1L, 1L);
        List<Assignable> assignables = new ArrayList<Assignable>();
        assignables.add(assignable);
        
        //add permission
        try
        {
            service.applyPermission( assignables, PERMISSION_READ, true, permissable, assignables );
        }
        catch ( InsufficentPrivilegesException e )
        {
            Assert.fail("Did not have sufficient privileges.");
        }
        Assert.assertTrue( "Did not find new permission", service.hasPermission( PERMISSION_READ, permissable, assignables));
        
        //remove permission
        try
        {
            service.applyPermission( assignables, PERMISSION_READ, false, permissable, assignables );
        }
        catch ( InsufficentPrivilegesException e )
        {
            Assert.fail("Did not have sufficient privileges.");
        }
        Assert.assertTrue( "Did not remove new permission", !service.hasPermission( PERMISSION_READ, permissable, assignables));
    }
    
    @Test
    public void testGetPermissions() throws RemoteException
    {
        //verify non-specific perms
        List<AssignedPermission> permissions = service.getPermissions( MockPermissionDao.EXISTING_PERMISSABLE );
        Assert.assertEquals("Did not find the correct number of permissions.", 1, permissions.size());
        
        //verify specific perms
        permissions = service.getPermissions( PERMISSION_CHPERMS, MockPermissionDao.EXISTING_PERMISSABLE );
        Assert.assertEquals("Did not find the correct number of permissions.", 1, permissions.size());
        
        //verify missing specific perms
        permissions = service.getPermissions( PERMISSION_DELETE, MockPermissionDao.EXISTING_PERMISSABLE );
        Assert.assertEquals("Did not find the correct number of permissions.", 0, permissions.size());
    }
    
    @Test
    public void testRemovePermissions() throws RemoteException
    {
        //remove permissions
        try
        {
	        service.removeAllPermissions( Arrays.asList( new Assignable[] { MockPermissionDao.EXISTING_ASSIGNABLE } ), MockPermissionDao.EXISTING_ASSIGNABLE );
        }
        catch ( InsufficentPrivilegesException e )
        {
            Assert.fail("Did not have sufficient privileges.");
        }
        Assert.assertTrue("Did not remove all permissions.", service.getPermissions( MockPermissionDao.EXISTING_PERMISSABLE ).isEmpty());
    }
}
