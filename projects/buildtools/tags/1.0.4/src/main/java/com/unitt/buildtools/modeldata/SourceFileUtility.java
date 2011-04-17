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
