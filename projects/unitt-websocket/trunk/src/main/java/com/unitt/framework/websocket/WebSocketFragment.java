package com.unitt.framework.websocket;


import java.nio.ByteBuffer;
import java.util.Random;


/**
 * @author Josh Morris
 */
public class WebSocketFragment
{
    public enum MessageOpCode
    {
        ILLEGAL( -1 ), CONTINUATION( 0x0 ), TEXT( 0x1 ), BINARY( 0x2 ), CLOSE( 0x8 ), PING( 0x9 ), PONG( 0xA );

        private int opCode;

        private MessageOpCode( int aOpCode )
        {
            opCode = aOpCode;
        }

        public int getOpCode()
        {
            return opCode;
        }
    };

    public enum PayloadType
    {
        UNKNOWN, TEXT, BINARY
    };

    public enum PayloadLength
    {
        ILLEGAL, MINIMUM, SHORT, LONG
    };

    private static Random randomizer = new Random();

    private boolean       isFinal;
    private int           mask;
    private int           payloadStart;
    private int           payloadLength;
    private PayloadType   payloadType;
    private byte[]        payloadData;
    private MessageOpCode opCode     = MessageOpCode.ILLEGAL;
    private byte[]        fragment;


    // constructors
    // ---------------------------------------------------------------------------
    public WebSocketFragment( MessageOpCode opCode, boolean isFinal, boolean useMask, byte[] payload )
    {
        if ( useMask )
        {
            setMask( generateMask() );
        }
        setOpCode( opCode );
        setFinal( isFinal );
        setPayloadData( payload );
        buildFragment();
    }

