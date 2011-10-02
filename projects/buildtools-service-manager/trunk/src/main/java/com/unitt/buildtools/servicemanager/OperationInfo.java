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


import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class OperationInfo
{
    protected boolean             hasPartialResults;
    protected String              name;
    protected String              returnType;
    protected List<ParameterInfo> parameters;
    protected List<ExceptionInfo> caughtExceptions;
    protected String              id;
    protected boolean             isReturnTypePrimitive;

    public Set<String> getImports()
    {
        Set<String> results = new TreeSet<String>();

        for ( ParameterInfo parameter : parameters )
        {
            if ( !parameter.isPrimitive() )
            {
                results.add( parameter.getType() );
            }
        }

        return results;
    }

    public boolean isReturnTypePrimitive()
    {
        return isReturnTypePrimitive;
    }

    public void setReturnTypePrimitive( boolean aIsReturnTypePrimitive )
    {
        isReturnTypePrimitive = aIsReturnTypePrimitive;
    }

    public boolean hasPartialResults()
    {
        return hasPartialResults;
    }

    public void setHasPartialResults( boolean aHasPartialResults )
    {
        hasPartialResults = aHasPartialResults;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String aName )
    {
        name = aName;
        id = name;
    }

    public String getReturnType()
    {
        return returnType;
    }

    public void setReturnType( String aReturnType )
    {
        returnType = aReturnType;
    }

    public List<ParameterInfo> getParameters()
    {
        return parameters;
    }

    public void setParameters( List<ParameterInfo> aParameters )
    {
        parameters = aParameters;
    }

    public List<ExceptionInfo> getCaughtExceptions()
    {
        return caughtExceptions;
    }

    public void setCaughtExceptions( List<ExceptionInfo> aCaughtExceptions )
    {
        caughtExceptions = aCaughtExceptions;
    }

    public String getId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return getClass().getName() + ": name=" + name + ", returyType=" + returnType + ", hasPartialResults=" + hasPartialResults + ", parameters=" + parameters;
    }
}
