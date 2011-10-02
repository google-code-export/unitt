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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.ext.DeclHandler;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.Modifier;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.type.ClassType;
import com.sun.mirror.type.InterfaceType;
import com.sun.mirror.type.ReferenceType;
import com.unitt.servicemanager.service.RemoteService;

@SuppressWarnings("restriction")
public class ServiceProxyProcessor implements AnnotationProcessor
{
    protected AnnotationProcessorEnvironment environment;
    protected AnnotationTypeDeclaration appliedDeclaration;
    protected ServiceProxyGeneratorForIOS proxyGenerator;
    protected DtoGeneratorForIOS dtoGenerator;
    protected Map<String, String> types = new HashMap<String, String>();
    protected Map<String, ClassConfig> configs = new HashMap<String, ClassConfig>();

    public ServiceProxyProcessor(AnnotationProcessorEnvironment aEnvironment)
    {
        environment = aEnvironment;
        appliedDeclaration = (AnnotationTypeDeclaration) environment.getTypeDeclaration(RemoteService.class.getName());
        proxyGenerator = new ServiceProxyGeneratorForIOS();
        dtoGenerator = new DtoGeneratorForIOS();

        // build primitives
        types.put(TypeInfo.BOOLEAN.getPrimitive(), TypeInfo.BOOLEAN.getComplex());
        types.put(TypeInfo.BYTE.getPrimitive(), TypeInfo.BYTE.getComplex());
        types.put(TypeInfo.SHORT.getPrimitive(), TypeInfo.SHORT.getComplex());
        types.put(TypeInfo.INTEGER.getPrimitive(), TypeInfo.INTEGER.getComplex());
        types.put(TypeInfo.LONG.getPrimitive(), TypeInfo.LONG.getComplex());
        types.put(TypeInfo.DOUBLE.getPrimitive(), TypeInfo.DOUBLE.getComplex());
        types.put(TypeInfo.FLOAT.getPrimitive(), TypeInfo.FLOAT.getComplex());
        types.put(TypeInfo.CHAR.getPrimitive(), TypeInfo.CHAR.getComplex());
    }

    public void process()
    {
        // Get all declarations that use the ModelDataBean annotation.
        Collection<Declaration> declarations = environment.getDeclarationsAnnotatedWith(appliedDeclaration);
        for (Declaration declaration : declarations)
        {
            processProxy(declaration);
        }
    }

    protected void processProxy(Declaration aDeclaration)
    {
        if (aDeclaration != null && aDeclaration instanceof ClassDeclaration)
        {
            ClassDeclaration declaration = (ClassDeclaration) aDeclaration;
            ServiceProxyConfig config = new ServiceProxyConfig(declaration.getPackage().getQualifiedName(), declaration.getSimpleName());
            process(declaration, config);
            handleOutput(config, proxyGenerator);
        }
    }

    protected void processDto(Declaration aDeclaration)
    {
        if (aDeclaration != null && aDeclaration instanceof ClassDeclaration)
        {
            ClassDeclaration declaration = (ClassDeclaration) aDeclaration;
            if (isDto(declaration))
            {
	            DtoConfig config = new DtoConfig(declaration.getPackage().getQualifiedName(), declaration.getSimpleName());
	            handleSerializable(declaration, config);
	            process(declaration, config);
	            handleOutput(config, dtoGenerator);
            }
        }
    }
    
    protected boolean isDto(ClassDeclaration aDeclaration)
    {
        if (aDeclaration.getQualifiedName().startsWith( "java.lang" ))
        {
            return false;
        }

        return true;
    }

    protected String getClassName(TypeDeclaration aDeclaration)
    {
        return aDeclaration.getPackage().getQualifiedName() + "." + aDeclaration.getSimpleName();
    }

    protected String getClassName(InterfaceType aDeclaration)
    {
        return aDeclaration.getDeclaration().getPackage().getQualifiedName() + "." + aDeclaration.getDeclaration().getSimpleName();
    }

    protected String getClassName(ClassType aDeclaration)
    {
        return aDeclaration.getDeclaration().getPackage().getQualifiedName() + "." + aDeclaration.getDeclaration().getSimpleName();
    }

    protected void handleSerializable(ClassDeclaration aDeclaration, DtoConfig aConfig)
    {
        if ("java.lang.Object".equals(getClassName(aDeclaration)))
        {
            return;
        }

        // look for java.io.Serializable in local interfaces
        for (TypeDeclaration type : aDeclaration.getNestedTypes())
        {
            // check for direct implementation
            if ("java.io.Serializable".equals(getClassName(type)))
            {
                aConfig.setSerializable(true);
                return;
            }

            // look for java.io.Serializable in super interfaces
            for (InterfaceType superType : type.getSuperinterfaces())
            {
                if ("java.io.Serializable".equals(getClassName(superType)))
                {
                    aConfig.setSerializable(true);
                    return;
                }
            }
        }

        // look for java.io.Serializable in super interfaces
        for (InterfaceType type : aDeclaration.getSuperinterfaces())
        {
            if ("java.io.Serializable".equals(getClassName(type)))
            {
                aConfig.setSerializable(true);
                return;
            }
        }

        // look for java.io.Serializable in super interfaces
        handleSerializable(aDeclaration.getSuperclass().getDeclaration(), aConfig);
    }

