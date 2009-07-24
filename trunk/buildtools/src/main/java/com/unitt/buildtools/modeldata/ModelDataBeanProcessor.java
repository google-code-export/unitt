package com.unitt.buildtools.modeldata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.type.ClassType;
import com.sun.mirror.type.InterfaceType;
import com.unitt.modeldata.ModelDataBean;

public class ModelDataBeanProcessor implements AnnotationProcessor
{
    protected AnnotationProcessorEnvironment environment;
    protected AnnotationTypeDeclaration appliedDeclaration;
    protected BeanGenerator generator;
    protected Map<String, String> types = new HashMap<String, String>();

    public ModelDataBeanProcessor(AnnotationProcessorEnvironment aEnvironment)
    {
        environment = aEnvironment;
        appliedDeclaration = (AnnotationTypeDeclaration) environment.getTypeDeclaration(ModelDataBean.class.getName());
        generator = new BeanGenerator();

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
            process(declaration);
        }
    }

    protected void process(Declaration aDeclaration)
    {
        if (aDeclaration != null && aDeclaration instanceof ClassDeclaration)
        {
            ClassDeclaration declaration = (ClassDeclaration) aDeclaration;
            ModelDataConfig config = new ModelDataConfig(declaration.getPackage().getQualifiedName(), declaration.getSimpleName());
            handleSerializable(declaration, config);
            process(declaration, config);
            handleOutput(config);
        }
    }
    
    protected void handleSerializable(ClassDeclaration aDeclaration, ModelDataConfig aConfig)
    {
        //look for java.io.Serializable in local interfaces
        for (TypeDeclaration type : aDeclaration.getNestedTypes())
        {
            if ("java.io.Serializable".equals(type.getPackage().getQualifiedName() + "." + type.getSimpleName()))
            {
                aConfig.setSerializable(true);
                return;
            }
        }
        
        //look for java.io.Serializable in super interfaces
        for (InterfaceType type : aDeclaration.getSuperinterfaces())
        {
            if ("java.io.Serializable".equals(type.getDeclaration().getPackage().getQualifiedName() + "." + type.getDeclaration().getSimpleName()))
            {
                aConfig.setSerializable(true);
                return;
            }
        }
    }

    protected boolean hasSpecifiedOutput()
    {
        return environment.getOptions().containsKey("-s") || environment.getOptions().containsKey("-Aoutput");
    }

    protected void handleOutput(ModelDataConfig aConfig)
    {
        if (hasSpecifiedOutput())
        {
            saveOutputToFile(aConfig);
        }
        else
        {
            writeOutputToConsole(aConfig);
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

    protected void saveOutputToFile(ModelDataConfig aConfig)
    {
        File packageDir = getPackageDirectory(aConfig.getGeneratedPackageName());

        if (!packageDir.exists())
        {
            packageDir.mkdirs();
        }

        File classFile = new File(packageDir, aConfig.getGeneratedClassName() + ".java");
        System.out.println("Creating file: " + classFile.getAbsolutePath());

        FileWriter writer = null;
        try
        {
            writer = new FileWriter(classFile);

            writer.write(generator.process(aConfig).toString());
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

    protected void writeOutputToConsole(ModelDataConfig aConfig)
    {
        System.out.println(generator.process(aConfig));
    }

    protected void process(ClassDeclaration aDeclaration, ModelDataConfig aConfig)
    {
        // no point in handling java.lang.Object
        if (aDeclaration.getQualifiedName().equals("java.lang.Object"))
        {
            return;
        }

        // determine getters & setters
        for (MethodDeclaration methodDeclaration : aDeclaration.getMethods())
        {
            if (methodDeclaration.getSimpleName().startsWith("get"))
            {
                addGetter(methodDeclaration, aConfig);
            }
            else if (methodDeclaration.getSimpleName().startsWith("is"))
            {
                addGetter(methodDeclaration, aConfig);
            }
            else if (methodDeclaration.getSimpleName().startsWith("has"))
            {
                addGetter(methodDeclaration, aConfig);
            }
            else if (methodDeclaration.getSimpleName().startsWith("set"))
            {
                addSetter(methodDeclaration, aConfig);
            }
        }

        // process super classes
        ClassType superDeclaration = aDeclaration.getSuperclass();
        if (superDeclaration != null)
        {
            process(superDeclaration.getDeclaration(), aConfig);
        }
    }

    protected void addGetter(MethodDeclaration aDeclaration, ModelDataConfig aConfig)
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

    protected void addSetter(MethodDeclaration aDeclaration, ModelDataConfig aConfig)
    {
        PropertyInfo info = getPropertyInfo(aDeclaration, aConfig);
        if (aDeclaration.getParameters().size() == 1)
        {
            ParameterDeclaration parameter = aDeclaration.getParameters().iterator().next();
            info.getSetters().add(getType(parameter.getType().toString()));
        }
    }

    protected PropertyInfo getPropertyInfo(MethodDeclaration aDeclaration, ModelDataConfig aConfig)
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
