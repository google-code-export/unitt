package com.unitt.servicemanager.worker;

public interface IntervalWorker
{
    public static final long DEFAULT_INTERVAL = 60 * 1000;

    public long getIntervalInMillis();

    public void setIntervalInMillis( long aIntervalInMillis );
}
