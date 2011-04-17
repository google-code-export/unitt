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
import com.unitt.commons.authorization.PermissionKey;
import com.unitt.commons.authorization.hazelcast.HazelcastPermissionManager;
import com.unitt.commons.authorization.util.PermissionHelper;


public class HasPermission implements Callable<Boolean>, Serializable
{
    private static final long serialVersionUID = 2109183662766060130L;
    
    protected PermissionKey key;
    protected long          requestedPermission;


    public HasPermission( PermissionKey aKey, long aRequestedPermission )
    {
        super();
        key = aKey;
        requestedPermission = aRequestedPermission;
    }


    public Boolean call() throws Exception
    {
        IMap<PermissionKey, Long> permissions = Hazelcast.getDefaultInstance().getMap( HazelcastPermissionManager.MAP_KEY_PREFIX_NAME );
        Long permission = permissions.get( key );
        if ( permission != null )
        {
            return PermissionHelper.allows( requestedPermission, permission );
        }

        return false;
    }
}
