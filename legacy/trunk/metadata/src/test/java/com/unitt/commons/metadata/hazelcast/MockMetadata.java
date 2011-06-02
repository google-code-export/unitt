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


import com.unitt.commons.metadata.Metadata;


public class MockMetadata implements Metadata
{
    private static final long serialVersionUID = 1L;

    protected String          description;
    protected Long            id;
    protected String          type;
    protected String name;


    // constructors
    // ---------------------------------------------------------------------------
    public MockMetadata()
    {
    }

    public MockMetadata( Long aId, String aType, String aName, String aDescription )
    {
        id = aId;
        type = aType;
        name = aName;
        description = aDescription;
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String aDescription )
    {
        description = aDescription;
    }

    public Long getId()
    {
        return id;
    }

    public void setId( Long aId )
    {
        id = aId;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String aType )
    {
        type = aType;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String aName )
    {
        name = aName;
    }
}
