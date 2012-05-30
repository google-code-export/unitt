package com.unitt.servicemanager.service;

import java.io.Serializable;

public interface PushesResults<T>
{
    public void push(T aPartialResult);
    public void complete();
}
