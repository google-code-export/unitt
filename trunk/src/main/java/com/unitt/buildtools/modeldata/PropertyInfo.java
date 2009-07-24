package com.unitt.buildtools.modeldata;

import java.util.Set;
import java.util.TreeSet;

public class PropertyInfo
{
    protected String name;
    protected String id;
    protected boolean isPrimitive;
    protected String getterType;
    protected String getterPrefix = "get";
    protected Set<String> setters = new TreeSet<String>();
    
    public boolean isPrimitive()
    {
        return isPrimitive;
    }

    public void setPrimitive(boolean aIsPrimitive)
    {
        isPrimitive = aIsPrimitive;
    }

    public Set<String> getSetters()
    {
        return setters;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String aName)
    {
        name = aName;
        id = aName.substring(0, 1).toLowerCase() + aName.substring(1);
    }

    public String getId()
    {
        return id;
    }

    public String getGetterType()
    {
        return getterType;
    }

    public void setGetterType(String aGetterType)
    {
        getterType = aGetterType;
    }

    public boolean hasGetter()
    {
        return getGetterType() != null && getGetterPrefix() != null;
    }
    
    public boolean hasSetter()
    {
        return !getSetters().isEmpty();
    }

    public String getGetterPrefix()
    {
        return getterPrefix;
    }

    public void setGetterPrefix(String aGetterPrefix)
    {
        getterPrefix = aGetterPrefix;
    }

    @Override
    public String toString()
    {
        return getClass().getName() + ": name=" + name + ", hasGetter=" + hasGetter() + ", getterType=" + getterType + ", setters=" + setters;
    }
}
