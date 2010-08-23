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
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hazelcast.core.DistributedTask;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.unitt.commons.authorization.Assignable;
import com.unitt.commons.authorization.AssignedPermission;
import com.unitt.commons.authorization.Permissable;
import com.unitt.commons.authorization.PermissionManager;
import com.unitt.commons.authorization.hazelcast.call.ChangePermission;
import com.unitt.commons.authorization.hazelcast.call.HasPermission;
import com.unitt.commons.authorization.hazelcast.call.HasPermissionCallback;
import com.unitt.commons.authorization.hazelcast.call.PermissionChangeCallback;
import com.unitt.commons.authorization.hazelcast.call.PermissionRemoveCallback;
import com.unitt.commons.authorization.hazelcast.call.RemovePermission;
import com.unitt.commons.authorization.hazelcast.call.ChangePermission.OPERATION;
import com.unitt.commons.authorization.util.PermissionHelper;


public class HazelcastPermissionManager implements PermissionManager
{
    private static Log          log                 = LogFactory.getLog( HazelcastPermissionManager.class );

    public static final String  MAP_KEY_PREFIX_NAME = "System:Permission";

    protected HazelcastInstance client;


    // getters & setters
    // ---------------------------------------------------------------------------
    public HazelcastInstance getClient()
    {
        if ( client == null )
        {
            throw new IllegalStateException( "Missing hazelcast client" );
        }
        return client;
    }

    public void setClient( HazelcastInstance aClient )
    {
        client = aClient;
    }


    // manager implementation
    // ---------------------------------------------------------------------------
    @SuppressWarnings( "unchecked" )
    public void applyPermission( long aPermission, boolean aAdd, Permissable aPermissable, List<Assignable> aAssignables )
    {
        OPERATION addRemove = aAdd ? OPERATION.ADD : OPERATION.REMOVE;
        PermissionChangeCallback callback = new PermissionChangeCallback();
        callback.setTotal( aAssignables.size() );
        for ( Assignable assignable : aAssignables )
        {
            PermissionKey key = new PermissionKey( assignable, aPermissable );
            ChangePermission operation = new ChangePermission( key, aPermission, addRemove );
            DistributedTask task = new DistributedTask( operation, key );
            task.setExecutionCallback( callback );
            getClient().getExecutorService().submit( task );
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
            log.error( "Was not able to wait for the permission to be changed. Assuming it worked.", e );
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
        IMap<PermissionKey, Long> map = getClient().getMap( MAP_KEY_PREFIX_NAME );
        Set<Entry<PermissionKey,Long>> entries =  map.entrySet( new PermissablePredicate( aPermissable ) );
        for (Entry<PermissionKey, Long> entry : entries)
        {
            permissions.add(new AssignedPermission(entry.getKey().getAssignableId(), entry.getKey().getAssignableTypeId(), entry.getValue()));
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
            HasPermission operation = new HasPermission( key, aPermission );
            DistributedTask<Boolean> task = new DistributedTask<Boolean>( operation, key );
            task.setExecutionCallback( callback );
            getClient().getExecutorService().submit( task );
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
            log.error( "Was not able to check for the permission. Assuming it worked.", e );
        }
        
        //if we have a result, return it
        if (callback.hasResult())
        {
            return callback.getResult();
        }

        return false;
    }

    @SuppressWarnings( "unchecked" )
    public void removeAllPermissions( Assignable aAssignable )
    {
        IMap<PermissionKey, Long> map = getClient().getMap( MAP_KEY_PREFIX_NAME );
        Set<PermissionKey> keys =  map.keySet( new AssignablePredicate( aAssignable ) );
        PermissionRemoveCallback callback = new PermissionRemoveCallback();
        callback.setTotal( keys.size() );
        for (PermissionKey key : keys)
        {
            RemovePermission operation = new RemovePermission( key );
            DistributedTask task = new DistributedTask( operation, key );
            task.setExecutionCallback( callback );
            getClient().getExecutorService().submit( task );
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
            log.error( "Was not able to wait for all of the permission to be removed for " + aAssignable + ". Assuming it worked.", e );
        }
    }

    @SuppressWarnings( "unchecked" )
    public void setPermission( long aPermission, boolean aAdd, Permissable aPermissable, List<Assignable> aAssignables )
    {
        PermissionChangeCallback callback = new PermissionChangeCallback();
        callback.setTotal( aAssignables.size() );
        for ( Assignable assignable : aAssignables )
        {
            PermissionKey key = new PermissionKey( assignable, aPermissable );
            ChangePermission operation = new ChangePermission( key, aPermission, OPERATION.SET );
            DistributedTask task = new DistributedTask( operation, key );
            task.setExecutionCallback( callback );
            getClient().getExecutorService().submit( task );
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
            log.error( "Was not able to wait for the permission to be set. Assuming it worked.", e );
        }
    }
}
