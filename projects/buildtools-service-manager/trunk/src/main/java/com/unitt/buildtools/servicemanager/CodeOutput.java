package com.unitt.buildtools.servicemanager;

public class CodeOutput
{
    protected String       name;
    protected StringBuffer contents;

    public String getName()
    {
        return name;
    }

    public void setName( String aName )
    {
        name = aName;
    }

    public StringBuffer getContents()
    {
        return contents;
    }

    public void setContents( StringBuffer aContents )
    {
        contents = aContents;
    }
}
