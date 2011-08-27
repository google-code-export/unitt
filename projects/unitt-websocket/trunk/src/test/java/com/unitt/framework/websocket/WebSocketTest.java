package com.unitt.framework.websocket;


import java.nio.charset.Charset;

import junit.framework.Assert;

import org.junit.Test;

import com.unitt.framework.websocket.WebSocketFragment.MessageOpCode;
import com.unitt.framework.websocket.WebSocketFragment.PayloadType;


public class WebSocketTest
{
    protected static final Charset utf8Charset = Charset.forName( "UTF-8" );

    @Test
    public void testMasking()
    {
        // test two way
        int mask = WebSocketFragment.generateMask();
        String text = "Hello";
        byte[] masked = WebSocketFragment.mask( mask, text.getBytes( utf8Charset ) );
        byte[] unmasked = WebSocketFragment.unmask( mask, masked );
        String testText = new String( unmasked, utf8Charset );
        Assert.assertEquals( "Masking is not two-way", text, testText );

        // test spec
        mask = 0x37 << 24 | 0xfa << 16 | 0x21 << 8 | 0x3d;
        masked = new byte[] { 0x7f, new Integer( 0x9f ).byteValue(), 0x4d, 0x51, 0x58 };
        unmasked = WebSocketFragment.unmask( mask, masked );
        testText = new String( unmasked, utf8Charset );
        Assert.assertEquals( "Did not find the correct message.", "Hello", testText );
    }

    @Test
    public void testUnmaskedText()
    {
        byte[] bytes = new byte[] { new Integer(0x81).byteValue(), 0x05, 0x48, 0x65, 0x6c, 0x6c, 0x6f };
        WebSocketFragment fragment = new WebSocketFragment( bytes );
        Assert.assertTrue( "Did not set final bit.", fragment.isFinal() );
        Assert.assertEquals( "Did not find the correct payloadtype.", PayloadType.TEXT, fragment.getPayloadType() );
        Assert.assertTrue( "Did have a mask.", !fragment.hasMask() );
        Assert.assertNotNull( "Did not build any payload data", fragment.getPayloadData() );
        Assert.assertEquals( "Did not find the correct message.", "Hello", new String( fragment.getPayloadData(), utf8Charset ) );
    }

    @Test
    public void testMaskedText()
    {
      byte[] sample = {new Integer(0x81).byteValue(), new Integer(0x85).byteValue(), 0x37, new Integer(0xfa).byteValue(), 0x21, 0x3d, 0x7f, new Integer(0x9f).byteValue(), 0x4d, 0x51, 0x58};
      WebSocketFragment fragment = new WebSocketFragment( sample );
      Assert.assertTrue("Did not set final bit.", fragment.isFinal());
      Assert.assertEquals("Did not find the correct payloadtype.", PayloadType.TEXT, fragment.getPayloadType() );
      Assert.assertTrue("Did not find the correct has mask value.", fragment.hasMask());
      Assert.assertNotNull( "Did not build any payload data", fragment.getPayloadData());
      int correctMask =  WebSocketUtil.convertBytesToInt( new byte[] {0x37, new Integer(0xfa).byteValue(), 0x21, 0x3d}, 0);
      Assert.assertEquals("Did not find correct mask expected=" + Integer.toHexString( correctMask ) + ", actual=" + Integer.toHexString( fragment.getMask() ), correctMask, fragment.getMask());
      String message = new String(fragment.getPayloadData(), utf8Charset);
      System.out.println(message);
      Assert.assertEquals("Did not find the correct message.", "Hello", message);
      fragment = new WebSocketFragment( MessageOpCode.TEXT, true, true, "Hello".getBytes( utf8Charset ) );
      fragment.setMask( correctMask );
      Assert.assertEquals("Did not apply correct mask", correctMask, fragment.getMask());
      fragment.buildFragment();
      byte[] buffer = fragment.getFragment();
      for (int i = 0; i < 6; i++) 
      {
          Assert.assertEquals("Byte #" + i + " is different. Should be '" + Integer.toHexString( sample[i] ) + "'. It was '" + Integer.toHexString( buffer[i] ) + "'", sample[i], buffer[i]);
      }
      byte[] sampleMessageData = WebSocketUtil.copySubArray( sample, 6, 5 );
      byte[] fragmentMessageData = WebSocketUtil.copySubArray( buffer, 6, 5 );
      Assert.assertEquals("Payload arrays are not the same length.", sampleMessageData.length, fragmentMessageData.length);
      for (int i = 0; i < sampleMessageData.length; i++) 
      {
          Assert.assertEquals("Payload Byte #" + i + " is different. Should be '" + Integer.toHexString( sampleMessageData[i] ) + "'. It was '" + Integer.toHexString( fragmentMessageData[i] ) + "'", sampleMessageData[i], fragmentMessageData[i]);
      }
      Assert.assertEquals("Fragment arrays are not the same length.", sampleMessageData.length, fragmentMessageData.length);
      for (int i = 0; i < sample.length; i++) 
      {
          Assert.assertEquals("Payload Byte #" + i + " is different. Should be '" + Integer.toHexString( sample[i] ) + "'. It was '" + Integer.toHexString( fragment.getFragment()[i] ) + "'", sample[i], fragment.getFragment()[i]);
      }
    }

    @Test
    public void testFragmentedText()
    {
    }

    @Test
    public void testUnmaskedBinary()
    {
    }
}
