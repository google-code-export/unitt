package com.unitt.framework.websocket;

/**
 * @author Josh Morris
 */
public class WebSocketUtil
{
    public static String getBytesPrintedOutput(byte[] aData)
    {
        StringBuffer out = new StringBuffer("Bytes: ");
        
        if (aData != null)
        {
            for (byte item : aData)
            {
                out.append(Integer.toHexString( item & 0xFF ) );
                out.append( "," );
            }
        }
        else
        {
            out.append("null");
        }
        
        return out.toString();
    }

    public static int getIndexOf(byte[] aToSearchIn, byte[] aToSearchFor) {
        int matchingIndexInSearchFor = 0;
        for (int i = 0; i < aToSearchIn.length; i++) {
            if (aToSearchIn[i] == aToSearchFor[matchingIndexInSearchFor]) {
                matchingIndexInSearchFor++;
                if (matchingIndexInSearchFor >= aToSearchFor.length) {
                    int result = i - aToSearchFor.length + 1;
                    if (result >= 0) {
                        return result;
                    }
                }
            } else {
                matchingIndexInSearchFor = 0;
            }
        }

        return -1;
    }
    
    public static byte[] copySubArray( byte[] aArray, int aStart, int aLength )
    {
        if (aArray == null) {
            return new byte[0];
        }

        int actualLength = aLength;

        // if the specified length is too big, trim
        if ( aStart + actualLength > aArray.length )
        {
            actualLength = aArray.length - aStart;
        }

        // copy bytes and return
        byte[] results = new byte[actualLength];
        System.arraycopy( aArray, aStart, results, 0, actualLength );
        return results;
    }

    public static byte[] appendArray( byte[] aOriginal, byte[] aAdditional )
    {
        int originalLength = aOriginal != null ? aOriginal.length : 0;
        int additionalLength = aAdditional != null ? aAdditional.length : 0;
        byte[] results = new byte[originalLength + additionalLength];

        if (aOriginal != null) {
            System.arraycopy( aOriginal, 0, results, 0, originalLength );
        }
        if (aAdditional != null) {
            System.arraycopy( aAdditional, 0, results, originalLength, additionalLength );
        }

        return results;
    }

    public static byte[] appendPartialArray( byte[] aOriginal, byte[] aAdditional, int aAdditionalStart, int aAdditionalLength )
    {
        int originalLength = aOriginal != null ? aOriginal.length : 0;
        byte[] results = new byte[originalLength + aAdditionalLength];

        if (aOriginal != null) {
            System.arraycopy( aOriginal, 0, results, 0, originalLength );
        }
        if (aAdditional != null) {
            System.arraycopy( aAdditional, aAdditionalStart, results, originalLength, aAdditionalLength );
        }

        return results;
    }

    public static long convertBytesToLong( byte[] aBytes, int aStart )
    {
        return convertBytes( aBytes, aStart, 8 );
    }

    public static int convertBytesToInt( byte[] aBytes, int aStart )
    {
        return convertBytes( aBytes, aStart, 4 ).intValue();
    }

    public static int convertBytesToShort( byte[] aBytes, int aStart )
    {
        return convertBytes( aBytes, aStart, 2 ).intValue();
    }

    protected static Long convertBytes( byte[] aBytes, int aStart, int aLength )
    {
        long result = 0;
        int count = 1;
        for ( int i = aStart; i < aStart + aLength; i++ )
        {
            // shift byte to correct location
            int bitsToShift = ( aLength - count++ ) * 8;
            long mask = 0xFF;
            long byteValue = aBytes[i];  //aBytes.length > i ? aBytes[i] : 0;
            if ( bitsToShift > 0 )
            {
                byteValue = byteValue << bitsToShift;
                mask = 0xFF << bitsToShift;
            }

            // push byte into result
            result |= (byteValue & mask);
        }

        return result;
    }

    public static byte[] convertLongToBytes( long aValue )
    {
        return convertToBytes( aValue, 8 );
    }

    public static byte[] convertIntToBytes( int aValue )
    {
        return convertToBytes( aValue, 4 );
    }

    public static byte[] convertShortToBytes( int aValue )
    {
        return  convertToBytes( aValue, 2 );
    }

    protected static byte[] convertToBytes( long aValue, int aLength )
    {
        byte[] bytes = new byte[aLength];
        int count = 1;

        for ( int i = 0; i < bytes.length; i++ )
        {
            // shift and grab just the bytes we want
            int bitsToShift = ( aLength - count++ ) * 8;
            Long byteValue = aValue;
            if ( bitsToShift > 0 )
            {
                byteValue = byteValue >>> bitsToShift;
            }
            bytes[i] = byteValue.byteValue();
        }

        return bytes;
    }
}
