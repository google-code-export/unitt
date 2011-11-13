package com.unitt.servicemanager.service;


import java.util.ArrayList;
import java.util.List;


public class MockService
{
    public List<String> getNumberOfValues( int aCount, String aValue )
    {
        List<String> results = new ArrayList<String>();

        for ( int i = 0; i < aCount; i++ )
        {
            results.add( aValue );
        }

        return results;
    }
}
