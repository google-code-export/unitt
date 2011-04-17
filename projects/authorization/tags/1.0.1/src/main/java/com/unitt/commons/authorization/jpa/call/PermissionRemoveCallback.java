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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PermissionRemoveCallback implements RunnableCallback<RemovePermission, Long>
{
    private static final Logger logger = LoggerFactory.getLogger( PermissionRemoveCallback.class );

    public PermissionRemoveCallback()
    {
    }

    public void onSuccess(Long aResult)
    {
        handleCompletion();
    }
    
    public void onError(RemovePermission aOperation, Throwable aThrowable)
    {
        logger.error( "Could not remove permission: " + aOperation, aThrowable );
        handleCompletion();
    }
    
    protected void handleCompletion()
    {
        synchronized(this)
        {
            notifyAll();
        }
    }
}
