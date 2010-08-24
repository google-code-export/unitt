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
package com.unitt.commons.session;


import java.io.Serializable;

import com.unitt.commons.foundation.lifecycle.Initializable;


public class SessionManager implements Initializable
{
    protected boolean             isInitialized;
    protected SessionProvider     provider;


    // session logic
    // ---------------------------------------------------------------------------
    public void create( String aSessionId, long aInactiveTimeToLiveInMillis )
    {
        getProvider().create( aSessionId, aInactiveTimeToLiveInMillis );
    }

    public void touch( String aSessionId )
    {
        getProvider().touch( aSessionId );
    }
    
    public void close(String aSessionId)
    {
        getProvider().close(aSessionId);
    }

    public void putValue( String aSessionId, String aKey, Serializable aValue )
    {
        getProvider().putValue( aSessionId, aKey, aValue );
    }

    public Serializable getValue( String aSessionId, String aKey )
    {
        return (Serializable) getProvider().getValue( aSessionId, aKey );
    }

    public void removeValue( String aSessionId, String aKey )
    {
        getProvider().removeValue( aSessionId, aKey );
    }


    // lifecycle logic
    // ---------------------------------------------------------------------------
    public void initialize()
    {
        if ( !isInitialized() )
        {
            isInitialized = true;
        }
    }

    public boolean isInitialized()
    {
        return isInitialized;
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public SessionProvider getProvider()
    {
        if ( provider == null )
        {
            throw new IllegalStateException( "Missing session provider." );
        }

        return provider;
    }

    public void setProvider( SessionProvider aProvider )
    {
        if ( isInitialized() )
        {
            throw new IllegalStateException( "Cannot set provider after session manager has been initialized" );
        }

        provider = aProvider;
    }
}
