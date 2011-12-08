package com.unitt.buildtools.servicemanager;

import com.unitt.servicemanager.service.RemoteService;

import java.util.List;

@RemoteService
public abstract class MockRemoteService
{
    public abstract void sayHello(String aMessage);
    public abstract void sayHelloTo(MockPerson aTarget, String aMessage);
    public abstract String getMessage(String aPrefix);
    public abstract MockPerson getPerson(String aPrefix);
    public abstract List<MockPerson> getPeople(String aPrefix);
}
