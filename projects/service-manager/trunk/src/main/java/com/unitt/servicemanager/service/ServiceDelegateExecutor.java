package com.unitt.servicemanager.service;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ServiceDelegateExecutor extends ThreadPoolExecutor
{
    private ServiceDelegate serviceDelegate;


    // constructors
    // ---------------------------------------------------------------------------
    public ServiceDelegateExecutor( int aCorePoolSize, int aMaximumPoolSize, long aKeepAliveTime, TimeUnit aUnit, BlockingQueue<Runnable> aWorkQueue )
    {
        super( aCorePoolSize, aMaximumPoolSize, aKeepAliveTime, aUnit, aWorkQueue );
    }

    public ServiceDelegateExecutor( int aCorePoolSize, int aMaximumPoolSize, long aKeepAliveTime, TimeUnit aUnit, BlockingQueue<Runnable> aWorkQueue, ThreadFactory aThreadFactory )
    {
        super( aCorePoolSize, aMaximumPoolSize, aKeepAliveTime, aUnit, aWorkQueue, aThreadFactory );
    }

    public ServiceDelegateExecutor( int aCorePoolSize, int aMaximumPoolSize, long aKeepAliveTime, TimeUnit aUnit, BlockingQueue<Runnable> aWorkQueue, RejectedExecutionHandler aHandler )
    {
        super( aCorePoolSize, aMaximumPoolSize, aKeepAliveTime, aUnit, aWorkQueue, aHandler );
    }

    public ServiceDelegateExecutor( int aCorePoolSize, int aMaximumPoolSize, long aKeepAliveTime, TimeUnit aUnit, BlockingQueue<Runnable> aWorkQueue, ThreadFactory aThreadFactory, RejectedExecutionHandler aHandler )
    {
        super( aCorePoolSize, aMaximumPoolSize, aKeepAliveTime, aUnit, aWorkQueue, aThreadFactory, aHandler );
    }


    // routing logic
    // ---------------------------------------------------------------------------
    @Override
    protected void afterExecute( Runnable aRunnable, Throwable aThrowable )
    {
        if ( aRunnable instanceof ServiceDelegateJob )
        {
            ( (ServiceDelegateJob) aRunnable ).setServiceDelegate( null );
        }
        super.afterExecute( aRunnable, aThrowable );
    }

    @Override
    protected void beforeExecute( Thread aThread, Runnable aRunnable )
    {
        if ( aRunnable instanceof ServiceDelegateJob )
        {
            ( (ServiceDelegateJob) aRunnable ).setServiceDelegate( getServiceDelegate() );
        }
        super.beforeExecute( aThread, aRunnable );
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public ServiceDelegate getServiceDelegate()
    {
        return serviceDelegate;
    }

    public void setServiceDelegate( ServiceDelegate aServiceDelegate )
    {
        serviceDelegate = aServiceDelegate;
    }
}
