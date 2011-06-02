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


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unitt.commons.metadata.Metadata;


public class MockMetadataDao implements MetadataDao
{
    public static final Long INITIAL_ID = 1L;
    public static final String INITIAL_TYPE = "TestType";
    public static final String INITIAL_DESCRIPTION = "TestDescription";
    
    protected Map<String, Metadata> metadatasByTypes = new HashMap<String, Metadata>();
    protected Map<Long, Metadata>   metadatasByIds   = new HashMap<Long, Metadata>();
    protected long currentId = 1;

    
    // constructors
    // ---------------------------------------------------------------------------
    public MockMetadataDao()
    {
        save(new MockMetadata(INITIAL_ID, INITIAL_TYPE, null, INITIAL_DESCRIPTION));
    }
    
    
    // dao logic
    // ---------------------------------------------------------------------------
    public void deleteAllByIds( Collection<Long> aIds )
    {
        List<Metadata> toDelete = findAllByIds( aIds );
        for ( Metadata metadata : toDelete )
        {
            metadatasByTypes.remove( metadata.getType() );
            metadatasByIds.remove( metadata.getId() );
        }
    }

    public void deleteAllByTypes( Collection<String> aTypes )
    {
        List<Metadata> toDelete = findAllByTypes( aTypes );
        for ( Metadata metadata : toDelete )
        {
            metadatasByTypes.remove( metadata.getType() );
            metadatasByIds.remove( metadata.getId() );
        }
    }

    public void deleteById( Long aId )
    {
        Metadata metadata = findById( aId );
        if ( metadata != null )
        {
            metadatasByIds.remove( metadata.getId() );
            metadatasByTypes.remove( metadata.getType() );
        }
    }

    public void deleteByType( String aType )
    {
        Metadata metadata = findByType( aType );
        if ( metadata != null )
        {
            metadatasByIds.remove( metadata.getId() );
            metadatasByTypes.remove( metadata.getType() );
        }
    }

    public List<Metadata> findAllByIds( Collection<Long> aIds )
    {
        List<Metadata> results = new ArrayList<Metadata>();

        for ( Long id : aIds )
        {
            Metadata metadata = metadatasByIds.get( id );
            if ( metadata != null )
            {
                results.add( metadata );
            }
        }

        return results;
    }

    public List<Metadata> findAllByTypes( Collection<String> aTypes )
    {
        List<Metadata> results = new ArrayList<Metadata>();

        for ( String name : aTypes )
        {
            Metadata metadata = metadatasByTypes.get( name );
            if ( metadata != null )
            {
                results.add( metadata );
            }
        }

        return results;
    }

    public Metadata findById( Long aId )
    {
        return metadatasByIds.get( aId );
    }

    public Metadata findByType( String aType )
    {
        return metadatasByTypes.get( aType );
    }

    public Metadata save( Metadata aMetadata )
    {
        MockMetadata metadata = new MockMetadata( aMetadata.getId(), aMetadata.getType(), null, aMetadata.getDescription() );
        if (aMetadata.getId() == null)
        {
            metadata.setId( currentId++ );
        }
        metadatasByIds.put( metadata.getId(), metadata );
        metadatasByTypes.put( metadata.getType(), metadata );
        return metadata;
    }

    public void save( Collection<Metadata> aMetadatas )
    {
        for ( Metadata metadata : aMetadatas )
        {
            save( metadata );
        }
    }
}
