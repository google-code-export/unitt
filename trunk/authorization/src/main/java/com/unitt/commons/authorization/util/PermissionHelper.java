//
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
package com.unitt.commons.authorization.util;

import com.unitt.commons.authorization.ReservedPermission;


public class PermissionHelper implements ReservedPermission
{

    // --------------------------------------------------------------
    // mask evaluation methods
    // --------------------------------------------------------------

    /**
     * Determines if the requested mask is contained in the internal permission
     * mask
     * 
     * @param aRequested
     *            containee mask
     * @param aMask
     *            container mask
     * @return boolean always true if the specified mask contains the owner mask
     */
    public static boolean allows( long aRequested, long aMask )
    {
        // first check owner
        if ( BitMasks.contains( aMask, PERMISSION_OWNER ) )
        {
            return true;
        }

        // proceed with normal mask check
        return BitMasks.contains( aMask, aRequested );
    }

    /**
     * Returns whether the mask has no values set
     * 
     * @return boolean true if mask has no values
     */
    public static boolean isMaskEmpty( long aMask )
    {
        return aMask == 0;
    }


    // --------------------------------------------------------------
    // utility methods for easy setting of permissions
    // --------------------------------------------------------------

    /**
     * Sets whether the given permission will allow read access.
     * 
     * @param aToggle
     *            true allows, false disallows
     */
    public static long setAllowRead( boolean aToggle, long aMask )
    {
        return applyMask( PERMISSION_READ, aMask, aToggle );
    }

    /**
     * Sets whether the given permission will allow create access.
     * 
     * @param aToggle
     *            true allows, false disallows
     */
    public static long setAllowCreate( boolean aToggle, long aMask )
    {
        return applyMask( PERMISSION_CREATE, aMask, aToggle );
    }

    /**
     * Sets whether the given permission will allow list access.
     * 
     * @param aToggle
     *            true allows, false disallows
     */
    public static long setAllowList( boolean aToggle, long aMask )
    {
        return applyMask( PERMISSION_LIST, aMask, aToggle );
    }

    /**
     * Sets whether the given permission will allow write access.
     * 
     * @param aToggle
     *            true allows, false disallows
     */
    public static long setAllowWrite( boolean aToggle, long aMask )
    {
        return applyMask( PERMISSION_WRITE, aMask, aToggle );
    }

    /**
     * Sets whether the given permission will allow delete access.
     * 
     * @param aToggle
     *            true allows, false disallows
     */
    public static long setAllowDelete( boolean aToggle, long aMask )
    {
        return applyMask( PERMISSION_DELETE, aMask, aToggle );
    }

    /**
     * Sets whether the given permission will allow change perms access.
     * 
     * @param aToggle
     *            true allows, false disallows
     */
    public static long setAllowChangePermissions( boolean aToggle, long aMask )
    {
        return applyMask( PERMISSION_CHPERMS, aMask, aToggle );
    }

    /**
     * Sets whether the given permission will allow change owner access.
     * 
     * @param aToggle
     *            true allows, false disallows
     */
    public static long setAllowChangeOwner( boolean aToggle, long aMask )
    {
        return applyMask( PERMISSION_CHOWN, aMask, aToggle );
    }

    /**
     * Sets whether the value for the given bit
     * 
     * @param aToggle
     *            true allows, false disallows
     */
    public static long setAllowBit( int aBit, long aMask, boolean aToggle )
    {
        if ( ( aBit < 31 ) && ( aBit > 9 ) )
        {
            return applyMask( 1 << aBit, aMask, aToggle );
        }
        
        return aMask;
    }

    /**
     * Sets the owner mask
     * 
     * @param aToggle
     *            true allows, false disallows
     */
    public static long setOwnerFlag( boolean aToggle, long aMask )
    {
        return applyMask( PERMISSION_OWNER, aMask, aToggle );
    }

    // --------------------------------------------------------------
    // masking operations
    // --------------------------------------------------------------

    /**
     * Adds/subtracts the given mask from the permission.
     * 
     * @param aMask
     *            mask to apply
     * @param aAddMask
     *            true adds, false subtracts
     */
    public static long applyMask( long aMaskToApply, long aOriginalMask, boolean aAddMask )
    {
        if ( aAddMask )
        {
            return addMask( aMaskToApply, aOriginalMask );
        }
        else
        {
            return subtractMask( aMaskToApply, aOriginalMask );
        }
    }

    /**
     * Adds given mask to permission mask
     * 
     * @param aMask
     * @see com.valeo.util.BitMasks#addMask(long, long)
     */
    public static long addMask( long aMaskToAdd, long aOriginalMask )
    {
        return BitMasks.addMask( aOriginalMask, aMaskToAdd );
    }

    /**
     * Subtracts given mask from permission mask
     * 
     * @param aMask
     * @see com.valeo.util.BitMasks#addMask(long, long)
     */
    public static long subtractMask( long aMaskToAdd, long aOriginalMask )
    {
        return BitMasks.subtractMask( aOriginalMask, aMaskToAdd );
    }
}
