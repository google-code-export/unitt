package com.unitt.servicemanager.service;

import java.io.Serializable;

public interface PartialResults<T extends Serializable>
{
    public void contribute(T aPartialResult);    
    public void complete(T aCompleteResult);
    public void complete();
}
