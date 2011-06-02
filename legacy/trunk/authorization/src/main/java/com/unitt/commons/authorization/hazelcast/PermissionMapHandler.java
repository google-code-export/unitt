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
import java.util.Map;

import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStore;
import com.unitt.commons.authorization.PermissionKey;

public class PermissionMapHandler implements MapLoader<PermissionKey, Long>, MapStore<PermissionKey, Long>
{
    protected PermissionDao dao;


    // getters & setters
    // ---------------------------------------------------------------------------
    public PermissionDao getDao()
    {
        return dao;
    }

    public void setDao( PermissionDao aDao )
    {
        dao = aDao;
    }


    // loader implementation
    // ---------------------------------------------------------------------------
    public Long load( PermissionKey aPermissionKey )
    {
        return getDao().load( aPermissionKey );
    }

    public Map<PermissionKey, Long> loadAll( Collection<PermissionKey> aPermissionKeys )
    {
        return getDao().loadAll( aPermissionKeys );
    }

    public void delete( PermissionKey aPermissionKey )
    {
        getDao().delete( aPermissionKey );
    }

    public void deleteAll( Collection<PermissionKey> aPermissionKeys )
    {
        getDao().deleteAll( aPermissionKeys );
    }

    public void store( PermissionKey aPermissionKey, Long aPermission )
    {
        getDao().store( aPermissionKey, aPermission );
    }

    public void storeAll( Map<PermissionKey, Long> aPermissions )
    {
        getDao().storeAll( aPermissions );
    }
}
