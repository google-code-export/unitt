package com.unitt.servicemanager.service;

import java.io.Serializable;

public interface PushesResults<T extends Serializable>
{
    public void push(T aPartialResult);
    public void complete();
}
