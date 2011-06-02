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
import java.util.List;

import com.unitt.commons.metadata.Metadata;


public interface MetadataDao
{
    // metadata operations
    public Metadata save( Metadata aMetadata );

    public void save( Collection<Metadata> aMetadatas );

    // id operations
    public Metadata findById( Long aId );

    public List<Metadata> findAllByIds( Collection<Long> aIds );

    public void deleteById( Long aId );

    public void deleteAllByIds( Collection<Long> aIds );

    // type operations
    public Metadata findByType( String aType );

    public List<Metadata> findAllByTypes( Collection<String> aTypes );

    public void deleteByType( String aType );

    public void deleteAllByTypes( Collection<String> aTypes );
}
