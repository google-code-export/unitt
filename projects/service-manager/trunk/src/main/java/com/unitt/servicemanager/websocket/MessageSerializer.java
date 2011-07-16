package com.unitt.servicemanager.websocket;

public interface MessageSerializer
{
    public short getSerializerType();
    
    public byte[] serializeHeader(MessageRoutingInfo aRoutingInfo);
    public byte[] serializeBody(Object aObject);
    
    public MessageRoutingInfo deserializeHeader(byte[] aHeader);
    public Object[] deserializeBody(byte[] aBody);
}
