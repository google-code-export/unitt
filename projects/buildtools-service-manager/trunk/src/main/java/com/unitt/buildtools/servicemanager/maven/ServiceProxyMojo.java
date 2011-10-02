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
package com.unitt.buildtools.servicemanager.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.tools.apt.Main;
import com.unitt.buildtools.servicemanager.ServiceProxyFactory;
import com.unitt.buildtools.servicemanager.SourceFileUtility;

/**
 * Goal which generates a bean implementation of ModelData
 * 
 * @goal modeldata
 * 
 * @phase generate-sources
 * @requiresProject true
 * @requiresDependencyResolution
 */
public class ServiceProxyMojo extends AbstractMojo
{
	protected String classpathSeparator = null;
	
    /**
     * @parameter expression="${project.runtimeClasspathElements}"
     * @required
     * @readonly
     */
    @SuppressWarnings("unchecked")
    private List classpathElements;

    /**
     * Location of source files.
     * 
     * @parameter expression="${project.build.sourceDirectory}"
     * @required
     */
    private File sourceDirectory;

    public void execute() throws MojoExecutionException
    {
        List<String> sources = SourceFileUtility.findSourceFiles(sourceDirectory);
        List<String> args = new ArrayList<String>();
        args.add("-nocompile");
        args.add("-classpath");
        args.add(getClasspath());
        args.add("-s");
        args.add(sourceDirectory.getAbsolutePath());
        args.addAll(sources);
        AnnotationProcessorFactory factory = new ServiceProxyFactory();
        Main.process(factory, args.toArray(new String[] {}));
    }
    
    protected String getClasspath()
    {
        StringBuffer out = new StringBuffer();
        boolean isFirst = true;
        for (Object item : classpathElements)
        {
            if (!isFirst)
            {
                out.append(getClasspathSeparator());
            }
            out.append(item);
            isFirst = false;
        }
        return out.toString();
    }
    
    protected String getClasspathSeparator()
    {
    	if (classpathSeparator == null)
    	{
    		if (System.getProperty("os.name").startsWith("Windows"))
    		{
    			classpathSeparator = ";";
    		}
    		else
    		{
    			classpathSeparator = ":";
    		}
    	}
    	
    	return classpathSeparator;
    }
}
