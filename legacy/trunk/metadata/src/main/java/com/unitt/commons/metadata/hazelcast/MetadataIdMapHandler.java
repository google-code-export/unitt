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


public class MetadataIdMapHandler implements MapLoader<Long, Metadata>, MapStore<Long, Metadata>
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
    public Metadata load( Long aId )
    {
        return getDao().findById( aId );
    }

    public Map<Long, Metadata> loadAll( Collection<Long> aIds )
    {
        Map<Long, Metadata> results = new HashMap<Long, Metadata>();

        // grab results from dao
        List<Metadata> found = getDao().findAllByIds( aIds );
        for ( Metadata metadata : found )
        {
            results.put( metadata.getId(), metadata );
        }

        return results;
    }


    // store implementation
    // ---------------------------------------------------------------------------
    public void delete( Long aId )
    {
        getDao().deleteById( aId );
    }

    public void deleteAll( Collection<Long> aIds )
    {
        getDao().deleteAllByIds( aIds );
    }

    public void store( Long aId, Metadata aMetadata )
    {
        getDao().save( aMetadata );
    }

    public void storeAll( Map<Long, Metadata> aMetadatas )
    {
        getDao().save( aMetadatas.values() );
    }
}
