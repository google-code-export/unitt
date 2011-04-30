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
package com.unitt.commons.metadata.hazelcast;


import com.hazelcast.core.HazelcastInstance;
import com.unitt.commons.metadata.Metadata;
import com.unitt.commons.metadata.MetadataProvider;


public class HazelcastProvider implements MetadataProvider
{
    public static final String  MAP_KEY_PREFIX_ID   = "System:Metadata:Id";
    public static final String  MAP_KEY_PREFIX_TYPE = "System:Metadata:Type";

    protected HazelcastInstance client;
    protected MetadataDao       dao;


    // getters & setters
    // ---------------------------------------------------------------------------
    public HazelcastInstance getClient()
    {
        if ( client == null )
        {
            throw new IllegalStateException( "Missing hazelcast client" );
        }
        return client;
    }

    public void setClient( HazelcastInstance aClient )
    {
        client = aClient;
    }

    public MetadataDao getDao()
    {
        if ( dao == null )
        {
            throw new IllegalStateException( "Missing metadata dao" );
        }
        return dao;
    }

    public void setDao( MetadataDao aDao )
    {
        dao = aDao;
    }


    // Metadata provider implementation
    // ---------------------------------------------------------------------------
    public Metadata getMetadata( Long aId )
    {
        return (Metadata) getClient().getMap( MAP_KEY_PREFIX_ID ).get( aId );
    }

    public Metadata getMetadata( String aType )
    {
        return (Metadata) getClient().getMap( MAP_KEY_PREFIX_TYPE ).get( aType );
    }

    public Metadata putMetadata( Metadata aMetadata )
    {
        Metadata metadata = aMetadata;

        // handle insert, if needed
        if ( aMetadata.getId() == null || aMetadata.getId() == 0 )
        {
            metadata = getDao().save( aMetadata );
        }

        // just push into maps, it will be updated by store
        getClient().getMap( MAP_KEY_PREFIX_ID ).put( metadata.getId(), metadata );
        getClient().getMap( MAP_KEY_PREFIX_TYPE ).put( metadata.getType(), metadata );
        return metadata;
    }

    public void removeMetadata( Metadata aMetadata )
    {
        if ( aMetadata.getId() != null && aMetadata.getId() != 0 )
        {
            getClient().getMap( MAP_KEY_PREFIX_ID ).remove( aMetadata.getId() );
            getClient().getMap( MAP_KEY_PREFIX_TYPE ).remove( aMetadata.getType() );
        }
    }
}
