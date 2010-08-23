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


import java.util.List;


public interface PermissionManager
{
    public List<AssignedPermission> getPermissions(long aMask, Permissable aPermissable);
    public List<AssignedPermission> getPermissions(Permissable aPermissable);
    
    public boolean hasPermission(long aPermission, Permissable aPermissable, List<Assignable> aAssignables);
    
    public void applyPermission(long aPermission, boolean aAdd, Permissable aPermissable, List<Assignable> aAssignables);
    
    public void setPermission(long aPermission, boolean aAdd, Permissable aPermissable, List<Assignable> aAssignables);
    
    public void removeAllPermissions(Assignable aAssignable);
}