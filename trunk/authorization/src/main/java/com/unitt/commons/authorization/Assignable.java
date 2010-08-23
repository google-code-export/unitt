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


public class Assignable implements Serializable
{
    private static final long serialVersionUID = -6379859480760169569L;

    protected long            id;
    protected long            typeId;


    // constructors
    // ---------------------------------------------------------------------------
    public Assignable()
    {
        // default
    }

    public Assignable( long aId, long aTypeId )
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
        Assignable other = (Assignable) obj;
        if ( id != other.id )
            return false;
        if ( typeId != other.typeId )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "Assignable [id=" + id + ", typeId=" + typeId + "]";
    }
}