    protected boolean hasSpecifiedOutput()
    {
        return environment.getOptions().containsKey("-s") || environment.getOptions().containsKey("-Aoutput");
    }

    protected <T extends ClassConfig> void handleOutput(T aConfig, CodeGenerator<T> aGenerator)
    {
        if (!aConfig.isCompleted())
        {
            if (hasSpecifiedOutput())
            {
                saveOutputToFile(aConfig, aGenerator);
            }
            else
            {
                writeOutputToConsole(aConfig, aGenerator);
            }
            aConfig.setCompleted( true );
        }
    }

    protected File getPackageDirectory(String aPackage)
    {
        String sourceDir = environment.getOptions().get("-Aoutput");
        if (sourceDir == null)
        {
            sourceDir = environment.getOptions().get("-s");
        }
        StringBuffer filename = new StringBuffer(sourceDir);
        String[] packages = aPackage.split("\\.");
        for (String packagePart : packages)
        {
            filename.append(File.separator);
            filename.append(packagePart);
        }
        return new File(filename.toString());
    }

    protected <T extends ClassConfig>  void saveOutputToFile(T aConfig, CodeGenerator<T> aGenerator)
    {
        File packageDir = getPackageDirectory(aConfig.getGeneratedPackageName());

        if (!packageDir.exists())
        {
            packageDir.mkdirs();
        }
        
        List<CodeOutput> outputs = aGenerator.generateCode( aConfig );
        for (CodeOutput output : outputs)
        {
            File classFile = new File(packageDir, output.getName());
            System.out.println("Creating file: " + classFile.getAbsolutePath());
    
            FileWriter writer = null;
            try
            {
                writer = new FileWriter(classFile);
    
                writer.write(output.getContents().toString());
            }
            catch (IOException e)
            {
                System.err.println("Error creating file " + classFile);
            }
            finally
            {
                if (writer != null)
                {
                    try
                    {
                        writer.close();
                    }
                    catch (IOException e)
                    {
                        // ignore
                    }
                }
            }
        }
    }

    protected <T extends ClassConfig> void writeOutputToConsole(T aConfig, CodeGenerator<T> aGenerator)
    {
        List<CodeOutput> outputs = aGenerator.generateCode( aConfig );
        for (CodeOutput output : outputs)
        {
            System.out.println("Contents of \"" + output.getName() + "\":");
            System.out.println(output.getContents());
        }
    }
    
    protected boolean matchesNamePattern(String aMethodName, String aPrefix)
    {
        if (aMethodName != null && aMethodName.length() > aPrefix.length())
        {
            if (aMethodName.startsWith( aPrefix ))
            {
                return Character.isUpperCase(aMethodName.charAt( aPrefix.length() ));
            }
        }
        
        return false;
    }


    protected void process(ClassDeclaration aDeclaration, ServiceProxyConfig aConfig)
    {
        // no point in handling java.lang.Object
        if (aDeclaration.getQualifiedName().startsWith("java.lang"))
        {
            return;
        }

        // determine operations
        for (MethodDeclaration methodDeclaration : aDeclaration.getMethods())
        {
            if (methodDeclaration.getModifiers().contains( Modifier.PUBLIC ) && !methodDeclaration.getModifiers().contains(Modifier.STATIC))
            {
                addOperation(methodDeclaration, aConfig);
            }
        }

        // process super classes
        ClassType superDeclaration = aDeclaration.getSuperclass();
        if (superDeclaration != null)
        {
            process(superDeclaration.getDeclaration(), aConfig);
        }
    }

