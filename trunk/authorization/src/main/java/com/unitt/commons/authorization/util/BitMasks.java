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

/**
 * Utility class for inspecting and modifying bit masks
 * 
 * @author Josh Morris
 */
public class BitMasks
{
    public BitMasks()
    {
        super();
    }

    /**
     * Does the specified mask contain more than one bit, or does more than one
     * bit have a value of one
     * 
     * @param aMask
     * @return boolean
     */
    public static boolean isPrimitive( long aMask )
    {
        int mBitCount = 0;

        for ( int i = 0; i < 32; i++ )
        {
            if ( contains( aMask >> i, 1 ) )
            {
                mBitCount++;

                if ( mBitCount > 1 )
                {
                    break;
                }
            }
        }

        return mBitCount <= 1;
    }

    /**
     * Adds specified mask in a manner that guarantees every bit set to 1 in
     * either mask will be set to 1 in the resulting mask.
     * 
     * @param aRecipient
     *            permission to change
     * @param aMask
     *            permission to add
     */
    public static long addMask( long aRecipient, long aMask )
    {
        return aRecipient |= aMask;
    }

    /**
     * Only subtracts mask if recipient mask is such that it can guarantee that
     * only the bits set to 1 are reset.
     * 
     * @param aRecipient
     *            value to change
     * @param aMask
     *            value used to compute necessary changes
     */
    public static long subtractMask( long aRecipient, long aMask )
    {
        if ( ( aRecipient & aMask ) > 0 )
        {
            return ( aRecipient & aMask ) ^ aRecipient;
        }

        return aRecipient;
    }

    /**
     * Determines if the requested mask is wholly contained within the
     * specified mask.
     * 
     * @param aContainer
     *            mask to search within
     * @param aContained
     *            mask to search for
     * 
     * @return true if wholly contained
     */
    public static boolean contains( long aContainer, long aContained )
    {
        return ( aContained == ( aContained & aContainer ) );
    }

    /**
     * Determines if the requested mask is, at least partially, contained
     * within the specified mask.
     * 
     * @param aContainer
     *            mask to search within
     * @param aContained
     *            mask to search for
     * 
     * @return false if the two masks do not have a single bit in common
     */
    public static boolean containsPartOfMask( long aContainer, long aContained )
    {
        if ( aContainer > 0 )
        {
            if ( aContained > 0 )
            {
                return ( ( aContained & aContainer ) > 0 );
            }
            else
            {
                return true;
            }
        }
        else if ( aContained == 0 )
        {
            return true;
        }

        return false;
    }
}
