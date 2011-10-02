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
import java.util.List;

import com.unitt.buildtools.servicemanager.TypeHelper.Platform;

public class DtoGeneratorForIOS implements CodeGenerator<DtoConfig>
{
    public static final Platform PLATFORM = Platform.IOS;
    
    protected TypeHelper types = new TypeHelper();
    
    public List<CodeOutput> generateCode(DtoConfig aConfig)
    {
        List<CodeOutput> results = new ArrayList<CodeOutput>();
        
        //create header
        CodeOutput interfaceCode = new CodeOutput();
        interfaceCode.setName( aConfig.getGeneratedClassName() + ".h" );
        interfaceCode.setContents( createInterfaceContents( aConfig ) );
        results.add(interfaceCode);
        
        //create implementation
        CodeOutput implementationCode = new CodeOutput();
        implementationCode.setName( aConfig.getGeneratedClassName() + ".m" );
        implementationCode.setContents( createImplementationContents( aConfig ) );
        results.add(implementationCode);
        
        return results;
    }
    
    protected StringBuffer createInterfaceContents(DtoConfig aConfig)
    {
        StringBuffer out = new StringBuffer();
        
        //imports
        out.append("#import <Foundation/Foundation.h>\n");
        out.append("\n");
        for (String importType : aConfig.getDtoImports())
        {
            if (types.doesNonPrimitiveNeedImportForPlatform( importType, PLATFORM ))
            {
                out.append("#import \"" + types.getImportTypeForPlatform( importType, PLATFORM ) + ".h\"\n");
            }
        }
        out.append("\n");
        out.append("\n");
        
        //start 
        out.append("@interface " + aConfig.getGeneratedClassName() + " : NSObject\n");
        
        //interface variables
        out.append("{\n");
        out.append("\t@private\n");
        for (PropertyInfo property : aConfig.getDtoProperties().values())
        {
            out.append( "\t" + types.getTypeForPlatform( property.getGetterType(), PLATFORM ) + " " + property.getId() + ";\n" );
        }
        out.append("}\n");
        
        //interface properties
        out.append("\n");
        for (PropertyInfo property : aConfig.getDtoProperties().values())
        {
            out.append( "@property (" + getPropertyFlags(property) + ") " + types.getTypeForPlatform( property.getGetterType(), PLATFORM ) + " " + property.getId() + ";\n" );
        }
        
        //end
        out.append("\n");
        out.append("@end");      
        
        return out;
    }
    
    protected String getPropertyFlags(PropertyInfo aProperty)
    {
        String platformType = types.getTypeForPlatform( aProperty.getGetterType(), PLATFORM );
        if (aProperty.isPrimitive())
        {
            return "assign";
        }
        else if ("NSString*".equals( platformType ) || "NSDate*".equals( platformType ))
        {
            return "copy";
        }
        else
        {
            return "retain";
        }
    }
    
    protected StringBuffer createImplementationContents(DtoConfig aConfig)
    {
        StringBuffer out = new StringBuffer();
        
        //imports
        out.append("#import \"" + aConfig.getGeneratedClassName() + ".h\"\n");
        out.append("\n");
        out.append("\n");
        
        //start
        out.append("@implementation " + aConfig.getGeneratedClassName() + "\n");
        
        //synthesize
        out.append("\n");
        for (PropertyInfo property : aConfig.getDtoProperties().values())
        {
            out.append( "@synthesize " + property.getId() + ";\n" );
        }
        out.append("\n");
        
        //dealloc
        out.append("\n");
        out.append("- (void) dealloc\n");
        out.append("{\n");
        for (PropertyInfo property : aConfig.getDtoProperties().values())
        {
            if (needsRelease(property))
            {
                out.append( "\t[" + property.getId() + " release];\n" );
            }
        }
        out.append("\n");
        out.append("\t[super dealloc];\n");
        out.append("}\n");
        
        //end
        out.append("\n");
        out.append("@end");      
        
        return out;
    }
    
    protected boolean needsRelease(PropertyInfo aProperty)
    {
        return !aProperty.isPrimitive();
    }
}
