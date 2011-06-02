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


public class ComplexMapModel<K, L> extends ComplexModelItemsImpl<ComplexMapModel<K, L>, K, L>
{
    protected Map<K, ListModel<L>>                             items                = new HashMap<K, ListModel<L>>();
    protected List<ModelChangeListener<ComplexMapModel<K, L>>> modelChangeListeners = new ArrayList<ModelChangeListener<ComplexMapModel<K, L>>>();


    // constructors
    // --------------------------------------------------------------------
    public ComplexMapModel( Map<K, List<L>> aItems )
    {
        super();
        setAllItems( aItems );
    }

    public ComplexMapModel()
    {
        // default
    }


    // Destructable implementation
    // --------------------------------------------------------------------
    @Override
    public void destroy()
    {
        super.destroy();
        if ( items != null )
        {
            items.clear();
            items = null;
        }
        if ( modelChangeListeners != null )
        {
            modelChangeListeners.clear();
            modelChangeListeners = null;
        }
    }


    // manage model
    // --------------------------------------------------------------------
    public Map<K, ListModel<L>> getAllItems()
    {
        return Collections.unmodifiableMap( items );
    }

    public void setAllItems( Map<K, List<L>> aItems )
    {
        if ( aItems != null )
        {
            for ( Map.Entry<K, List<L>> entry : aItems.entrySet() )
            {
                items.put( entry.getKey(), new ListModel<L>( entry.getValue() ) );
            }
        }
        fireModelChange( ModelChangeEvent.EventType.MODEL_CHANGED_EVENT );
    }

    public void putItems( K aKey, List<L> aItems )
    {
        items.put( aKey, new ListModel<L>( aItems ) );
    }

    public void removeItems( Collection<K> aKeys )
    {
        for ( K key : aKeys )
        {
            removeItems( key );
        }
    }

    public void removeItems( K aKey )
    {
        ListModel<L> model = items.get( aKey );
        if ( model != null && model.getItems() != null )
        {
            List<L> items = new ArrayList<L>( model.getItems() );
            for ( L item : items )
            {
                removeItem( aKey, item );
            }
        }
    }

    public void addItem( K aKey )
    {
        if ( !items.containsKey( aKey ) )
        {
            items.put( aKey, new ListModel<L>() );
            fireModelChange( aKey, ModelChangeEvent.EventType.MODEL_ADD_ITEM_EVENT );
        }
    }

    public void removeItem( K aKey )
    {
        if ( items.containsKey( aKey ) )
        {
            items.remove( aKey );
            fireModelChange( aKey, ModelChangeEvent.EventType.MODEL_REMOVE_ITEM_EVENT );
        }
    }

    public void addItem( K aKey, L aItem )
    {
        ListModel<L> model = items.get( aKey );
        if ( model != null )
        {
            model.addItem( aItem );
            fireModelChange( aKey, aItem, ModelChangeEvent.EventType.MODEL_ADD_ITEM_EVENT );
        }
    }

    public void removeItem( K aKey, L aItem )
    {
        ListModel<L> model = items.get( aKey );
        if ( model != null )
        {
            model.removeItem( aItem );
            fireModelChange( aKey, aItem, ModelChangeEvent.EventType.MODEL_REMOVE_ITEM_EVENT );
        }
    }
}
