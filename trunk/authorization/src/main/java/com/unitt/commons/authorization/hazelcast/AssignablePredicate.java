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
package com.unitt.commons.authorization.hazelcast;


import com.hazelcast.core.MapEntry;
import com.hazelcast.query.Predicate;
import com.unitt.commons.authorization.Assignable;


public class AssignablePredicate implements Predicate
{
    private static final long serialVersionUID = -8845741383867782704L;
    
    protected long assignableId;
    protected long assignableTypeId;

    public AssignablePredicate( Assignable aAssignable )
    {
        this( aAssignable.getId(), aAssignable.getTypeId() );
    }

    public AssignablePredicate( long aAssignableId, long aAssignableTypeId )
    {
        super();
        assignableId = aAssignableId;
        assignableTypeId = aAssignableTypeId;
    }

    @SuppressWarnings( "unchecked" )
    public boolean apply( MapEntry aEntry )
    {
        if (aEntry.getKey() instanceof PermissionKey)
        {
            PermissionKey key = (PermissionKey) aEntry.getKey();
            if (key.getAssignableId() == assignableId)
            {
                return key.getAssignableTypeId() == assignableTypeId;
            }
        }
        
        return false;
    }
}
