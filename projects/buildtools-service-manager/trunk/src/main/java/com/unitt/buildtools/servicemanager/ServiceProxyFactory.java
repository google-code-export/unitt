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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.unitt.servicemanager.service.RemoteService;

@SuppressWarnings("restriction")
public class ServiceProxyFactory implements AnnotationProcessorFactory
{
    protected String sourceDir;
    
    public ServiceProxyFactory(String aSourceDir)
    {
        sourceDir = aSourceDir;
    }
    
    public ServiceProxyFactory()
    {
        //default
    }
    
    public AnnotationProcessor getProcessorFor(Set<AnnotationTypeDeclaration> aDeclarations, AnnotationProcessorEnvironment aEnvironment)
    {
        AnnotationProcessor result;
        if (aDeclarations.isEmpty())
        {
            result = AnnotationProcessors.NO_OP;
        }
        else
        {
            result = new ServiceProxyProcessor(aEnvironment);
        }
        return result;
    }

    public Collection<String> supportedAnnotationTypes()
    {
        Collection<String> supported = new ArrayList<String>();
        supported.add(RemoteService.class.getName());
        return supported;
    }

    public Collection<String> supportedOptions()
    {
        List<String> options = new ArrayList<String>();
        
        options.add("-s");
        options.add("-Aoutput");
        
        return options;
    }
}
