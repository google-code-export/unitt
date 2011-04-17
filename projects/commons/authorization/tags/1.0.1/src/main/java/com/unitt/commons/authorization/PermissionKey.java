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
 * Simple bean used to index the permissions held by an assignable on a permissable.
 * 
 * @author Josh Morris
 */
public class PermissionKey implements Serializable
{
    private static final long serialVersionUID = 2818642294748136055L;
    
    protected long permissableId;
    protected long permissableTypeId;
    protected long assignableId;
    protected long assignableTypeId;

    
    // constructors
    // ---------------------------------------------------------------------------
    public PermissionKey()
    {
        // default
    }
    
    public PermissionKey(Assignable aAssignable, Permissable aPermissable)
    {
        this(aPermissable.getId(), aPermissable.getTypeId(), aAssignable.getId(), aAssignable.getTypeId());
    }

    public PermissionKey( long aPermissableId, long aPermissableTypeId, long aAssignableId, long aAssignableTypeId )
    {
        super();
        permissableId = aPermissableId;
        permissableTypeId = aPermissableTypeId;
        assignableId = aAssignableId;
        assignableTypeId = aAssignableTypeId;
    }

    
    // getters & setters
    // ---------------------------------------------------------------------------
    /**
     * The id component of the unique identifier for the permissable associated with this permission. 
     */
    public long getPermissableId()
    {
        return permissableId;
    }

    public void setPermissableId( long aPermissableId )
    {
        permissableId = aPermissableId;
    }

    /**
     * The type component of the unique identifier for the permissable associated with this permission.
     */
    public long getPermissableTypeId()
    {
        return permissableTypeId;
    }

    public void setPermissableTypeId( long aPermissableTypeId )
    {
        permissableTypeId = aPermissableTypeId;
    }

    /**
     * The id component of the unique identifier for the assignable associated with this permission. 
     */
    public long getAssignableId()
    {
        return assignableId;
    }

    public void setAssignableId( long aAssignableId )
    {
        assignableId = aAssignableId;
    }

    /**
     * The type component of the unique identifier for the assignable associated with this permission.
     */
    public long getAssignableTypeId()
    {
        return assignableTypeId;
    }

    public void setAssignableTypeId( long aAssignableTypeId )
    {
        assignableTypeId = aAssignableTypeId;
    }

    
    // Object overrides
    // ------------------------------------------------
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) ( assignableId ^ ( assignableId >>> 32 ) );
        result = prime * result + (int) ( assignableTypeId ^ ( assignableTypeId >>> 32 ) );
        result = prime * result + (int) ( permissableId ^ ( permissableId >>> 32 ) );
        result = prime * result + (int) ( permissableTypeId ^ ( permissableTypeId >>> 32 ) );
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
        PermissionKey other = (PermissionKey) obj;
        if ( assignableId != other.assignableId )
            return false;
        if ( assignableTypeId != other.assignableTypeId )
            return false;
        if ( permissableId != other.permissableId )
            return false;
        if ( permissableTypeId != other.permissableTypeId )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "PermissionKey [assignableId=" + assignableId + ", assignableTypeId=" + assignableTypeId + ", permissableId=" + permissableId + ", permissableTypeId=" + permissableTypeId + "]";
    }
}
