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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.unitt.commons.authorization.Assignable;
import com.unitt.commons.authorization.Permissable;
import com.unitt.commons.authorization.ReservedPermission;

public class MockPermissionDao implements PermissionDao, ReservedPermission
{
    public static final PermissionKey EXISTING_PERMISSION = new PermissionKey(1L, 1L, 1L, 1L);
    public static final Permissable EXISTING_PERMISSABLE = new Permissable(1L, 1L);
    public static final Assignable EXISTING_ASSIGNABLE = new Assignable(1L, 1L);
    
    protected Map<PermissionKey, Long> permissions;
    
    public MockPermissionDao()
    {
        initData();
    }
    
    protected void initData()
    {
        permissions = new HashMap<PermissionKey, Long>();
        permissions.put( EXISTING_PERMISSION, new Long(PERMISSION_CHPERMS) );
    }
    
    public void reset()
    {
        initData();
    }
    
    public void delete( PermissionKey aPermissionKey )
    {
        permissions.remove(aPermissionKey);
    }

    public void deleteAll( Collection<PermissionKey> aPermissionKeys )
    {
        for (PermissionKey key : aPermissionKeys)
        {
            delete(key);
        }
    }

    public Long load( PermissionKey aPermissionKey )
    {
        return permissions.get(aPermissionKey);
    }

    public Map<PermissionKey, Long> loadAll( Collection<PermissionKey> aPermissionKeys )
    {
        return new HashMap<PermissionKey, Long>(permissions);
    }

    public void store( PermissionKey aPermissionKey, Long aPermission )
    {
        permissions.put(aPermissionKey, aPermission);
    }

    public void storeAll( Map<PermissionKey, Long> aPermissions )
    {
        permissions.putAll( aPermissions );
    }

    public boolean isEmpty()
    {
        return permissions.isEmpty();
    }
}
