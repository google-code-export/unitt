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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStore;
import com.unitt.commons.metadata.Metadata;

public class MetadataTypeMapHandler implements MapLoader<String, Metadata>, MapStore<String, Metadata>
{
    protected MetadataDao dao;


    // getters & setters
    // ---------------------------------------------------------------------------
    public MetadataDao getDao()
    {
        return dao;
    }

    public void setDao( MetadataDao aDao )
    {
        dao = aDao;
    }


    // loader implementation
    // ---------------------------------------------------------------------------
    public Metadata load( String aType )
    {
        return getDao().findByType( aType );
    }

    public Map<String, Metadata> loadAll( Collection<String> aTypes )
    {
        Map<String, Metadata> results = new HashMap<String, Metadata>();

        // grab results from dao
        List<Metadata> found = getDao().findAllByTypes( aTypes );
        for ( Metadata metadata : found )
        {
            results.put( metadata.getType(), metadata );
        }

        return results;
    }


    // store implementation
    // ---------------------------------------------------------------------------
    public void delete( String aType )
    {
        //do nothing, id handler will manage persistence
    }

    public void deleteAll( Collection<String> aTypes )
    {
        //do nothing, id handler will manage persistence
    }

    public void store( String aType, Metadata aMetadata )
    {
        //do nothing, id handler will manage persistence
    }

    public void storeAll( Map<String, Metadata> aMetadatas )
    {
        //do nothing, id handler will manage persistence
    }
}
