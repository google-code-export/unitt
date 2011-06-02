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
package com.unitt.commons.foundation.client.model.event;



/**
 * The event that is fired when a Property changes.
 * 
 * @param <T> the class type of the model
 */
public class ModelChangeEvent<T>
{
   public static enum EventType 
   {
       MODEL_ADD_ITEM_EVENT,
       MODEL_REMOVE_ITEM_EVENT,
       MODEL_CHANGED_EVENT, 
       LOADING_STARTED_EVENT, 
       LOADING_COMPLETE_EVENT, 
       SELECTION_CHANGED_EVENT, 
       UPDATE_COMPLETE_EVENT, 
       UPDATE_STARTED_EVENT 
   }
   
   protected T model   = null;
   protected EventType eventType = null;
    
   public ModelChangeEvent(T aModel, EventType aType)
   {
      eventType = aType;
      model     = aModel;
   }

   public T getModel()
   {
      return model;
   }
   
   public EventType getEventType()
   {
       return eventType;
   }
}
