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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unitt.commons.foundation.client.model.ManagedModel;

@SuppressWarnings("unchecked")
public class HasManagedModelsPayback implements Serializable
{
    private static final long serialVersionUID = 1481859075985534522L;
    
    protected Map<ManagedModelKey, List<ManagedModel>> listModelContents;
    protected Map<ManagedModelKey, Map<ManagedModel,ManagedModel>> mapModelContents;
    
    public HasManagedModelsPayback()
    {
        listModelContents = new HashMap<ManagedModelKey, List<ManagedModel>>();
        mapModelContents = new HashMap<ManagedModelKey, Map<ManagedModel,ManagedModel>>();
    }
    
    public Map<ManagedModelKey, List<ManagedModel>> getListModelContents()
    {
        return listModelContents;
    }
    
    public Map<ManagedModelKey, Map<ManagedModel,ManagedModel>> getMapModelContents()
    {
        return mapModelContents;
    }
}
