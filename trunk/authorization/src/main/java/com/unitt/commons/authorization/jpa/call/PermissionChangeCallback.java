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


public class PermissionChangeCallback implements RunnableCallback<ChangePermission, Long>
{
    private static final Logger logger = LoggerFactory.getLogger( PermissionChangeCallback.class );
    
    protected int total;
    protected int current;

    public PermissionChangeCallback()
    {
    }

    public void onSuccess(Long aResult)
    {
        handleCompletion();
    }
    
    protected void handleCompletion()
    {
        current++;
        if ( current == total )
        {
            synchronized(this)
            {
                notifyAll();
            }
        }
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal( int aTotal )
    {
        total = aTotal;
    }

    public int getCurrent()
    {
        return current;
    }
    
    public void onError(ChangePermission aPermission, Throwable aThrowable)
    {
        logger.error( "Could not change permission: " + aPermission, aThrowable );
        handleCompletion();
    }
}
