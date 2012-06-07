package com.unitt.servicemanager.routing;

public interface Pushes<T> {
    void push(T aHeader, long aQueueTimeoutInMillis);
}
