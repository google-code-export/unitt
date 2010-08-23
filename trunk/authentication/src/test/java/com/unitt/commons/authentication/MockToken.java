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
package com.unitt.commons.authentication;

public class MockToken implements AuthenticationToken, Credential
{
    protected boolean allowingAccepts;
    protected boolean allowingAuthentication;
    protected MockIdentity identity;


    // constructors
    // ---------------------------------------------------------------------------
    public MockToken()
    {
        // default
    }

    public MockToken( boolean aAccepts, boolean aAuthenticate )
    {
        allowingAccepts = aAccepts;
        allowingAuthentication = aAuthenticate;
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public boolean isAllowingAccepts()
    {
        return allowingAccepts;
    }

    public void setAllowingAccepts( boolean aAccepts )
    {
        allowingAccepts = aAccepts;
    }

    public boolean isAllowingAuthentication()
    {
        return allowingAuthentication;
    }

    public void setAllowingAuthentication( boolean aAuthenticate )
    {
        allowingAuthentication = aAuthenticate;
    }

    public MockIdentity getIdentity()
    {
        return identity;
    }

    public void setIdentity( MockIdentity aIdentity )
    {
        identity = aIdentity;
    }
}
