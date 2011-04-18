package com.unitt.commons.authentication;

import java.util.List;

import com.unitt.commons.security.Identity;
import com.unitt.commons.security.SecurityContext;

public interface SecurityContextFactory
{
    public SecurityContext createContext(Identity aPrimary, List<Identity> aSecondary);
}