    protected void process(ClassDeclaration aDeclaration, DtoConfig aConfig)
    {
        // no point in handling java.lang.Object
        if (aDeclaration.getQualifiedName().startsWith( "java.lang" ))
        {
            return;
        }

        // determine getters & setters
        for (MethodDeclaration methodDeclaration : aDeclaration.getMethods())
        {
            if (methodDeclaration.getModifiers().contains( Modifier.PUBLIC ) && !methodDeclaration.getModifiers().contains(Modifier.STATIC))
            {
                if (methodDeclaration.getParameters().isEmpty())
                {
                    if (matchesNamePattern( methodDeclaration.getSimpleName(), "get" ))
                    {
                        addGetter(methodDeclaration, aConfig);
                    }
                    else if (matchesNamePattern( methodDeclaration.getSimpleName(), "is" ))
                    {
                        addGetter(methodDeclaration, aConfig);
                    }
                    else if (matchesNamePattern( methodDeclaration.getSimpleName(), "has" ))
                    {
                        addGetter(methodDeclaration, aConfig);
                    }
                }
                else if (methodDeclaration.getParameters().size() == 1 && matchesNamePattern( methodDeclaration.getSimpleName(), "set" ))
                {
                    addSetter(methodDeclaration, aConfig);
                }
            }
        }

        // process super classes
        ClassType superDeclaration = aDeclaration.getSuperclass();
        if (superDeclaration != null)
        {
            process(superDeclaration.getDeclaration(), aConfig);
        }
    }

    protected void addGetter(MethodDeclaration aDeclaration, DtoConfig aConfig)
    {
        PropertyInfo info = getPropertyInfo(aDeclaration, aConfig);
        if (info.getGetterType() == null)
        {
            info.setPrimitive(isPrimitive(aDeclaration.getReturnType().toString()));
            info.setGetterType(getType(aDeclaration.getReturnType().toString()));
            if (aDeclaration.getSimpleName().startsWith("has"))
            {
                info.setGetterPrefix("has");
            }
            else if (aDeclaration.getSimpleName().startsWith("is"))
            {
                info.setGetterPrefix("is");
            }
        }
    }

    protected void addOperation(MethodDeclaration aDeclaration, ServiceProxyConfig aConfig)
    {
        OperationInfo info = new OperationInfo();
        info.setName( aDeclaration.getSimpleName() );
        info.setReturnType( aDeclaration.getReturnType().toString() );
        info.setHasPartialResults( false );
        info.setParameters( getParameters(aDeclaration.getParameters()) );
        info.setCaughtExceptions( getExceptions(aDeclaration.getThrownTypes()) );
        info.setReturnTypePrimitive( isPrimitive( info.getReturnType() ) );
        aConfig.getOperations().put(info.getName(), info);
    }
    
    protected List<ExceptionInfo> getExceptions(Collection<ReferenceType> aTypes)
    {
        List<ExceptionInfo> results = new ArrayList<ExceptionInfo>();
        for (ReferenceType type : aTypes)
        {
            ExceptionInfo info = new ExceptionInfo();
            info.setType( type.toString() );
            results.add(info);
        }
        return results;
    }
    
    protected List<ParameterInfo> getParameters(Collection<ParameterDeclaration> aTypes)
    {
        List<ParameterInfo> results = new ArrayList<ParameterInfo>();
        for (ParameterDeclaration type : aTypes)
        {
            ParameterInfo info = new ParameterInfo();
            info.setType( type.getType().toString() );
            info.setName( type.getSimpleName() );
            info.setPrimitive( isPrimitive( info.getType() ) );
            results.add(info);
            processDto(environment.getTypeDeclaration( type.getType().toString() ));
        }
        return results;
    }

    protected boolean isPrimitive(String aType)
    {
        return types.containsKey(aType);
    }

    protected String getType(String aOriginal)
    {
        String type = types.get(aOriginal);
        if (type == null)
        {
            type = aOriginal;
        }
        return type;
    }

    protected void addSetter(MethodDeclaration aDeclaration, DtoConfig aConfig)
    {
        PropertyInfo info = getPropertyInfo(aDeclaration, aConfig);
        if (aDeclaration.getParameters().size() == 1)
        {
            ParameterDeclaration parameter = aDeclaration.getParameters().iterator().next();
            SetterInfo setter = new SetterInfo(getType(parameter.getType().toString()));
            try
            {
                info.getSetters().add(setter);
            }
            catch ( Exception e )
            {
                System.err.println("Could not add setter: " + aDeclaration.getSimpleName() + "(" + setter.getType() + ")");
                e.printStackTrace();
            }
        }
    }

    protected PropertyInfo getPropertyInfo(MethodDeclaration aDeclaration, DtoConfig aConfig)
    {
        // get info
        String name = aDeclaration.getSimpleName();
        if (name.startsWith("get"))
        {
            name = name.substring(3);
        }
        else if (name.startsWith("set"))
        {
            name = name.substring(3);
        }
        else if (name.startsWith("is"))
        {
            name = name.substring(2);
        }
        else if (name.startsWith("has"))
        {
            name = name.substring(3);
        }

        // create property
        PropertyInfo info = aConfig.getProperties().get(name);

        // create if missing
        if (info == null)
        {
            info = new PropertyInfo();
            info.setName(name);
            aConfig.getProperties().put(name, info);
        }

        return info;
    }
}
