package com.unitt.framework.websocket;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Josh Morris
 */
public class WebSocketMessage
{
    private static Logger             logger    = LoggerFactory.getLogger( WebSocketMessage.class );

    protected List<WebSocketFragment> fragments = new ArrayList<WebSocketFragment>();


    // constructors
    // ---------------------------------------------------------------------------
    public WebSocketMessage()
    {
        // default
    }

    public WebSocketMessage( WebSocketFragment aFragment )
    {
        add( aFragment );
    }


    // message logic
    // ---------------------------------------------------------------------------
    public void add( WebSocketFragment aFragment )
    {
        if ( aFragment != null )
        {
            fragments.add( aFragment );
        }
    }

    public byte[] getData()
    {
        try
        {
            System.out.println("Getting data from " + fragments.size() + " fragments");
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            for ( WebSocketFragment fragment : fragments )
            {
                output.write( fragment.getPayloadData() );
            }
            return output.toByteArray();
        }
        catch ( IOException e )
        {
            logger.error( "Could not concatenate all payload data from fragments", e );
        }

        return null;
    }

    public void clear()
    {
        fragments.clear();
    }
}
