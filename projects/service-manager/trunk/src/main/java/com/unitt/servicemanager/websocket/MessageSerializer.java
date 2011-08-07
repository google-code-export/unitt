package com.unitt.servicemanager.websocket;

public interface MessageSerializer
{
    public static final short SERIALIZER_TYPE_JSON = 1;
    public static final short SERIALIZER_TYPE_XML = 2;
    
    //custom types must be over 16 (0-16 is reserved)
    public short getSerializerType();
    
    public byte[] serializeHeader(MessageRoutingInfo aRoutingInfo);
    public byte[] serializeBody(Object aObject);
    
    public MessageRoutingInfo deserializeHeader(byte[] aHeader);
    public DeserializedMessageBody deserializeBody(MessageRoutingInfo aInfo, byte[] aBody);
}
