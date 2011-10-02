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

public class ServiceProxyGeneratorForIOS implements CodeGenerator<ServiceProxyConfig>
{
    public static final Platform PLATFORM = Platform.IOS;
    
    protected TypeHelper types = new TypeHelper();
    
    public List<CodeOutput> generateCode(ServiceProxyConfig aConfig)
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
    
    protected StringBuffer createInterfaceContents(ServiceProxyConfig aConfig)
    {
        StringBuffer out = new StringBuffer();
        
        //imports
        out.append("#import <Foundation/Foundation.h>\n");
        out.append("#import ServiceProxy.h\n");
        out.append("\n");
        for (String importType : aConfig.getImports())
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
        out.append("}\n");
        
        //interface methods
        out.append("\n");
        for (OperationInfo operation : aConfig.getOperations().values())
        {
            out.append( "- (" + types.getTypeForPlatform( operation.getReturnType(), PLATFORM ) + ") " );
            out.append( operation.getName() );
            
            //craft message signature based on number of args
            if (operation.getParameters().isEmpty())
            {
                out.append( ":(id<ServiceCallback>) aCallback;\n");
            }
            else
            {
                boolean isFirst = true;
                for (ParameterInfo param : operation.getParameters())
                {
                    if (isFirst)
                    {
                        out.append(":(" + types.getTypeForPlatform(param.getType(), PLATFORM) + ") " + param.getName());
                        isFirst = false;
                    }
                    else
                    {
                        out.append(" " + param.getNameWithoutPrefix() + ":(" + types.getTypeForPlatform(param.getType(), PLATFORM) + ") " + param.getName());
                    }
                }
                out.append( " callback:(id<ServiceCallback>) aCallback;\n");
            }
            out.append("\n");
        }
        
        //end
        out.append("\n");
        out.append("@end");      
        
        return out;
    }
    
    protected StringBuffer createImplementationContents(ServiceProxyConfig aConfig)
    {
        StringBuffer out = new StringBuffer();
        
        //imports
        out.append("#import \"" + aConfig.getGeneratedClassName() + ".h\"\n");
        out.append("\n");
        out.append("\n");
        
        //start
        out.append("@implementation " + aConfig.getGeneratedClassName() + "\n");
        
        //service methods
        out.append("\n");
        for (OperationInfo operation : aConfig.getOperations().values())
        {
            out.append( "- (" + types.getTypeForPlatform( operation.getReturnType(), PLATFORM ) + ") " );
            out.append( operation.getName() );
            
            //craft method signature based on number of args
            if (operation.getParameters().isEmpty())
            {
                out.append( ":(id<ServiceCallback>) aCallback;\n");
            }
            else
            {
                boolean isFirst = true;
                for (ParameterInfo param : operation.getParameters())
                {
                    if (isFirst)
                    {
                        out.append(":(" + types.getTypeForPlatform(param.getType(), PLATFORM) + ") " + param.getName());
                        isFirst = false;
                    }
                    else
                    {
                        out.append(" " + param.getNameWithoutPrefix() + ":(" + types.getTypeForPlatform(param.getType(), PLATFORM) + ") " + param.getName());
                    }
                }
                out.append( " callback:(id<ServiceCallback>) aCallback;\n");
            }
            
            //method body
            out.append("{\n");
            out.append("\t[self.client requestForService:self.serviceName\n");
            out.append("\t             methodSignature:@\"" + getSignature(operation) + "\"\n");
            out.append("\t             parameters:[NSArray arrayWithObjects:");
            boolean isFirst = true;
            for (ParameterInfo param : operation.getParameters())
            {
                if (isFirst)
                {
                    isFirst = false;
                }
                else
                {
                    out.append(", ");
                }
                out.append(param.getName());
            }
            out.append( ", nil]\n" );
            out.append("\t             callback:aCallback];\n");
            out.append("}\n");
            out.append("\n");
        }
        out.append("\n");
        
        //end
        out.append("\n");
        out.append("@end");      
        
        return out;
    }


    public String getSignature(OperationInfo aInfo)
    {
        StringBuffer output = new StringBuffer();
        output.append( aInfo.getName() );
        output.append( "#" );
        boolean isFirst = true;
        for ( ParameterInfo param : aInfo.getParameters() )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                output.append( "," );
            }
            output.append( param.getType() );
        }

        return output.toString();
    }
}
