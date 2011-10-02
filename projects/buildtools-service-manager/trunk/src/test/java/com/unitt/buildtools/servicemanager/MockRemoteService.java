package com.unitt.buildtools.servicemanager;

import com.unitt.servicemanager.service.RemoteService;

@RemoteService
public abstract class MockRemoteService
{
    public abstract void sayHello(String aMessage);
    public abstract void sayHelloTo(MockPerson aTarget, String aMessage);
}
