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
package com.unitt.commons.authorization;


import java.io.Serializable;


/**
 * Used to identify the entity that is being secured by permissions . Each
 * permissable is identified by a type and an id. These are determined by the
 * implementation and can be any long. The combination of type and id must be
 * unique. This allows the system to uniquely identify objects that may share
 * the same id across type boundaries. For example: an external system file with
 * an id of 1 would also have the type id (200) for external system files. This
 * would be different from a locally managed document with an id of 1 and the
 * type id (210) for locally managed documents.
 * 
 * @author Josh Morris
 */
public class Permissable implements Serializable
{
    private static final long serialVersionUID = -6379859480760169569L;

    protected long            id;
    protected long            typeId;


    // constructors
    // ---------------------------------------------------------------------------
    public Permissable()
    {
        // default
    }

    public Permissable( long aId, long aTypeId )
    {
        super();
        id = aId;
        typeId = aTypeId;
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public long getId()
    {
        return id;
    }

    public void setId( long aId )
    {
        id = aId;
    }

    public long getTypeId()
    {
        return typeId;
    }

    public void setTypeId( long aTypeId )
    {
        typeId = aTypeId;
    }


    // Object overrides
    // ------------------------------------------------
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) ( id ^ ( id >>> 32 ) );
        result = prime * result + (int) ( typeId ^ ( typeId >>> 32 ) );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        Permissable other = (Permissable) obj;
        if ( id != other.id )
            return false;
        if ( typeId != other.typeId )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "Permissable [id=" + id + ", typeId=" + typeId + "]";
    }
}