    public WebSocketFragment( byte[] fragment )
    {
        setOpCode( MessageOpCode.ILLEGAL );
        setFragment( fragment );
        parseHeader();
        if ( getMessageLength() <= fragment.length )
        {
            parseContent();
        }
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public boolean isFinal()
    {
        return isFinal;
    }

    public void setFinal( boolean isFinal )
    {
        this.isFinal = isFinal;
    }

    public int getMask()
    {
        return mask;
    }

    public void setMask( int mask )
    {
        this.mask = mask;
    }

    public int getPayloadStart()
    {
        return payloadStart;
    }

    public void setPayloadStart( int payloadStart )
    {
        this.payloadStart = payloadStart;
    }

    public int getPayloadLength()
    {
        return payloadLength;
    }

    public void setPayloadLength( int payloadLength )
    {
        this.payloadLength = payloadLength;
    }

    public PayloadType getPayloadType()
    {
        return payloadType;
    }

    public void setPayloadType( PayloadType aPayloadType )
    {
        payloadType = aPayloadType;
    }

    public void appendFragment( byte[] data )
    {
        fragment = WebSocketUtil.appendArray( fragment, data );
    }

    public byte[] getPayloadData()
    {
        return payloadData;
    }

    public void setPayloadData( byte[] payloadData )
    {
        this.payloadData = payloadData;
    }

    public MessageOpCode getOpCode()
    {
        return opCode;
    }

    public void setOpCode( MessageOpCode opCode )
    {
        this.opCode = opCode;
    }

    public void setOpCode( int opCode )
    {
        // set default value
        setOpCode( MessageOpCode.ILLEGAL );

        // see if the specified value matches an enum to use instead
        for ( MessageOpCode opCodeEnum : MessageOpCode.values() )
        {
            if ( opCodeEnum.getOpCode() == opCode )
            {
                setOpCode( opCodeEnum );
                return;
            }
        }
    }

    public byte[] getFragment()
    {
        return fragment;
    }

    public void setFragment( byte[] fragment )
    {
        this.fragment = fragment;
    }


    // fragment state
    // ---------------------------------------------------------------------------
    public boolean hasMask()
    {
        return getMask() != 0;
    }

    public boolean isControlFrame()
    {
        return getOpCode() == MessageOpCode.CLOSE || getOpCode() == MessageOpCode.PING || getOpCode() == MessageOpCode.PONG;
    }

    public boolean isDataFrame()
    {
        return getOpCode() == MessageOpCode.CONTINUATION || getOpCode() == MessageOpCode.TEXT || getOpCode() == MessageOpCode.BINARY;
    }

    public boolean isValid()
    {
        if ( getMessageLength() > 0 && fragment != null )
        {
            return getPayloadStart() + getPayloadLength() == fragment.length;
        }

        return false;
    }

    public boolean isHeaderValid()
    {
        return payloadStart > 0;
    }

    public boolean isDataValid()
    {
        return getPayloadData() != null && getPayloadData().length > 0;
    }

    public int getMessageLength()
    {
        if ( getFragment() != null && getPayloadStart() > 0 )
        {
            return getPayloadStart() + getPayloadLength();
        }

        return 0;
    }


    // content logic
    // ---------------------------------------------------------------------------
    public void parseHeader()
    {
        // get header data bits
        int bufferLength = 14;
        if ( getFragment() != null && getFragment().length < bufferLength )
        {
            bufferLength = getFragment().length;
        }
        byte[] buffer = WebSocketUtil.copySubArray( getFragment(), 0, bufferLength );

        // determine opcode
        if ( bufferLength > 0 )
        {
            int index = 0;
            setFinal( ( buffer[index] & 0x80 ) != 0 );
            setOpCode( buffer[index++] & 0x0F );

            // handle data depending on opcode
            switch ( getOpCode() )
            {
                case TEXT:
                    setPayloadType( PayloadType.TEXT );
                    break;
                case BINARY:
                    setPayloadType( PayloadType.BINARY );
                    break;
            }

            // handle content, if any
            if ( bufferLength > 1 )
            {
                // do we have a mask
                boolean hasMask = ( buffer[index] & 0x80 ) != 0;

                // get payload length
                Long dataLength = new Integer( buffer[index++] & 0x7F ).longValue();
                if ( dataLength == 126 )
                {
                    // exit if we are missing bytes
                    if ( bufferLength < 4 )
                    {
                        return;
                    }

                    dataLength = new Short( WebSocketUtil.convertBytesToShort( buffer, index ) ).longValue();
                    index += 2;
                }
                else if ( dataLength == 127 )
                {
                    // exit if we are missing bytes
                    if ( bufferLength < 10 )
                    {
                        return;
                    }

                    index += 8;
                    dataLength = WebSocketUtil.convertBytesToLong( buffer, index );
                }

                // if applicable, set mask value
                if ( hasMask )
                {
                    // exit if we are missing bytes
                    if ( bufferLength < index + 4 )
                    {
                        return;
                    }

                    // grab mask
                    setMask( WebSocketUtil.convertBytesToInt( buffer, index ) );
                    index += 4;
                }

                payloadStart = index;
                if ( dataLength > Integer.MAX_VALUE )
                {
                    throw new IllegalArgumentException( "Implementation does not support payload lengths in excess of " + Integer.MAX_VALUE + ": " + dataLength );
                }
                payloadLength = dataLength.intValue();
            }
        }
    }

    public void parseContent()
    {
        System.out.println( "Getting content: start=" + getPayloadStart() + " length=" + getPayloadLength() );
        if ( getFragment() != null && getFragment().length >= getPayloadStart() + getPayloadLength() )
        {
            // set payload
            if ( hasMask() )
            {
                setPayloadData( WebSocketFragment.unmask( getMask(), getFragment(), getPayloadStart(), getPayloadLength() ) );
            }
            else
            {
                setPayloadData( WebSocketUtil.copySubArray( getFragment(), getPayloadStart(), getPayloadLength() ) );
            }

            // trim fragment, if necessary
            if ( getFragment() != null && getFragment().length > getMessageLength() )
            {
                setFragment( WebSocketUtil.copySubArray( getFragment(), 0, getMessageLength() ) );
            }
        }
    }

    protected int determineHeaderLength()
    {
        int length = 1;

        // account for including payload length
        int fullPayloadLength = getPayloadData().length;
        if ( fullPayloadLength <= 125 )
        {
            length += 1;
        }
        else if ( fullPayloadLength <= Short.MAX_VALUE )
        {
            length += 3;
        }
        else
        {
            length += 9;
        }

        // account for mask, if any
        if ( hasMask() )
        {
            length += 4;
        }

        return length;
    }

    public void buildFragment()
    {
        // init
        Integer fullPayloadLength = getPayloadData().length;
        int headerLength = determineHeaderLength();
        ByteBuffer output = ByteBuffer.allocate( headerLength + fullPayloadLength );

        // build fin & reserved
        Integer b = 0x0;
        if ( isFinal() )
        {
            b = 0x80;
        }

        // build opmask
        b |= new Integer( getOpCode().opCode & 0xF );

        // push first byte
        output.put( b.byteValue() );

        // use mask
        b = hasMask() ? 0x80 : 0x0;

        // payload length
        if ( fullPayloadLength <= 125 )
        {
            b |= (byte) ( fullPayloadLength & 0xFF );
            output.put( b.byteValue() );
        }
        else if ( fullPayloadLength <= Short.MAX_VALUE )
        {
            b |= 126;
            output.put( b.byteValue() );
            output.putShort( fullPayloadLength.shortValue() );
        }
        else
        {
            b |= 127;
            output.put( b.byteValue() );
            output.putLong( fullPayloadLength.longValue() );
        }

        // mask
        if ( hasMask() )
        {
            output.putInt( getMask() );
        }

        // payload data
        setPayloadStart( headerLength );
        setPayloadLength( fullPayloadLength );
        if ( hasMask() )
        {
            output.put( mask( getMask(), getPayloadData(), 0, payloadLength ) );
        }
        else
        {
            output.put( getPayloadData() );
        }

        // set fragment
        setFragment( output.array() );

        // cleanup
        output = null;
    }


    // utility methods
    // ---------------------------------------------------------------------------
    public static int generateMask()
    {
        return randomizer.nextInt();
    }

    public static byte[] mask( int mask, byte[] data )
    {
        if ( data != null )
        {
            return mask( mask, data, 0, data.length );
        }

        return null;
    }

    public static byte[] mask( int mask, byte[] data, int start, int length )
    {
        if ( data != null )
        {
            // init
            byte[] results = new byte[length];

            // get mask
            byte[] maskBytes = WebSocketUtil.convertIntToBytes( mask );

            // loop through mask data, masking
            int end = start + length;
            byte current;
            int index = start;
            if ( end > data.length )
            {
                end = data.length;
            }
            int m = 0;
            while ( index < end )
            {
                // set current byte
                current = data[index];

                // mask
                current ^= maskBytes[m++ % 4];

                // append result & continue
                results[index - start] = current;
                index++;
            }

            return results;
        }

        return null;
    }

    // public static byte[] mask( int mask, byte[] data, int start, int length )
    // {
    // if ( data != null )
    // {
    // // init
    // byte[] results = new byte[data.length];
    //
    // // if there are bytes before our masking data, copy
    // if ( start > 0 )
    // {
    // System.arraycopy( data, 0, results, 0, start );
    // }
    //
    // // get mask
    // byte[] maskBytes = WebSocketUtil.convertIntToBytes( mask );
    //
    // // loop through mask data, masking
    // int end = start + length;
    // byte current;
    // int index = start;
    // if ( end > data.length )
    // {
    // end = data.length;
    // }
    // int m = 0;
    // while ( index < end )
    // {
    // // set current byte
    // current = data[index];
    //
    // // mask
    // current ^= maskBytes[m++ % 4];
    //
    // // append result & continue
    // results[index] = current;
    // index++;
    // }
    //
    // // if there are bytes after our masking data, copy
    // if ( end < data.length )
    // {
    // System.arraycopy( data, end, results, end, length - end );
    // }
    //
    // return results;
    // }
    //
    // return null;
    // }

    public static byte[] unmask( int mask, byte[] data )
    {
        return mask( mask, data );
    }

    public static byte[] unmask( int mask, byte[] data, int start, int length )
    {
        return mask( mask, data, start, length );
    }
}
