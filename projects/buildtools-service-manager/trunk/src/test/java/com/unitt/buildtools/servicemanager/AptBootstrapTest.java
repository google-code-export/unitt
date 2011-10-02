package com.unitt.buildtools.servicemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.tools.apt.Main;

@SuppressWarnings( "restriction" )
public class AptBootstrapTest
{
    protected File sourceDirectory = new File("src/test/java");
    protected String classpathSeparator = null;
    
    @Test
    public void bootstrapTest()
    {
        System.out.println("Testing source in " + sourceDirectory.getAbsolutePath());
        List<String> sources = SourceFileUtility.findSourceFiles(sourceDirectory);
        System.out.println("Testing Sources: " + sources);
        List<String> args = new ArrayList<String>();
        args.add("-nocompile");
//        args.add("-classpath");
//        args.add(getClasspath());
        args.add("-s");
        args.add(sourceDirectory.getAbsolutePath());
        args.addAll(sources);
        AnnotationProcessorFactory factory = new ServiceProxyFactory();
        Main.process(factory, args.toArray(new String[] {}));
    }
//    
//    protected String getClasspath()
//    {
//        StringBuffer out = new StringBuffer();
//        boolean isFirst = true;
//        for (Object item : classpathElements)
//        {
//            if (!isFirst)
//            {
//                out.append(getClasspathSeparator());
//            }
//            out.append(item);
//            isFirst = false;
//        }
//        return out.toString();
//    }
//    
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
