package com.unitt.buildtools.modeldata;

public class SetterInfo
{
    protected String type;

    public SetterInfo(String aType)
    {
        type = aType;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String aType)
    {
        type = aType;
    }

    public String getTypeWithoutGenerics()
    {
        String type = getType();
        int index = type.indexOf("<");
        if (index > 0)
        {
            type = type.substring(0, index);
        }
        return type;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SetterInfo other = (SetterInfo) obj;
        if (type == null)
        {
            if (other.type != null)
                return false;
        }
        else if (!type.equals(other.type))
            return false;
        return true;
    }
}
