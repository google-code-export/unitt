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
package com.unitt.commons.identity;


import java.util.List;

import com.unitt.commons.security.Identity;


public interface IdentityProvider
{
    public Identity getIdentity( String aSessionId, Long aId, String aType );
    public  List<Identity> getSecondaryIdentities( String aSessionId, Long aId, String aType );

    public Identity putIdentity( String aSessionId, Identity aIdentity );
    public Identity putSecondaryIdentity( String aSessionId, Long aId, Identity aIdentity );
    public boolean isPutAllowed();

    public Identity removeIdentity( String aSessionId, Identity aIdentity );
    public Identity removeSecondaryIdentity( String aSessionId, Long aId, Identity aIdentity );
    public boolean isRemoveAllowed();
}
