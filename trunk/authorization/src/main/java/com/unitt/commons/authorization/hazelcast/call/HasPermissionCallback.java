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
package com.unitt.commons.authorization.hazelcast.call;

import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hazelcast.core.ExecutionCallback;

public class HasPermissionCallback implements ExecutionCallback<Boolean>
{
    private static Log      log = LogFactory.getLog( HasPermissionCallback.class );
    
    protected int total;
    protected int current;
    protected boolean hasResult;
    protected Boolean result = false;

    public void done( Future<Boolean> aResult )
    {
        try
        {
            current++;
            if (aResult.get())
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
            log.error("Could not acquire result.", e);
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
