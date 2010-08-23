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


import java.util.List;


public class InsufficentPrivelegesException extends Exception
{
    private static final long  serialVersionUID = -2578019419215440853L;

    protected Permissable      permissable;
    protected List<Assignable> assignables;


    // constructors
    // ---------------------------------------------------------------------------
    public InsufficentPrivelegesException( Permissable aPermissable, List<Assignable> aAssignables )
    {
        super();
        permissable = aPermissable;
        assignables = aAssignables;
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public Permissable getPermissable()
    {
        return permissable;
    }

    public void setPermissable( Permissable aPermissable )
    {
        permissable = aPermissable;
    }

    public List<Assignable> getAssignables()
    {
        return assignables;
    }

    public void setAssignables( List<Assignable> aAssignables )
    {
        assignables = aAssignables;
    }
}
