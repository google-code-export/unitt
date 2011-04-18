package com.unitt.commons.authentication;

import java.rmi.RemoteException;

import com.unitt.commons.security.SecurityContext;

/**
 * Authentication service interface
 */
public interface Authentication 
{
    public SecurityContext authenticate( AuthenticationToken aToken ) throws BadCredentialsException, RemoteException;
}
