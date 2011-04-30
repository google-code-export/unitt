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
import com.unitt.commons.foundation.lifecycle.Destructable;

public class ModelBaseImpl<M> implements Destructable, Model<M>
{
    protected boolean isSilent;
    protected List<ModelChangeListener<M>> modelChangeListeners = new ArrayList<ModelChangeListener<M>>();

    // event handling
    // --------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    protected M getChangeModel()
    {
        return (M) this;
    }

    //Destructable implementation
    //--------------------------------------------------------------------
    public void destroy()
    {
        modelChangeListeners.clear();
        modelChangeListeners = null;
    }

    // event handling
    // --------------------------------------------------------------------
    public void addModelChangeListener(ModelChangeListener<M> aListener)
    {
        if (aListener != null && !modelChangeListeners.contains(aListener))
        {
            modelChangeListeners.add(aListener);
        }
    }

    public void removeModelChangeListener(ModelChangeListener<M> aListener)
    {
        if (aListener != null)
        {
            modelChangeListeners.remove(aListener);
        }
    }

    public void fireModelChange(ModelChangeEvent.EventType aType)
    {
        if (!isSilent)
        {
            for (ModelChangeListener<M> listener : modelChangeListeners)
            {
                listener.onModelChanged(new ModelChangeEvent<M>(getChangeModel(), aType));
            }
        }
    }

    public void setSilent(boolean aIsSilent)
    {
        isSilent = aIsSilent;
    }
    
    public boolean isSilent()
    {
        return isSilent;
    }
}
