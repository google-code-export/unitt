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
package com.unitt.commons.authorization.jpa.call;


import java.io.Serializable;

import com.unitt.commons.authorization.PermissionKey;
import com.unitt.commons.authorization.jpa.Permission;
import com.unitt.commons.authorization.jpa.PermissionDao;
import com.unitt.commons.authorization.jpa.PermissionPk;
import com.unitt.commons.authorization.util.PermissionHelper;


public class ChangePermission implements Runnable, Serializable
{
    private static final long serialVersionUID = -7173433229971013721L;

    public enum OPERATION
    {
        ADD, REMOVE, SET
    }

    protected PermissionKey     key;
    protected long              requestedPermission;
    protected OPERATION         operation;
    protected PermissionDao     dao;
    protected RunnableCallback<ChangePermission, Long> callback;


    public ChangePermission( RunnableCallback<ChangePermission, Long> aCallback, PermissionKey aKey, long aRequestedPermission, OPERATION aOperation, PermissionDao aDao )
    {
        callback = aCallback;
        key = aKey;
        requestedPermission = aRequestedPermission;
        operation = aOperation;
        dao = aDao;
    }


    public Permission updatePermission() throws Exception
    {
        Permission permission = dao.find( new PermissionPk( key ) );
        Long mask = permission.getPermissionMask();

        // get new permission
        switch ( operation )
        {
            case ADD:
                mask = addPermission( mask );
                break;
            case REMOVE:
                mask = removePermission( mask );
                break;
            case SET:
                mask = setPermission( mask );
                break;
        }

        // save new permission
        permission.setPermissionMask( mask );
        dao.safeSave( permission );
        
        return permission;
    }

    public void run()
    {
        try
        {
            Permission permission = updatePermission();
            callback.onSuccess( permission.getPermissionMask() );
        }
        catch ( Exception e )
        {
            callback.onError( this, e );
        }
    }

    protected Long addPermission( Long aPermission )
    {
        return PermissionHelper.addMask( requestedPermission, aPermission );
    }

    protected Long removePermission( Long aPermission )
    {
        long newPermission = PermissionHelper.subtractMask( requestedPermission, aPermission );
        if ( newPermission != 0 )
        {
            return newPermission;
        }
        return null;
    }

    protected Long setPermission( Long aPermission )
    {
        if ( aPermission == 0 )
        {
            return null;
        }

        return aPermission;
    }


    @Override
    public String toString()
    {
        return "ChangePermission [key=" + key + ", operation=" + operation + ", requestedPermission=" + requestedPermission + "]";
    }
}
