package com.unitt.buildtools.modeldata;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SourceFileUtility
{
    public static List<String> findSourceFiles(File aDirectory)
    {
        List<String> sourceFiles = new ArrayList<String>();
        
        //discover source files
        for (File file : aDirectory.listFiles())
        {
            //descend into all children
            if (file.isDirectory())
            {
                sourceFiles.addAll(findSourceFiles(file));
            }
            
            //add java source files
            if (file.isFile() && file.getName().endsWith(".java"))
            {
                sourceFiles.add(file.getAbsolutePath());
            }
        }
        
        return sourceFiles;
    }
}
