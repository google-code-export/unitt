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
package com.unitt.commons.metadata;


public class MetadataManager
{
    protected boolean isInitialized;
    protected MetadataProvider provider;

    
    // getters & setters
    // ---------------------------------------------------------------------------
    public Metadata getMetadata(String aType)
    {
        return getProvider().getMetadata( aType );
    }
    
    public Metadata getMetadata(Long aId)
    {
        return getProvider().getMetadata( aId );
    }
    
    public Metadata putMetadata(Metadata aMetadata)
    {
        return getProvider().putMetadata( aMetadata );
    }
    
    public void removeMetadata(Metadata aMetadata)
    {
        getProvider().removeMetadata( aMetadata );
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

    
    // provider logic
    // ---------------------------------------------------------------------------
    public MetadataProvider getProvider()
    {
        if (provider == null)
        {
            throw new IllegalStateException("Missing metadata provider.");
        }
        
        return provider;
    }

    public void setProvider( MetadataProvider aProvider )
    {
        if ( isInitialized() )
        {
            throw new IllegalStateException( "Cannot set provider after metadata manager has been initialized" );
        }
        
        provider = aProvider;
    }
}
