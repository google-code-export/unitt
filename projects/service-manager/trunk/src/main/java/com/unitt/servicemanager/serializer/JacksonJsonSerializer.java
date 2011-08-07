package com.unitt.servicemanager.serializer;


import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unitt.servicemanager.websocket.DeserializedMessageBody;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;
import com.unitt.servicemanager.websocket.MessageSerializer;


public class JacksonJsonSerializer implements MessageSerializer
{
    private static Logger logger = LoggerFactory.getLogger( JacksonJsonSerializer.class );

    private ObjectMapper  mapper;

    
    // constructors
    // ---------------------------------------------------------------------------
    public JacksonJsonSerializer()
    {
        mapper = new ObjectMapper();
    }
    
    
    // getters & setters
    // ---------------------------------------------------------------------------
    public ObjectMapper getMapper()
    {
        return mapper;
    }

    public void setMapper( ObjectMapper aMapper )
    {
        mapper = aMapper;
    }


    // message serializer logic
    // ---------------------------------------------------------------------------
    public DeserializedMessageBody deserializeBody( MessageRoutingInfo aInfo, byte[] aBody )
    {
        try
        {
            return mapper.readValue( aBody, DeserializedMessageBody.class );
        }
        catch ( Exception e )
        {
            logger.error( "Could not deserialize header.", e );
        }
        
        return null;
    }

    public MessageRoutingInfo deserializeHeader( byte[] aHeader )
    {
        try
        {
            return mapper.readValue( aHeader, MessageRoutingInfo.class );
        }
        catch ( Exception e )
        {
            logger.error( "Could not deserialize header.", e );
        }
        
        return null;
    }

    public short getSerializerType()
    {
        return SERIALIZER_TYPE_JSON;
    }

    public byte[] serializeBody( Object aObject )
    {
        try
        {
            return mapper.writeValueAsBytes( aObject );
        }
        catch ( Exception e )
        {
            logger.error( "Could not serialize body." + aObject, e );
        }
        
        return null;
    }

    public byte[] serializeHeader( MessageRoutingInfo aRoutingInfo )
    {
        try
        {
            return mapper.writeValueAsBytes( aRoutingInfo );
        }
        catch ( Exception e )
        {
            logger.error( "Could not serialize header: " + aRoutingInfo, e );
        }
        
        return null;
    }

}
