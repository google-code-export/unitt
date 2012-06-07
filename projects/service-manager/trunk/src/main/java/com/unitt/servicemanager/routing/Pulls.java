package com.unitt.servicemanager.routing;

public interface Pulls<T> {
    T pull(long aQueueTimeoutInMillis);
}
