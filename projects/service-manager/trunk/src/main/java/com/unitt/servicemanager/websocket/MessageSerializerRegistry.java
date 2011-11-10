package com.unitt.servicemanager.websocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageSerializerRegistry
{
    protected Map<Short, MessageSerializer> serializers = new HashMap<Short, MessageSerializer>();
    
    
    // constructors
    // ---------------------------------------------------------------------------
    public MessageSerializerRegistry()
    {
        //default
    }
    
    public MessageSerializerRegistry(List<MessageSerializer> aSerializers)
    {
        register(aSerializers);
    }
    
        
    // registry logic
    // ---------------------------------------------------------------------------
    public MessageSerializer getSerializer(Short aType)
    {
        return serializers.get( aType );
    }
    
    public void register(MessageSerializer aSerializer)
    {
        serializers.put(aSerializer.getSerializerType(), aSerializer);
    }
    
    public void register(List<MessageSerializer> aSerializers)
    {
        for(MessageSerializer serializer : aSerializers)
        {
            register(serializer);
        }
    }
}
