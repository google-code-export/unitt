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

/**
 * Utility interface to enable classes to easily refer to the reserved, common
 * permissions for a permissable. Bit positions 1-12 are reserved. All others
 * are available for custom permissions. This allows 52 different custom
 * permissions for every object.
 * 
 * @author Josh Morris
 */
public interface ReservedPermission
{
    // standard permissions
    /**
     * Allows the assignable to read the contents of the permissable.
     */
    public static final long PERMISSION_READ     = 1;

    /**
     * Allows the assignable to write to the contents of the permissable.
     */
    public static final long PERMISSION_WRITE    = 1 << 1;

    /**
     * Allows the assignable to delete the permissable.
     */
    public static final long PERMISSION_DELETE   = 1 << 2;

    /**
     * Allows the assignable to manipulate existing permissions for the
     * permissable.
     */
    public static final long PERMISSION_CHPERMS  = 1 << 3;

    /**
     * Allows the assignable to assign the owner of the permissable.
     */
    public static final long PERMISSION_CHOWN    = 1 << 4;

    /**
     * Designates the assignable as the owner of the permissable. The owner has
     * FULL permissions on the permissable.
     */
    public static final long PERMISSION_OWNER    = 1 << 5;

    // hierarchy permissions only
    /**
     * Allows the assignable to traverse the hierarchy to access deeper
     * locations in it. Using the traversal typically requires an absolute
     * position in the hierarchy. For example, in a sample system the assignable
     * does not have any access to the / folder, but does have read access to
     * /mine. They would be unable to access /mine. However, if the assignable
     * was provided traverse permissions on the / folder, they could access
     * /mine. Traversal, does not grant any permission other than being able to
     * navigate deeper in the hierarchy to a specific end point. It will not
     * list the children in the hierarchy of the permissable, it will simply
     * allow the assignable to use the permissable's position in the hierarchy
     * to navigate deeper.
     */
    public static final long PERMISSION_TRAVERSE = 1 << 6;

    /**
     * Allows the assignable to create children in the hierarchy for the
     * specific permissable.
     */
    public static final long PERMISSION_CREATE   = 1 << 7;

    /**
     * Allows the assignable to list children in the hierarchy of the specific
     * permissable.
     */
    public static final long PERMISSION_LIST     = ( ( 1 << 8 ) | ( 1 << 6 ) );


    /**
     * Aggregate permission that represents all reserved permissions.
     */
    public static final long PERMISSION_ALL      = PERMISSION_READ | PERMISSION_WRITE | PERMISSION_DELETE | PERMISSION_CHPERMS | PERMISSION_CHOWN | PERMISSION_TRAVERSE | PERMISSION_CREATE | PERMISSION_LIST;
}
