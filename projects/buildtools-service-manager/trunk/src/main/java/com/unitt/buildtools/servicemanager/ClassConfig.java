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


public class ClassConfig
{
    protected boolean completed = false;
    protected String className;
    protected String packageName;
    protected boolean isSerializable = false;

    public ClassConfig()
    {
    }
    
    public ClassConfig(String aPackageName, String aClassName)
    {
        this();
        
        className = aClassName;
        packageName = aPackageName;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String aClassName)
    {
        className = aClassName;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public void setPackageName(String aPackageName)
    {
        packageName = aPackageName;
    }
    
    public String getQualifiedName()
    {
        return getPackageName() + "." + getClassName();
    }
    
    public String getGeneratedPackageName()
    {
        return getPackageName();
    }
    
    public String getGeneratedClassName()
    {
        return getClassName();
    }
    
    public String getGeneratedQualifiedName()
    {
        return getGeneratedPackageName() + "." + getGeneratedClassName();
    }

    public boolean isSerializable()
    {
        return isSerializable;
    }

    public void setSerializable(boolean aIsSerializable)
    {
        isSerializable = aIsSerializable;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted( boolean aCompleted )
    {
        completed = aCompleted;
    }

    @Override
    public String toString()
    {
        StringBuffer out = new StringBuffer();
        out.append(getClass().getName());
        out.append(": className=");
        out.append(className);
        out.append(", packageName=");
        out.append(packageName);
        out.append(", isSerializable=");
        out.append(isSerializable);
        return out.toString();
    }
}
