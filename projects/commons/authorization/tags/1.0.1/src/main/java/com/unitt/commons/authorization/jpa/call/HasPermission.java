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


public class HasPermission implements Runnable, Serializable
{
    private static final long                          serialVersionUID = 2109183662766060130L;

    protected RunnableCallback<HasPermission, Boolean> callback;
    protected PermissionKey                            key;
    protected long                                     requestedPermission;
    protected PermissionDao                            dao;


    public HasPermission( RunnableCallback<HasPermission, Boolean> aCallback, PermissionKey aKey, long aRequestedPermission, PermissionDao aDao )
    {
        super();
        callback = aCallback;
        key = aKey;
        requestedPermission = aRequestedPermission;
        dao = aDao;
    }


    public void run()
    {
        try
        {
            Boolean result = false;
            Permission permission = dao.find( new PermissionPk( key ) );
            if ( permission != null )
            {
                result = PermissionHelper.allows( requestedPermission, permission.getPermissionMask() );
            }
            callback.onSuccess( result );
        }
        catch ( Exception e )
        {
            callback.onError( this, e );
        }
    }


    @Override
    public String toString()
    {
        return "HasPermission [key=" + key + ", requestedPermission=" + requestedPermission + "]";
    }
}
