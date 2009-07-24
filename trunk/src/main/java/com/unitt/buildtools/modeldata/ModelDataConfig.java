package com.unitt.buildtools.modeldata;

import java.util.HashMap;
import java.util.Map;

public class ModelDataConfig
{
    protected String className;
    protected String packageName;
    protected Map<String, PropertyInfo> properties;
    protected boolean isSerializable = false;

    public ModelDataConfig()
    {
        properties = new HashMap<String, PropertyInfo>();
    }
    
    public ModelDataConfig(String aPackageName, String aClassName)
    {
        this();
        
        className = aClassName;
        packageName = aPackageName;
    }

    public Map<String, PropertyInfo> getProperties()
    {
        return properties;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String aClassName)
    {
        className = aClassName;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public void setPackageName(String aPackageName)
    {
        packageName = aPackageName;
    }
    
    public String getQualifiedName()
    {
        return getPackageName() + "." + getClassName();
    }
    
    public String getGeneratedPackageName()
    {
        return getPackageName() + ".bean";
    }
    
    public String getGeneratedClassName()
    {
        return getClassName() + "Bean";
    }
    
    public String getGeneratedQualifiedName()
    {
        return getGeneratedPackageName() + "." + getGeneratedClassName();
    }

    public boolean isSerializable()
    {
        return isSerializable;
    }

    public void setSerializable(boolean aIsSerializable)
    {
        isSerializable = aIsSerializable;
    }

    @Override
    public String toString()
    {
        StringBuffer out = new StringBuffer();
        out.append(getClass().getName());
        out.append(": className=");
        out.append(className);
        out.append(", packageName=");
        out.append(packageName);
        out.append(", isSerializable=");
        out.append(isSerializable);
        out.append("\nproperties:\n");
        out.append("-----------------------------------------------------------------\n");
        for (PropertyInfo property : properties.values())
        {
            out.append(property);
            out.append("\n");
        }
        out.append("-----------------------------------------------------------------\n");
        return out.toString();
    }
}
