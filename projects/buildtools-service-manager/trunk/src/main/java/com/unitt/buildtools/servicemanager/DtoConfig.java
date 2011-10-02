/*
 *  Licensed to UnitT Software, Inc. under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.unitt.buildtools.servicemanager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DtoConfig extends ClassConfig
{
    protected Map<String, PropertyInfo> properties = new HashMap<String, PropertyInfo>();
    protected Map<String, PropertyInfo> dtoProperties;
    
    protected TypeHelper helper = new TypeHelper();

    public DtoConfig()
    {
    }
    
    public DtoConfig(String aPackageName, String aClassName)
    {
        super(aPackageName, aClassName);
    }

    public Map<String, PropertyInfo> getProperties()
    {
        return properties;
    }

    public Map<String, PropertyInfo> getDtoProperties()
    {
        if (dtoProperties == null)
        {
            dtoProperties = new HashMap<String, PropertyInfo>();
            for (PropertyInfo property : properties.values())
            {
                if (property.hasGetter())
                {
                    if (property.isValidDto())
                    {
                        dtoProperties.put( property.getName(), property );
                    }
                }
            }
        }
        
        return dtoProperties;
    }
    
    public Set<String> getImports()
    {
        Set<String> results = new TreeSet<String>();
        
        for (PropertyInfo property : properties.values())
        {
            if (property.hasGetter())
            {
                results.add(property.getGetterType());
            }
        }
        
        return results;
    }
    
    public Set<String> getDtoImports()
    {
        Set<String> results = new TreeSet<String>();
        
        for (PropertyInfo property : getDtoProperties().values())
        {
            if (property.hasGetter())
            {
                if (!property.isPrimitive())
                {
                    results.add(property.getGetterType());
                }
            }
        }
        
        return results;
    }

    @Override
    public String toString()
    {
        StringBuffer out = new StringBuffer();
        out.append(super.toString());
        out.append("\nproperties:\n");
        out.append("-----------------------------------------------------------------\n");
        for (PropertyInfo property : properties.values())
        {
            out.append(property);
            out.append("\n");
        }
        out.append("-----------------------------------------------------------------\n");
        out.append("\ndtoProperties:\n");
        out.append("-----------------------------------------------------------------\n");
        for (PropertyInfo property : dtoProperties.values())
        {
            out.append(property);
            out.append("\n");
        }
        out.append("-----------------------------------------------------------------\n");
        return out.toString();
    }
}
