package com.unitt.buildtools.modeldata.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.tools.apt.Main;
import com.unitt.buildtools.modeldata.ModelDataBeanFactory;
import com.unitt.buildtools.modeldata.SourceFileUtility;

/**
 * Goal which generates a bean implementation of ModelData
 * 
 * @goal modeldata
 * 
 * @phase generate-sources
 * @requiresProject true
 * @requiresDependencyResolution
 */
public class ModelDataMojo extends AbstractMojo
{
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
        AnnotationProcessorFactory factory = new ModelDataBeanFactory();
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
                out.append(":");
            }
            out.append(item);
            isFirst = false;
        }
        return out.toString();
    }
}
