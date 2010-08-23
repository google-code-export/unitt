/*
 * Copyright 2009 UnitT Software Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitt.commons.foundation.util;

import java.util.Date;

public class StringConverter
{
    public static String convertFrom(Boolean aValue)
    {
        if (aValue == null)
        {
            return null;
        }
        return aValue.toString();
    }

    public static String convertFrom(Integer aValue)
    {
        if (aValue == null)
        {
            return null;
        }
        return aValue.toString();
    }

    public static String convertFrom(Long aValue)
    {
        if (aValue == null)
        {
            return null;
        }
        return aValue.toString();
    }

    public static String convertFrom(Double aValue)
    {
        if (aValue == null)
        {
            return null;
        }
        return aValue.toString();
    }

    public static String convertFrom(Date aValue)
    {
        if (aValue == null)
        {
            return null;
        }
        return Long.toString(aValue.getTime());
    }

    public static Boolean convertToBoolean(String aValue)
    {
        if (aValue == null)
        {
            return null;
        }
        return Boolean.valueOf(aValue);
    }

    public static Integer convertToInteger(String aValue)
    {
        if (aValue == null)
        {
            return null;
        }
        try
        {
            return Integer.parseInt(aValue);
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public static Long convertToLong(String aValue)
    {
        if (aValue == null)
        {
            return null;
        }
        try
        {
            return Long.parseLong(aValue);
        }
        catch (Exception e)
        {
            return 0L;
        }
    }

    public static Double convertToDouble(String aValue)
    {
        if (aValue == null)
        {
            return null;
        }
        try
        {
            return Double.parseDouble(aValue);
        }
        catch (Exception e)
        {
            return 0D;
        }
    }

    public static Date convertToDate(String aValue)
    {
        if (aValue == null)
        {
            return null;
        }

        return new Date(convertToLong(aValue));
    }
}
