package com.unitt.framework.websocket;

/**
 * @author Josh Morris
 */
public class WebSocketUtil
{
    public static byte[] copySubArray(byte[] array, int start, int length)
    {
        int actualLength = length;
        
        //if the specified length is too big, trim
        if (start + actualLength > array.length)
        {
            actualLength = array.length - start;
        }
        
        //copy bytes and return
        byte[] results = new byte[length];
        System.arraycopy( array, start, results, 0, length );
        return results;
    }
    
    public static byte[] appendArray(byte[] original, byte[] additional)
    {
        byte[] results = new byte[original.length + additional.length];
        
        System.arraycopy( original, 0, results, 0, original.length );
        System.arraycopy( additional, 0, results, original.length, additional.length );
        
        return results;
    }
    
    public static long convertBytesToLong(byte[] bytes, int start)
    {
        return convertBytes(bytes, start, 8);
    }
    
    public static int convertBytesToInt(byte[] bytes, int start)
    {
        return convertBytes(bytes, start, 4).intValue();
    }
    
    public static short convertBytesToShort(byte[] bytes, int start)
    {
        return convertBytes(bytes, start, 2).shortValue();
    }
    
    protected static Long convertBytes(byte[] bytes, int start, int length)
    {
        long result = 0;
        
        for ( int i = start; i < start + length; i++ )
        {
            //shift byte to correct location
            int bitsToShift = (bytes.length - i - 1) * 8;
            long byteValue = bytes[i];
            if (bitsToShift > 0)
            {
                byteValue = byteValue << bitsToShift;
            }
            
            //push byte into result
            result |= byteValue;
        }
        
        return result;
    }
    
    public static byte[] convertLongToBytes(long value)
    {
        return convertToBytes( value, 8 );
    }

    public static byte[] convertIntToBytes(int value)
    {
        return convertToBytes( value, 4 );
    }

    public static byte[] convertShortToBytes(short value)
    {
        return convertToBytes( value, 2 );
    }

    protected static byte[] convertToBytes(long value, int length)
    {
        byte[] bytes = new byte[length];
        
        for ( int i = 0; i < bytes.length; i++ )
        {
            //shift and grab just the bytes we want
            int bitsToShift = (bytes.length - i - 1) * 8;
            long byteValue = value;
            if (bitsToShift > 0)
            {
                byteValue = byteValue >> bitsToShift;
            }
            bytes[i] = (byte) ( byteValue & 0xFF );
        }
        
        return bytes;
    }
}
