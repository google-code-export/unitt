package com.unitt.commons.persist;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public class PersistUtil
{
    /**
     * Get the underlying class for a type, or null if the type is a variable
     * type.
     * 
     * @param aType
     *            the type
     * @return the underlying class
     */
    @SuppressWarnings( "unchecked" )
    public static Class<?> getClass( Type aType )
    {
        if ( aType instanceof Class )
        {
            return (Class) aType;
        }
        else if ( aType instanceof ParameterizedType )
        {
            return getClass( ( (ParameterizedType) aType ).getRawType() );
        }
        else if ( aType instanceof GenericArrayType )
        {
            Type componentType = ( (GenericArrayType) aType ).getGenericComponentType();
            Class<?> componentClass = getClass( componentType );
            if ( componentClass != null )
            {
                return Array.newInstance( componentClass, 0 ).getClass();
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }
}
