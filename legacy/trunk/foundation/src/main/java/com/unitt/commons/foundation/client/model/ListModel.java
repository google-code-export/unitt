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
import java.util.List;

import com.unitt.commons.foundation.client.model.event.ModelChangeEvent;
import com.unitt.commons.foundation.client.model.event.ModelChangeListener;
import com.unitt.commons.foundation.client.model.event.ModelChangeEvent.EventType;

public class ListModel<T> extends ModelItemsImpl<ListModel<T>, T>
{
    protected List<T> items = new ArrayList<T>();
    protected List<ModelChangeListener<ListModel<T>>> modelChangeListeners = new ArrayList<ModelChangeListener<ListModel<T>>>();

    // constructors
    // --------------------------------------------------------------------
    public ListModel(List<T> aItems)
    {
        super();
        items = aItems;
    }

    public ListModel()
    {
        // default
    }

    // Destructable implementation
    // --------------------------------------------------------------------
    @Override
    public void destroy()
    {
        super.destroy();
        items.clear();
        items = null;
    }

    // manage model
    // --------------------------------------------------------------------
    public List<T> getItems()
    {
        if ( items == null )
        {
            items = new ArrayList<T>();
        }

        return Collections.unmodifiableList( items );
    }

    public void setItems( List<T> aItems )
    {
        items = aItems;
        fireModelChange( ModelChangeEvent.EventType.MODEL_CHANGED_EVENT );
    }

    public void addItems( Collection<T> aItems )
    {
        for ( T item : aItems )
        {
            addItem( item );
        }
    }

    public void removeItems( Collection<T> aItems )
    {
        for ( T item : aItems )
        {
            removeItem( item );
        }
    }

    public void addItem( T aItem )
    {
        items.add( aItem );
        fireModelChange( aItem, ModelChangeEvent.EventType.MODEL_ADD_ITEM_EVENT );
    }

    public void removeItem( T aItem )
    {
        items.remove( aItem );
        fireModelChange( aItem, ModelChangeEvent.EventType.MODEL_REMOVE_ITEM_EVENT );
    }
    
    public void clear()
    {
        items.clear();
        fireModelChange( EventType.MODEL_CHANGED_EVENT );
    }
}
