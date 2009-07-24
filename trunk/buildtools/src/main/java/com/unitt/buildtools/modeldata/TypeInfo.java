package com.unitt.buildtools.modeldata;

public enum TypeInfo
{
    BOOLEAN("boolean", "Boolean"),
    BYTE("byte", "Byte"),
    SHORT("short", "Short"),
    INTEGER("int", "Integer"),
    LONG("long", "Long"),
    DOUBLE("double", "Double"),
    FLOAT("float", "Float"),
    CHAR("char", "Char");
    
    protected String primitive;
    protected String complex;
    
    private TypeInfo(String aPrimitive, String aComplex)
    {
        primitive = aPrimitive;
        complex = aComplex;
    }
    
    public String getPrimitive()
    {
        return primitive;
    }
    
    public String getComplex()
    {
        return complex;
    }
}
