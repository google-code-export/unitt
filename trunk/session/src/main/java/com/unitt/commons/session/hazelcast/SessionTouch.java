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

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;

public class SessionTouch implements Runnable, Serializable
{
    private static final long serialVersionUID = 1319622540608735260L;
    
    protected long expiresFrom = System.currentTimeMillis();
    protected String sessionId;
    
    public SessionTouch()
    {
        //default
    }
    
    public SessionTouch( long aExpiresFrom, String aSessionId )
    {
        super();
        expiresFrom = aExpiresFrom;
        sessionId = aSessionId;
    }
    
    public SessionTouch( String aSessionId )
    {
        sessionId = aSessionId;
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public long getExpiresFrom()
    {
        return expiresFrom;
    }

    public void setExpiresFrom( long aExpiresFrom )
    {
        expiresFrom = aExpiresFrom;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId( String aSessionId )
    {
        sessionId = aSessionId;
    }
    

    // runnable logic
    // ---------------------------------------------------------------------------
    public void run()
    {
        IMap<Object, Object> map = Hazelcast.getDefaultInstance().getMap( HazelcastProvider.getMapKey( getSessionId() ) );
        SessionExpiration expiration = (SessionExpiration) map.get( HazelcastProvider.KEY_EXPIRATION );
        expiration.setExpiresAt( getExpiresFrom() + expiration.getInactiveTimeToLive() );
        map.put( HazelcastProvider.KEY_EXPIRATION, expiration );
    }
}
