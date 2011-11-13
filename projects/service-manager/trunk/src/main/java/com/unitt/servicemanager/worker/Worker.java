package com.unitt.servicemanager.worker;

import com.unitt.commons.foundation.lifecycle.Destructable;


public interface Worker extends Runnable, Destructable
{
    public String getName();
    
    public void setName(String aName);

    public void start();

    public void stop();

    public boolean isRunning();
}
