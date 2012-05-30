package com.unitt.servicemanager.service;

import com.unitt.servicemanager.websocket.MessageResponse;
import com.unitt.servicemanager.websocket.MessageRoutingInfo;

public class PushesResultsImpl<T> implements PushesResults<T> {
    private ServiceDelegate delegate;
    private MessageResponse response;
    
    public PushesResultsImpl(ServiceDelegate aDelegate, MessageResponse aResponse)
    {
        delegate = aDelegate;
        response = aResponse;
    }
    
    public void push(T aPartialResult)
    {
        MessageResponse cloned = new MessageResponse();
        cloned.setHeader(new MessageRoutingInfo(response.getHeader()));
        cloned.setBody(aPartialResult);
        cloned.getHeader().setResultType( MessageRoutingInfo.MessageResultType.PartialSuccess);
        delegate.sendResponse(cloned);
    }

    public void complete()
    {
        response.getHeader().setResultType( MessageRoutingInfo.MessageResultType.CompleteSuccess);
        delegate.sendResponse(response);
    }
}
