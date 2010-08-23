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
package com.unitt.commons.authorization.hazelcast.call;


import java.io.Serializable;
import java.util.concurrent.Callable;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;
import com.unitt.commons.authorization.hazelcast.HazelcastPermissionManager;
import com.unitt.commons.authorization.hazelcast.PermissionKey;
import com.unitt.commons.authorization.util.PermissionHelper;


public class ChangePermission implements Callable<Long>, Serializable
{
    private static final long serialVersionUID = -7173433229971013721L;

    public enum OPERATION
    {
        ADD, REMOVE, SET
    }

    protected PermissionKey key;
    protected long          requestedPermission;
    protected OPERATION     operation;


    public ChangePermission( PermissionKey aKey, long aRequestedPermission, OPERATION aOperation )
    {
        key = aKey;
        requestedPermission = aRequestedPermission;
        operation = aOperation;
    }


    public Long call() throws Exception
    {
        IMap<PermissionKey, Long> permissions = Hazelcast.getDefaultInstance().getMap( HazelcastPermissionManager.MAP_KEY_PREFIX_NAME );
        Long permission = permissions.get( key );

        // get new permission
        switch ( operation )
        {
            case ADD:
                permission = addPermission( permission );
                break;
            case REMOVE:
                permission = removePermission( permission );
                break;
            case SET:
                permission = setPermission( permission );
                break;
        }

        // save new permission
        if ( permission != null )
        {
            permissions.put( key, permission );
        }
        else
        {
            permissions.remove( key );
        }
        
        return permission;
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
}
