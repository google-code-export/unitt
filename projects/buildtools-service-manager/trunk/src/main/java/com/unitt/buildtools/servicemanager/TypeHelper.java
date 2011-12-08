package com.unitt.buildtools.servicemanager;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


//@todo: handle generics
public class TypeHelper
{
    protected Map<String, String> types = new HashMap<String, String>();

    public TypeHelper()
    {
        // build primitives
        types.put( TypeInfo.BOOLEAN.getPrimitive(), TypeInfo.BOOLEAN.getComplex() );
        types.put( TypeInfo.BYTE.getPrimitive(), TypeInfo.BYTE.getComplex() );
        types.put( TypeInfo.SHORT.getPrimitive(), TypeInfo.SHORT.getComplex() );
        types.put( TypeInfo.INTEGER.getPrimitive(), TypeInfo.INTEGER.getComplex() );
        types.put( TypeInfo.LONG.getPrimitive(), TypeInfo.LONG.getComplex() );
        types.put( TypeInfo.DOUBLE.getPrimitive(), TypeInfo.DOUBLE.getComplex() );
        types.put( TypeInfo.FLOAT.getPrimitive(), TypeInfo.FLOAT.getComplex() );
        types.put( TypeInfo.CHAR.getPrimitive(), TypeInfo.CHAR.getComplex() );
    }

    public enum Platform
    {
        IOS, Android, Java, Cocoa
    };

    public String getTypeForPlatform( String aType, Platform aPlatform )
    {
        switch ( aPlatform )
        {
            case IOS:
            {
                return getTypeForIOS( getPrimaryClass(aType) );
            }
        }

        return null;
    }

    public String getImportTypeForPlatform( String aType, Platform aPlatform )
    {
        switch ( aPlatform )
        {
            case IOS:
            {
                return getImportTypeForIOS( getPrimaryClass(aType) );
            }
        }

        return null;
    }

    public boolean isPrimitive( String aType )
    {
        return types.containsKey( aType );
    }

    public boolean doesNonPrimitiveNeedImportForPlatform( String aType, Platform aPlatform )
    {
        switch ( aPlatform )
        {
            case IOS:
            {
                return doesNonPrimitiveNeedImportForIOS( getPrimaryClass(aType) );
            }
        }

        return false;
    }
    
    protected String getPrimaryClass(String aType)
    {
        int index = aType.indexOf("<");
        if (index < 0)
        {
            return aType;
        }

        return aType.substring(0, index);
    }

    protected String getPrimaryListItemClass(String aType)
    {
        int startIndex = aType.indexOf("<");
        int endIndex = aType.indexOf(">");
        if (startIndex < 0 || endIndex < 0)
        {
            return aType;
        }

        return aType.substring(startIndex + 1, endIndex);
    }

    protected boolean doesNonPrimitiveNeedImportForIOS( String aType )
    {
        if ( "void".equals(aType) || "java.lang.Object".equals( aType ) )
        {
            return false;
        }
        if ( "java.lang.Boolean".equals( aType ) || "java.lang.Integer".equals( aType ) || "java.lang.Long".equals( aType ) || "java.lang.Double".equals( aType ) )
        {
            return false;
        }
        else if ( "java.lang.String".equals( aType ) )
        {
            return false;
        }
        else if ( "java.util.Date".equals( aType ) || "java.util.Calendar".equals( aType ) )
        {
            return false;
        }
        else if ( "java.lang.Exception".equals( aType ) || "java.lang.Throwable".equals( aType ) || "java.lang.RuntimeException".equals( aType ) )
        {
            return false;
        }
        else if ( isArray( aType ) )
        {
            return false;
        }
        else if ( isMap( aType ) )
        {
            return false;
        }

        return true;
    }

    protected String getTypeForIOS( String aType )
    {
        if ("java.lang.Class<?>".equals(aType))
        {
            return "Class*";
        }
        if ( "void".equals( aType ) )
        {
            return "void";
        }
        else if ( "java.lang.Object".equals( aType ) )
        {
            return "id";
        }
        else if ( "boolean".equals( aType ) )
        {
            return "BOOL";
        }
        else if ( "int".equals( aType ) )
        {
            return "int";
        }
        else if ( "long".equals( aType ) )
        {
            return "long long";
        }
        else if ( "double".equals( aType ) )
        {
            return "double";
        }
        else if ( "java.lang.boolean".equals( aType ) )
        {
            return "BOOL";
        }
        else if ( "java.lang.int".equals( aType ) )
        {
            return "int";
        }
        else if ( "java.lang.long".equals( aType ) )
        {
            return "long long";
        }
        else if ( "java.lang.double".equals( aType ) )
        {
            return "double";
        }
        else if ( "java.lang.Boolean".equals( aType ) || "java.lang.Integer".equals( aType ) || "java.lang.Long".equals( aType ) || "java.lang.Double".equals( aType ) )
        {
            return "NSNumber*";
        }
        else if ( "java.lang.String".equals( aType ) )
        {
            return "NSString*";
        }
        else if ( "java.util.Date".equals( aType ) || "java.util.Calendar".equals( aType ) )
        {
            return "NSDate*";
        }
        else if ( isArray( aType ) )
        {
            return "NSArray*";
        }
        else if ( isMap( aType ) )
        {
            return "NSDictionary*";
        }
        else if ( "java.lang.Exception".equals( aType ) || "java.lang.Throwable".equals( aType ) || "java.lang.RuntimeException".equals( aType ) )
        {
            return "NSError*";
        }
        else
        {
            return getSimpleClassName( aType ) + "*";
        }
    }

    protected String getImportTypeForIOS( String aType )
    {
        if ( "java.lang.Boolean".equals( aType ) || "java.lang.Integer".equals( aType ) || "java.lang.Long".equals( aType ) || "java.lang.Double".equals( aType ) )
        {
            return "NSNumber";
        }
        else if ( "java.lang.String".equals( aType ) )
        {
            return "NSString";
        }
        else if ( "java.util.Date".equals( aType ) || "java.util.Calendar".equals( aType ) )
        {
            return "NSDate";
        }
        else if ( isArray( aType ) )
        {
            return "NSArray";
        }
        else if ( isMap( aType ) )
        {
            return "NSDictionary";
        }
        else if ( "java.lang.Exception".equals( aType ) || "java.lang.Throwable".equals( aType ) || "java.lang.RuntimeException".equals( aType ) )
        {
            return "NSError";
        }
        else
        {
            return getSimpleClassName( aType );
        }
    }

    @SuppressWarnings( "rawtypes" )
    public boolean isArray( String aType )
    {
        String type = getPrimaryClass(aType);
        try
        {;
            Class typeClass = Class.forName( type );
            return List.class.isAssignableFrom( typeClass ) || Set.class.isAssignableFrom( typeClass ) || typeClass.isArray();
        }
        catch ( ClassNotFoundException e )
        {
            System.err.println( "Could not find class to determine if its an array: " + type );
            e.printStackTrace();
        }

        return false;
    }

    protected boolean isMap( String aType )
    {
        try
        {
            return Map.class.isAssignableFrom( Class.forName( aType ) );
        }
        catch ( ClassNotFoundException e )
        {
            System.err.println( "Could not find class to determine if its a map: " + aType );
            e.printStackTrace();
        }

        return false;
    }

    protected String getSimpleClassName( String aType )
    {
        if (aType.contains( "." ))
        {
            return aType.substring( aType.lastIndexOf( "." ) + 1 );
        }
        
        return aType;
    }
}
