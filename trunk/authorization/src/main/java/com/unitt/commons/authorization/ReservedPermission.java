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
package com.unitt.commons.authorization;

public interface ReservedPermission
{
    // standard permissions
    public static final long PERMISSION_READ     = 1;
    public static final long PERMISSION_WRITE    = 1 << 1;
    public static final long PERMISSION_DELETE   = 1 << 2;
    public static final long PERMISSION_CHPERMS  = 1 << 3;
    public static final long PERMISSION_CHOWN    = 1 << 4;
    public static final long PERMISSION_OWNER    = 1 << 5;

    // hierarchy permissions only
    public static final long PERMISSION_TRAVERSE = 1 << 6;
    public static final long PERMISSION_CREATE   = 1 << 7;
    public static final long PERMISSION_LIST     = ( ( 1 << 8 ) | ( 1 << 6 ) );
    public static final long PERMISSION_ALL      = PERMISSION_READ | PERMISSION_WRITE | PERMISSION_DELETE | PERMISSION_CHPERMS | PERMISSION_CHOWN | PERMISSION_TRAVERSE | PERMISSION_CREATE | PERMISSION_LIST;
}
