package com.unitt.commons.authentication;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.unitt.commons.foundation.lifecycle.Initializable;
import com.unitt.commons.security.Identity;
import com.unitt.commons.security.SecurityContext;
import com.unitt.commons.security.SecurityContextFactoryImpl;


/**
 * Authentication service implementation
 * 
 * @author Josh Morris
 */
public class AuthenticationImpl implements Authentication, Initializable
{
    private static Log                     log            = LogFactory.getLog( AuthenticationImpl.class );

    protected List<AuthenticationProvider> providers      = new ArrayList<AuthenticationProvider>();
    protected boolean                      isInitialized  = false;
    protected SecurityContextFactory       contextFactory = new SecurityContextFactoryImpl();


    // getters & setters
    // ---------------------------------------------------------------------------
    public List<AuthenticationProvider> getProviders()
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

    public SecurityContextFactory getContextFactory()
    {
        return contextFactory;
    }

    public void setContextFactory( SecurityContextFactory aContextFactory )
    {
        contextFactory = aContextFactory;
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
    /**
     * Authenticates the user using the specified token.
     * 
     * @param aToken
     *            there must be a provider that can handle this token
     * 
     * @return valid security context when successfully authenticated
     * 
     * @throws BadCredentialsException
     *             if the credentials are not valid
     * @throws IllegalArgumentException
     *             if no provider is found that can handle the token
     */
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
            secondary.addAll( getSecondaryIdentities( aIdentity ) );
        }
        catch ( Exception e )
        {
            log.error( "Could not load secondary identities for: " + aIdentity, e );
        }

        // create context
        return getContextFactory().createContext( aIdentity, secondary );
    }

    protected List<Identity> getSecondaryIdentities( Identity aIdentity )
    {
        return Collections.emptyList();
    }
}
