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

import com.unitt.commons.security.Identity;

public class MockProvider implements AuthenticationProvider
{
    public boolean accepts( AuthenticationToken aToken )
    {
        if (aToken instanceof MockToken)
        {
            return ((MockToken) aToken).isAllowingAccepts();
        }
        
        return false;
    }

    public Identity authenticate( AuthenticationToken aToken ) throws BadCredentialsException
    {
        MockToken token = (MockToken) aToken;
        if (token.isAllowingAuthentication())
        {
            return token.getIdentity();
        }
        
        throw new BadCredentialsException( token );
    }
}
