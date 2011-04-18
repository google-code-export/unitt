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

import com.unitt.commons.foundation.client.model.event.ModelChangeEvent;
import com.unitt.commons.foundation.client.model.event.ModelChangeListener;
import com.unitt.commons.foundation.client.model.event.ModelItemChangeEvent;

public class ModelItemsImpl<M, I> extends ModelBaseImpl<M>
{
    public void fireModelChange(I aItem, ModelChangeEvent.EventType aType )
    {
        if ( !isSilent )
        {
            for ( ModelChangeListener<M> listener : modelChangeListeners )
            {
                listener.onModelChanged( new ModelItemChangeEvent<M,I>( getChangeModel(), aItem, aType ) );
            }
        }
    }
}
