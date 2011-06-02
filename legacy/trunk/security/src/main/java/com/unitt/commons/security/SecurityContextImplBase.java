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
package com.unitt.commons.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SecurityContextImplBase implements SecurityContext
{
    private static final long serialVersionUID = -463121247201177019L;
    
    protected Identity primaryIdentity;
    protected List<Identity> secondaryIdentities = Collections.emptyList();

        
    // security context implementation
    // ---------------------------------------------------------------------------
    public Identity getPrimaryIdentity()
    {
        return primaryIdentity;
    }

    public List<Identity> getSecondaryIdentities()
    {
        return secondaryIdentities;
    }

    
    // writable entries
    // ---------------------------------------------------------------------------
    protected void setPrimaryIdentity( Identity aPrimaryIdentity )
    {
        primaryIdentity = aPrimaryIdentity;
    }

    protected void setSecondaryIdentities( List<Identity> aSecondaryIdentities )
    {
        secondaryIdentities = new ArrayList<Identity>(aSecondaryIdentities);
    }
}
