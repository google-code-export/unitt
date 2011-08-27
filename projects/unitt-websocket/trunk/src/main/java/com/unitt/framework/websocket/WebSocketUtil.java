package com.unitt.framework.websocket;

/**
 * @author Josh Morris
 */
public class WebSocketUtil
{
    public static byte[] copySubArray( byte[] aArray, int aStart, int aLength )
    {
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
        byte[] results = new byte[aOriginal.length + aAdditional.length];

        System.arraycopy( aOriginal, 0, results, 0, aOriginal.length );
        System.arraycopy( aAdditional, 0, results, aOriginal.length, aAdditional.length );

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

    public static short convertBytesToShort( byte[] aBytes, int aStart )
    {
        return convertBytes( aBytes, aStart, 2 ).shortValue();
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
            long byteValue = aBytes[i];
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

    public static byte[] convertShortToBytes( short aValue )
    {
        return convertToBytes( aValue, 2 );
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
