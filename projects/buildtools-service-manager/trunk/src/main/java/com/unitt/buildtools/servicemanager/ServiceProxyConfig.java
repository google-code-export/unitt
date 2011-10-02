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

public class ServiceProxyConfig extends ClassConfig
{
    protected Map<String, OperationInfo> operations = new HashMap<String, OperationInfo>();

    public ServiceProxyConfig()
    {
    }
    
    public ServiceProxyConfig(String aPackageName, String aClassName)
    {
        super(aPackageName, aClassName);
    }

    public Map<String, OperationInfo> getOperations()
    {
        return operations;
    }
    
    public Set<String> getImports()
    {
        Set<String> results = new TreeSet<String>();
        
        for (OperationInfo operation : operations.values())
        {
            results.addAll(operation.getImports());
        }
        
        return results;
    }

    @Override
    public String toString()
    {
        StringBuffer out = new StringBuffer();
        out.append(super.toString());
        out.append("\noperations:\n");
        out.append("-----------------------------------------------------------------\n");
        for (OperationInfo operation : operations.values())
        {
            out.append(operation);
            out.append("\n");
        }
        out.append("-----------------------------------------------------------------\n");
        return out.toString();
    }
}
