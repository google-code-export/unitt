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
package com.unitt.commons.foundation.client.model;

import java.util.ArrayList;
import java.util.List;

import com.unitt.commons.foundation.client.model.event.ModelChangeEvent;
import com.unitt.commons.foundation.client.model.event.ModelChangeListener;

public class SimpleModel<T> extends ModelBaseImpl<SimpleModel<T>>
{
    protected T item;
    protected boolean silent;
    protected List<ModelChangeListener<SimpleModel<T>>> modelChangeListeners = new ArrayList<ModelChangeListener<SimpleModel<T>>>();


    // constructors
    // --------------------------------------------------------------------
    public SimpleModel()
    {
        // default
    }

    public SimpleModel( T aItem )
    {
        item = aItem;
    }
    
    
    // manage model
    // --------------------------------------------------------------------
    public void setItem(T aItem)
    {
        boolean hasChanged = false;
        if (item != aItem)
        {
            if (aItem != null && item != null)
            {
                hasChanged = !aItem.equals(item);
            }
            else if (aItem != null || item != null)
            {
                hasChanged = true;
            }
        }
        item = aItem;
        if (hasChanged)
        {
            fireModelChange(ModelChangeEvent.EventType.MODEL_CHANGED_EVENT);
        }
    }

    public T getItem()
    {
        return item;
    }
}
