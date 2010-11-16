/*
 * Copyright 2010 UnitT Software Inc.
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
package com.unitt.commons.authorization.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unitt.commons.authorization.Assignable;
import com.unitt.commons.authorization.AssignedPermission;
import com.unitt.commons.authorization.Permissable;
import com.unitt.commons.authorization.PermissionKey;
import com.unitt.commons.authorization.PermissionManager;
import com.unitt.commons.authorization.jpa.call.ChangePermission;
import com.unitt.commons.authorization.jpa.call.HasPermission;
import com.unitt.commons.authorization.jpa.call.HasPermissionCallback;
import com.unitt.commons.authorization.jpa.call.PermissionChangeCallback;
import com.unitt.commons.authorization.jpa.call.PermissionRemoveCallback;
import com.unitt.commons.authorization.jpa.call.RemovePermission;
import com.unitt.commons.authorization.jpa.call.ChangePermission.OPERATION;
import com.unitt.commons.authorization.util.PermissionHelper;
import com.unitt.commons.foundation.lifecycle.Destructable;
import com.unitt.commons.foundation.lifecycle.Initializable;

public class JpaPermissionManager implements PermissionManager, Initializable, Destructable
{
    private static final Logger logger = LoggerFactory.getLogger( JpaPermissionManager.class );
    
    protected boolean isInitialized;
    protected ExecutorService executor;
    protected PermissionDao dao;
    
    
    // getters & setters
    // ---------------------------------------------------------------------------
    public ExecutorService getExecutor()
    {
        return executor;
    }

    public void setExecutor( ExecutorService aExecutor )
    {
        executor = aExecutor;
    }    

    public PermissionDao getDao()
    {
        return dao;
    }

    public void setDao( PermissionDao aDao )
    {
        dao = aDao;
    }
    

    // lifecycle logic
    // ---------------------------------------------------------------------------
    public void initialize()
    {
        if (!isInitialized())
        {
            if (executor == null)
            {
                executor = Executors.newCachedThreadPool();
            }
            isInitialized = true;
        }
    }

    public boolean isInitialized()
    {
        return isInitialized;
    }

    public void destroy()
    {
        isInitialized = false;
        executor.shutdown();
        executor = null;
    }


    // manager implementation
    // ---------------------------------------------------------------------------
    public void applyPermission( long aPermission, boolean aAdd, Permissable aPermissable, List<Assignable> aAssignables )
    {
        OPERATION addRemove = aAdd ? OPERATION.ADD : OPERATION.REMOVE;
        PermissionChangeCallback callback = new PermissionChangeCallback();
        callback.setTotal( aAssignables.size() );
        for ( Assignable assignable : aAssignables )
        {
            PermissionKey key = new PermissionKey( assignable, aPermissable );
            ChangePermission operation = new ChangePermission( callback, key, aPermission, addRemove, dao );
            executor.submit( operation );
        }
        try
        {
            synchronized ( callback )
            {
                callback.wait( 60000 );
            }
        }
        catch ( InterruptedException e )
        {
            logger.error( "Was not able to wait for the permission to be changed. Assuming it worked.", e );
        }
    }

    public List<AssignedPermission> getPermissions( long aMask, Permissable aPermissable )
    {
        List<AssignedPermission> possibles = getPermissions(aPermissable);
        List<AssignedPermission> actuals = new ArrayList<AssignedPermission>();
        for (AssignedPermission permission : possibles)
        {
            if (PermissionHelper.allows( aMask, permission.getPermission() ))
            {
                actuals.add(permission);
            }
        }
        return actuals;
    }

    public List<AssignedPermission> getPermissions( Permissable aPermissable )
    {
        List<AssignedPermission> permissions = new ArrayList<AssignedPermission>();
        List<Permission> found = dao.findByPermissable( aPermissable.getId(), aPermissable.getTypeId() );
        for (Permission permission : found)
        {
            permissions.add(new AssignedPermission(permission.getAssignableId(), permission.getAssignableTypeId(), permission.getPermissionMask()));
        }
        return permissions;
    }

    public boolean hasPermission( long aPermission, Permissable aPermissable, List<Assignable> aAssignables )
    {
        //queue up all checks for permissions
        HasPermissionCallback callback = new HasPermissionCallback();
        callback.setTotal( aAssignables.size() );
        for ( Assignable assignable : aAssignables )
        {
            PermissionKey key = new PermissionKey( assignable, aPermissable );
            HasPermission operation = new HasPermission( callback, key, aPermission, dao );
            executor.submit( operation );
        }
        
        //wait for execution
        try
        {
            synchronized ( callback )
            {
                callback.wait( 60000 );
            }
        }
        catch ( InterruptedException e )
        {
            logger.error( "Was not able to check for the permission. Assuming it worked.", e );
        }
        
        //if we have a result, return it
        if (callback.hasResult())
        {
            return callback.getResult();
        }

        return false;
    }

    public void removeAllPermissions( Assignable aAssignable )
    {
        PermissionRemoveCallback callback = new PermissionRemoveCallback();
        RemovePermission operation = new RemovePermission( callback, aAssignable, dao );
        executor.submit( operation );
        
        try
        {
            synchronized (callback)
            {
                callback.wait( 60000 );
            }
        }
        catch ( InterruptedException e )
        {
            logger.error( "Was not able to wait for all of the permission to be removed for " + aAssignable + ". Assuming it worked.", e );
        }
    }

    public void setPermission( long aPermission, boolean aAdd, Permissable aPermissable, List<Assignable> aAssignables )
    {
        PermissionChangeCallback callback = new PermissionChangeCallback();
        callback.setTotal( aAssignables.size() );
        for ( Assignable assignable : aAssignables )
        {
            PermissionKey key = new PermissionKey( assignable, aPermissable );
            ChangePermission operation = new ChangePermission( callback, key, aPermission, OPERATION.SET, dao );
            executor.submit( operation );
        }
        try
        {
            synchronized (callback)
            {
                callback.wait( 60000 );
            }
        }
        catch ( InterruptedException e )
        {
            logger.error( "Was not able to wait for the permission to be set. Assuming it worked.", e );
        }
    }
}
