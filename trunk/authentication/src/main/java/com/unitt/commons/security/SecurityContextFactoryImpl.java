package com.unitt.commons.security;

import java.util.List;

import com.unitt.commons.authentication.SecurityContextFactory;
import com.unitt.commons.security.Identity;
import com.unitt.commons.security.SecurityContext;
import com.unitt.commons.security.SecurityContextImplBase;

public class SecurityContextFactoryImpl implements SecurityContextFactory
{
    public SecurityContext createContext( Identity aPrimary, List<Identity> aSecondary )
    {
        SecurityContextImplBase context = new SecurityContextImplBase();
        context.setPrimaryIdentity( aPrimary );
        context.setSecondaryIdentities( aSecondary );
        return context;
    }
}
