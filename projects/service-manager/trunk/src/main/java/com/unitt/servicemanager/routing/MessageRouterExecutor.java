package com.unitt.servicemanager.routing;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class MessageRouterExecutor extends ThreadPoolExecutor
{
    private MessageRouter router;

    // constructors
    // ---------------------------------------------------------------------------
    public MessageRouterExecutor( int aCorePoolSize, int aMaximumPoolSize, long aKeepAliveTime, TimeUnit aUnit, BlockingQueue<Runnable> aWorkQueue )
    {
        super( aCorePoolSize, aMaximumPoolSize, aKeepAliveTime, aUnit, aWorkQueue );
    }

    public MessageRouterExecutor( int aCorePoolSize, int aMaximumPoolSize, long aKeepAliveTime, TimeUnit aUnit, BlockingQueue<Runnable> aWorkQueue, ThreadFactory aThreadFactory )
    {
        super( aCorePoolSize, aMaximumPoolSize, aKeepAliveTime, aUnit, aWorkQueue, aThreadFactory );
    }

    public MessageRouterExecutor( int aCorePoolSize, int aMaximumPoolSize, long aKeepAliveTime, TimeUnit aUnit, BlockingQueue<Runnable> aWorkQueue, RejectedExecutionHandler aHandler )
    {
        super( aCorePoolSize, aMaximumPoolSize, aKeepAliveTime, aUnit, aWorkQueue, aHandler );
    }

    public MessageRouterExecutor( int aCorePoolSize, int aMaximumPoolSize, long aKeepAliveTime, TimeUnit aUnit, BlockingQueue<Runnable> aWorkQueue, ThreadFactory aThreadFactory, RejectedExecutionHandler aHandler )
    {
        super( aCorePoolSize, aMaximumPoolSize, aKeepAliveTime, aUnit, aWorkQueue, aThreadFactory, aHandler );
    }


    // routing logic
    // ---------------------------------------------------------------------------
    @Override
    protected void afterExecute( Runnable aRunnable, Throwable aThrowable )
    {
        if ( aRunnable instanceof MessageRouterJob )
        {
            ( (MessageRouterJob) aRunnable ).setMessageRouter( null );
        }
        super.afterExecute( aRunnable, aThrowable );
    }

    @Override
    protected void beforeExecute( Thread aThread, Runnable aRunnable )
    {
        if ( aRunnable instanceof MessageRouterJob )
        {
            ( (MessageRouterJob) aRunnable ).setMessageRouter( getRouter() );
        }
        super.beforeExecute( aThread, aRunnable );
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public MessageRouter getRouter()
    {
        return router;
    }

    public void setRouter( MessageRouter aRouter )
    {
        router = aRouter;
    }
}
