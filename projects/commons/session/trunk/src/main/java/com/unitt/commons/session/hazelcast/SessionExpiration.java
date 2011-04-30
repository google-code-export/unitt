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

public class SessionExpiration implements Serializable
{
    private static final long serialVersionUID = -8679600099180867454L;
    
    protected long inactiveTimeToLive;
    protected long expiresAt;
    
    
    // constructors
    // ---------------------------------------------------------------------------
    public SessionExpiration()
    {
        //default
    }

    public SessionExpiration( long aInactiveTimeToLive, long aExpiresAt )
    {
        super();
        inactiveTimeToLive = aInactiveTimeToLive;
        expiresAt = aExpiresAt;
    }

    
    // getters & setters
    // ---------------------------------------------------------------------------
    public long getInactiveTimeToLive()
    {
        return inactiveTimeToLive;
    }

    public void setInactiveTimeToLive( long aInactiveTimeToLive )
    {
        inactiveTimeToLive = aInactiveTimeToLive;
    }

    public long getExpiresAt()
    {
        return expiresAt;
    }

    public void setExpiresAt( long aExpiresAt )
    {
        expiresAt = aExpiresAt;
    }
    
}
