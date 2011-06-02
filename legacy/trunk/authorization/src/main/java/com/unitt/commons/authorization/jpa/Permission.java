/*
 * Copyright 2010 UnitT Software Inc.
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
package com.unitt.commons.authorization.jpa;


import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.unitt.commons.persist.CompositeKeyedPersistedObject;
import com.unitt.commons.persist.PersistedObjectImplBase;


@Entity
@Table( name = "permissions" )
@IdClass( PermissionPk.class )
@NamedQueries 
        ({ 
            @NamedQuery( name = "findPermissionsByPermissable", query = "SELECT p FROM Permission p WHERE p.permissableId = :permissableId and p.permissableTypeId = :permissableTypeId" ),
            @NamedQuery( name = "deletePermissionsByAssignable", query = "DELETE FROM Permission p WHERE p.assignableId = :assignableId and p.assignableTypeId = :assignableTypeId" )
        })
public class Permission extends PersistedObjectImplBase implements CompositeKeyedPersistedObject
{
    private static final long serialVersionUID = -274860572038055412L;

    
    // Composite key fields
    // ------------------------------------------------
    @Id
    @AttributeOverrides( { @AttributeOverride( name = "assignableId", column = @Column( name = "assignableId" ) ), @AttributeOverride( name = "assignableTypeId", column = @Column( name = "assignableTypeId" ) ), @AttributeOverride( name = "permissableId", column = @Column( name = "permissableId" ) ), @AttributeOverride( name = "permissableTypeId", column = @Column( name = "permissableTypeId" ) ) } )
    private Long              assignableId;
    private Long              assignableTypeId;
    private Long              permissableId;
    private Long              permissableTypeId;


    // Custom fields
    // ------------------------------------------------
    @Column( name = "permissionMask" )
    private Long              permissionMask;


    // constructors
    // ------------------------------------------------
    public Permission()
    {
        // default
    }

    public Permission( PermissionPk aKey )
    {
        assignableId = aKey.getAssignableId();
        assignableTypeId = aKey.getAssignableTypeId();
        permissableId = aKey.getPermissableId();
        permissableTypeId = aKey.getPermissableTypeId();
    }


    // persisted object logic
    // ------------------------------------------------
    public boolean isPersisted()
    {
        return getCreatedOn() != null;
    }


    // getters/setters
    // ------------------------------------------------
    public Long getAssignableId()
    {
        return assignableId;
    }

    public void setAssignableId( Long aAssignableId )
    {
        assignableId = aAssignableId;
    }

    public Long getAssignableTypeId()
    {
        return assignableTypeId;
    }

    public void setAssignableTypeId( Long aAssignableTypeId )
    {
        assignableTypeId = aAssignableTypeId;
    }

    public Long getPermissableId()
    {
        return permissableId;
    }

    public void setPermissableId( Long aPermissableId )
    {
        permissableId = aPermissableId;
    }

    public Long getPermissableTypeId()
    {
        return permissableTypeId;
    }

    public void setPermissableTypeId( Long aPermissableTypeId )
    {
        permissableTypeId = aPermissableTypeId;
    }

    public Long getPermissionMask()
    {
        return permissionMask;
    }

    public void setPermissionMask( Long aPermissionMask )
    {
        permissionMask = aPermissionMask;
    }


    // Object overrides
    // ------------------------------------------------
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( assignableId == null ) ? 0 : assignableId.hashCode() );
        result = prime * result + ( ( assignableTypeId == null ) ? 0 : assignableTypeId.hashCode() );
        result = prime * result + ( ( permissableId == null ) ? 0 : permissableId.hashCode() );
        result = prime * result + ( ( permissableTypeId == null ) ? 0 : permissableTypeId.hashCode() );
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
        Permission other = (Permission) obj;
        if ( assignableId == null )
        {
            if ( other.assignableId != null )
                return false;
        }
        else if ( !assignableId.equals( other.assignableId ) )
            return false;
        if ( assignableTypeId == null )
        {
            if ( other.assignableTypeId != null )
                return false;
        }
        else if ( !assignableTypeId.equals( other.assignableTypeId ) )
            return false;
        if ( permissableId == null )
        {
            if ( other.permissableId != null )
                return false;
        }
        else if ( !permissableId.equals( other.permissableId ) )
            return false;
        if ( permissableTypeId == null )
        {
            if ( other.permissableTypeId != null )
                return false;
        }
        else if ( !permissableTypeId.equals( other.permissableTypeId ) )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "Permission [assignableId=" + assignableId + ", assignableTypeId=" + assignableTypeId + ", permissableId=" + permissableId + ", permissableTypeId=" + permissableTypeId + ", permissionMask=" + permissionMask + "]";
    }
}
