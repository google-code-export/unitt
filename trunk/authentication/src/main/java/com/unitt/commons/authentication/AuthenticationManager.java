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
package com.unitt.commons.authentication;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.unitt.commons.foundation.lifecycle.Initializable;
import com.unitt.commons.identity.IdentityManager;
import com.unitt.commons.security.Identity;
import com.unitt.commons.security.SecurityContext;
import com.unitt.commons.security.SecurityContextHelper;


public class AuthenticationManager implements Initializable
{
    private static Log                     log           = LogFactory.getLog( AuthenticationManager.class );

    protected List<AuthenticationProvider> providers     = new ArrayList<AuthenticationProvider>();
    protected boolean                      isInitialized = false;
    protected IdentityManager              identityManager;


    // getters & setters
    // ---------------------------------------------------------------------------
    protected List<AuthenticationProvider> getProviders()
    {
        return providers;
    }

    public void setProviders( List<AuthenticationProvider> aProviders )
    {
        if ( isInitialized() )
        {
            throw new IllegalStateException( "Cannot set providers after authentication manager has been initialized" );
        }

        providers.clear();
        providers.addAll( aProviders );
    }


    public IdentityManager getIdentityManager()
    {
        return identityManager;
    }

    public void setIdentityManager( IdentityManager aIdentityManager )
    {
        if ( isInitialized() )
        {
            throw new IllegalStateException( "Cannot set identity manager after authentication manager has been initialized" );
        }

        identityManager = aIdentityManager;
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


    // authentication logic
    // ---------------------------------------------------------------------------
    public SecurityContext authenticate( AuthenticationToken aToken ) throws BadCredentialsException
    {
        // verify we have initialized
        if ( !isInitialized() )
        {
            initialize();
        }

        // authenticate with first valid provider
        for ( AuthenticationProvider provider : getProviders() )
        {
            if ( provider.accepts( aToken ) )
            {
                return createContext( provider.authenticate( aToken ) );
            }
        }

        // could not find a valid provider
        throw new IllegalArgumentException( "No Provider for specified Token:" + aToken );
    }

    protected SecurityContext createContext( Identity aIdentity )
    {
        // grab secondary identities
        List<Identity> secondary = new ArrayList<Identity>();
        try
        {
            secondary.addAll( getIdentityManager().getSecondaryIdentities( aIdentity.getId(), null ) );
        }
        catch ( Exception e )
        {
            log.error( "Could not load secondary identities for: " + aIdentity, e );
        }

        // create context
        return SecurityContextHelper.createContext( aIdentity, secondary );
    }
}
