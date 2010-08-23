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
package com.unitt.commons.foundation.client.model.manager;

import java.io.Serializable;

public abstract class ManagedModelKeyImpl<T> implements ManagedModelKey<T>, Serializable
{
    private static final long serialVersionUID = -7114870127098746031L;

    protected String key;

    public ManagedModelKeyImpl()
    {
        // default
    }

    public ManagedModelKeyImpl(String aKey)
    {
        key = aKey;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String aKey)
    {
        key = aKey;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        ManagedModelKeyImpl other = (ManagedModelKeyImpl) obj;
        if (key == null)
        {
            if (other.key != null)
            {
                return false;
            }
        }
        else if (!key.equals(other.key))
        {
            return false;
        }
        return true;
    }
}
