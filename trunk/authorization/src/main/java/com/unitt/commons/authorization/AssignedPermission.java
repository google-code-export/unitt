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

import com.unitt.commons.authorization.util.BitMasks;


/**
 * Used to identify a permission for a specific assignable. This permission
 * could be just one permission, or an aggregation of several permissions. The
 * specific permissions that exist can be determined using the BitMasks utility.
 * 
 * @author Josh Morris
 * 
 * @see Assignable
 * @see BitMasks
 */
public class AssignedPermission implements Serializable
{
    private static final long serialVersionUID = -3376596355657590506L;

    protected long            permission;
    protected long            id;
    protected long            typeId;


    // constructors
    // ---------------------------------------------------------------------------
    public AssignedPermission()
    {
        // default
    }

    public AssignedPermission( long aId, long aTypeId )
    {
        id = aId;
        typeId = aTypeId;
    }

    public AssignedPermission( long aId, long aTypeId, long aPermission )
    {
        id = aId;
        typeId = aTypeId;
        permission = aPermission;
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

    public long getPermission()
    {
        return permission;
    }

    public void setPermission( long aPermission )
    {
        permission = aPermission;
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
        AssignedPermission other = (AssignedPermission) obj;
        if ( id != other.id )
            return false;
        if ( typeId != other.typeId )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "AssignedPermission [id=" + id + ", typeId=" + typeId + ", permission=" + permission + "]";
    }
}
