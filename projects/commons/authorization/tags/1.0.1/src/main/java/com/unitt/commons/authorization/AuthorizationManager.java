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
package com.unitt.commons.authorization;

import java.util.List;

import com.unitt.commons.foundation.lifecycle.Initializable;

public class AuthorizationManager implements Initializable, ReservedPermission
{
    protected boolean isInitialized;
    protected PermissionManager manager;


    // getters & setters
    // ---------------------------------------------------------------------------
    public PermissionManager getPermissionManager()
    {
        if (manager == null)
        {
            throw new IllegalStateException("Missing permission manager.");
        }
        
        return manager;
    }

    public void setPermissionManager( PermissionManager aManager )
    {
        if ( isInitialized() )
        {
            throw new IllegalStateException( "Cannot set permission manager after authorization manager has been initialized" );
        }
        
        manager = aManager;
    }

    
    // lifecycle logic
    // ---------------------------------------------------------------------------
    public void initialize()
    {
        System.out.println("Initializing...");
        if ( !isInitialized() )
        {
            if (manager instanceof Initializable)
            {
                Initializable permMgr = (Initializable) manager;
                if (!permMgr.isInitialized())
                {
                    System.out.println("Initializing permission manager");
                    permMgr.initialize();
                }
            }
            else
            {
                System.out.println("Manager is not initializable");
            }
            isInitialized = true;
        }
    }

    public boolean isInitialized()
    {
        return isInitialized;
    }
    
    
    // authorization logic
    // ---------------------------------------------------------------------------
    public void applyPermission( List<Assignable> aAssigning, long aPermission, boolean aAdd, Permissable aPermissable, List<Assignable> aAssignables ) throws InsufficentPrivilegesException
    {
        verifyChangePermission( aPermissable, aAssigning );
        getPermissionManager().applyPermission( aPermission, aAdd, aPermissable, aAssignables );
    }

    public List<AssignedPermission> getPermissions( long aMask, Permissable aPermissable )
    {
        return getPermissionManager().getPermissions( aMask, aPermissable );
    }

    public List<AssignedPermission> getPermissions( Permissable aPermissable )
    {
        return getPermissionManager().getPermissions( aPermissable );
    }

    public boolean hasPermission( long aPermission, Permissable aPermissable, List<Assignable> aAssignables )
    {
        return getPermissionManager().hasPermission( aPermission, aPermissable, aAssignables );
    }

    public void setPermission( List<Assignable> aAssigning, long aPermission, Permissable aPermissable, List<Assignable> aAssignables ) throws InsufficentPrivilegesException
    {
        verifyChangePermission( aPermissable, aAssigning );
        getPermissionManager().setPermission( aPermission, aPermissable, aAssignables );
    }
    
    protected void verifyChangePermission(Permissable aPermissable, List<Assignable> aAssigning) throws InsufficentPrivilegesException
    {
        if (!hasPermission( PERMISSION_CHPERMS, aPermissable, aAssigning ))
        {
            throw new InsufficentPrivilegesException( aPermissable, aAssigning );
        }
    }
    
    public void removeAllPermissions(List<Assignable> aAssigning, Assignable aAssignable)
    {
        getPermissionManager().removeAllPermissions( aAssignable );
    }
}
