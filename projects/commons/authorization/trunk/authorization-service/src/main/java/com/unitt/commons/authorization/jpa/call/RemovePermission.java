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
package com.unitt.commons.authorization.jpa.call;


import java.io.Serializable;

import com.unitt.commons.authorization.Assignable;
import com.unitt.commons.authorization.jpa.PermissionDao;


public class RemovePermission implements Runnable, Serializable
{
    private static final long          serialVersionUID = -7173433229971013721L;

    protected Assignable               assignable;
    protected PermissionDao            dao;
    protected PermissionRemoveCallback callback;


    public RemovePermission( PermissionRemoveCallback aCallback, Assignable aAssignable, PermissionDao aDao )
    {
        callback = aCallback;
        assignable = aAssignable;
        dao = aDao;
    }


    public void run()
    {
        try
        {
            dao.safeDeleteAll( assignable.getId(), assignable.getTypeId() );
            callback.onSuccess( null );
        }
        catch ( Exception e )
        {
            callback.onError( this, e );
        }
    }


    @Override
    public String toString()
    {
        return "RemovePermission [assignable=" + assignable + "]";
    }
}
