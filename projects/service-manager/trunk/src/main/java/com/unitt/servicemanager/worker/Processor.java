package com.unitt.servicemanager.worker;

public interface Processor<T>
{
    public void process(T aObject);
}
