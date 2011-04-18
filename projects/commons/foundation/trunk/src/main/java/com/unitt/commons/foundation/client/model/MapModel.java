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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unitt.commons.foundation.client.model.event.ModelChangeEvent;
import com.unitt.commons.foundation.client.model.event.ModelChangeListener;

public class MapModel<K, V> extends ModelItemsImpl<MapModel<K, V>, K>
{
    protected Map<K, V> items = new HashMap<K, V>();
    protected List<ModelChangeListener<MapModel<K, V>>> modelChangeListeners = new ArrayList<ModelChangeListener<MapModel<K, V>>>();

    // constructors
    // --------------------------------------------------------------------
    public MapModel(Map<K, V> aItems)
    {
        super();
        items = aItems;
    }

    public MapModel()
    {
        // default
    }

    //Destructable implementation
    //--------------------------------------------------------------------
    @Override
    public void destroy()
    {
        super.destroy();
        items.clear();
        items = null;
    }

    // manage model
    // --------------------------------------------------------------------
    public Map<K, V> getItems()
    {
        return Collections.unmodifiableMap(items);
    }

    public void setItems(Map<K, V> aItems)
    {
        items = aItems;
        fireModelChange(ModelChangeEvent.EventType.MODEL_CHANGED_EVENT);
    }

    public void putItems(Map<K, V> aItems)
    {
        for (Map.Entry<K, V> entry : aItems.entrySet())
        {
            putItem(entry.getKey(), entry.getValue());
        }
    }

    public void removeItems(Collection<K> aKeys)
    {
        for (K key : aKeys)
        {
            items.remove(key);
        }
    }

    public void putItem(K aKey, V aItem)
    {
        items.put(aKey, aItem);
        fireModelChange(aKey, ModelChangeEvent.EventType.MODEL_ADD_ITEM_EVENT);
    }

    public void removeItem(K aKey)
    {
        items.remove(aKey);
        fireModelChange(aKey, ModelChangeEvent.EventType.MODEL_REMOVE_ITEM_EVENT);
    }
}
