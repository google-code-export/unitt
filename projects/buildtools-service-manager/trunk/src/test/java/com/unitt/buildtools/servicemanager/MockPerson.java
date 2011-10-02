package com.unitt.buildtools.servicemanager;

import java.io.Serializable;

public class MockPerson implements Serializable
{
    protected String firstName;
    protected String lastName;
    
    public String getFirstName()
    {
        return firstName;
    }
    public void setFirstName( String aFirstName )
    {
        firstName = aFirstName;
    }
    public String getLastName()
    {
        return lastName;
    }
    public void setLastName( String aLastName )
    {
        lastName = aLastName;
    }
}
