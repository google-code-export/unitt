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


import java.io.Serializable;

import javax.persistence.Embeddable;

import com.unitt.commons.authorization.PermissionKey;


@Embeddable
public class PermissionPk implements Serializable
{
    private static final long serialVersionUID = -6441253151401428905L;

    private Long              assignableId;
    private Long              assignableTypeId;
    private Long              permissableId;
    private Long              permissableTypeId;


    // constructors
    // ------------------------------------------------
    public PermissionPk()
    {
        // default
    }

    public PermissionPk( Long aAssignableId, Long aAssignableTypeId, Long aPermissableId, Long aPermissableTypeId )
    {
        super();
        assignableId = aAssignableId;
        assignableTypeId = aAssignableTypeId;
        permissableId = aPermissableId;
        permissableTypeId = aPermissableTypeId;
    }

    public PermissionPk( PermissionKey aKey )
    {
        assignableId = aKey.getAssignableId();
        assignableTypeId = aKey.getAssignableTypeId();
        permissableId = aKey.getPermissableId();
        permissableTypeId = aKey.getPermissableTypeId();
    }


    // getters/setters
    // ------------------------------------------------
    protected Long getAssignableId()
    {
        return assignableId;
    }

    protected void setAssignableId( Long aAssignableId )
    {
        assignableId = aAssignableId;
    }

    protected Long getAssignableTypeId()
    {
        return assignableTypeId;
    }

    protected void setAssignableTypeId( Long aAssignableTypeId )
    {
        assignableTypeId = aAssignableTypeId;
    }

    protected Long getPermissableId()
    {
        return permissableId;
    }

    protected void setPermissableId( Long aPermissableId )
    {
        permissableId = aPermissableId;
    }

    protected Long getPermissableTypeId()
    {
        return permissableTypeId;
    }

    protected void setPermissableTypeId( Long aPermissableTypeId )
    {
        permissableTypeId = aPermissableTypeId;
    }
}
