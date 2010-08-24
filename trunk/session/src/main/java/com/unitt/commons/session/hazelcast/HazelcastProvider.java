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
package com.unitt.commons.session.hazelcast;


import java.io.Serializable;

import com.hazelcast.core.DistributedTask;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.unitt.commons.session.SessionProvider;


public class HazelcastProvider implements SessionProvider
{
    public static final String  KEY_EXPIRATION = "System:Session/Expiration";
    public static final String  KEY_ID         = "System:Session/Id";
    public static final String  MAP_KEY_PREFIX = "System:Session:";

    protected HazelcastInstance client;
    protected boolean touchOnAccess = false;


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

    public boolean isTouchOnAccess()
    {
        return touchOnAccess;
    }

    public void setTouchOnAccess( boolean aTouchOnAccess )
    {
        touchOnAccess = aTouchOnAccess;
    }
    

    // Session provider implementation
    // ---------------------------------------------------------------------------
    public void create( String aSessionId, long aInactiveTimeToLiveInMillis )
    {
        IMap<Object, Object> map = getClient().getMap( getMapKey( aSessionId ) );
        map.put( KEY_ID, aSessionId );
        map.put( KEY_EXPIRATION, new SessionExpiration( aInactiveTimeToLiveInMillis, System.currentTimeMillis() + aInactiveTimeToLiveInMillis ) );
    }

    @SuppressWarnings( "unchecked" )
    public void touch( String aSessionId )
    {
        DistributedTask task = new DistributedTask( new SessionTouch( aSessionId ), getMapKey( aSessionId ) );
        getClient().getExecutorService().submit( task );
    }

    @SuppressWarnings( "unchecked" )
    public void close( String aSessionId )
    {
        IMap map = getClient().getMap( getMapKey( aSessionId ) );
        if ( map != null )
        {
            map.destroy();
        }
    }

    @SuppressWarnings( "unchecked" )
    public Serializable getValue( String aSessionId, String aKey )
    {
        IMap map = getClient().getMap( getMapKey( aSessionId ) );
        if ( map != null )
        {
            if (isTouchOnAccess())
            {
                touch(aSessionId);
            }
            return (Serializable) map.get( aKey );
        }

        return null;
    }

    @SuppressWarnings( "unchecked" )
    public void putValue( String aSessionId, String aKey, Serializable aValue )
    {
        IMap map = getClient().getMap( getMapKey( aSessionId ) );
        if ( map != null )
        {
            if (isTouchOnAccess())
            {
                touch(aSessionId);
            }
            map.put( aKey, aValue );
        }
    }

    @SuppressWarnings( "unchecked" )
    public void removeValue( String aSessionId, String aKey )
    {
        IMap map = getClient().getMap( getMapKey( aSessionId ) );
        if ( map != null )
        {
            if (isTouchOnAccess())
            {
                touch(aSessionId);
            }
            map.remove( aKey );
        }
    }

    protected static String getMapKey( String aSessionId )
    {
        return MAP_KEY_PREFIX + aSessionId;
    }
}
