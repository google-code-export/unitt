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

public class HasPermissionCallback implements RunnableCallback<HasPermission, Boolean>
{
    private static Logger      logger = LoggerFactory.getLogger( HasPermissionCallback.class );
    
    protected int total;
    protected int current;
    protected boolean hasResult;
    protected Boolean result = false;

    public void onSuccess(Boolean aResult)
    {
        handleCompletion( aResult );
    }
    
    public void onError(HasPermission aRunnable, Throwable aThrowable)
    {
        logger.error( "Could not determine if permission applies: " + aRunnable, aThrowable );
        handleCompletion( false );
    }
    
    protected void handleCompletion(Boolean aResult)
    {
        try
        {
            current++;
            if (aResult)
            {
                hasResult = true;
                result = true;
                synchronized(this)
                {
                    notifyAll();
                }
            }
        }
        catch ( Exception e )
        {
            logger.error("Could not acquire result.", e);
        }
        if ( current == total )
        {
            hasResult = true;
            synchronized(this)
            {
                notifyAll();
            }
        }
    }
    
    public boolean hasResult()
    {
        return hasResult;
    }

    public Boolean getResult()
    {
        return result;
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
}
