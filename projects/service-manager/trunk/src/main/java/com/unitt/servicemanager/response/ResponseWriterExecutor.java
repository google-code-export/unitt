package com.unitt.servicemanager.response;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ResponseWriterExecutor extends ThreadPoolExecutor
{
    private ResponseWriter writer;
    
    
    // constructors
    // ---------------------------------------------------------------------------
    public ResponseWriterExecutor( int aCorePoolSize, int aMaximumPoolSize, long aKeepAliveTime, TimeUnit aUnit, BlockingQueue<Runnable> aWorkQueue )
    {
        super( aCorePoolSize, aMaximumPoolSize, aKeepAliveTime, aUnit, aWorkQueue );
    }

    public ResponseWriterExecutor( int aCorePoolSize, int aMaximumPoolSize, long aKeepAliveTime, TimeUnit aUnit, BlockingQueue<Runnable> aWorkQueue, ThreadFactory aThreadFactory )
    {
        super( aCorePoolSize, aMaximumPoolSize, aKeepAliveTime, aUnit, aWorkQueue, aThreadFactory );
    }

    public ResponseWriterExecutor( int aCorePoolSize, int aMaximumPoolSize, long aKeepAliveTime, TimeUnit aUnit, BlockingQueue<Runnable> aWorkQueue, RejectedExecutionHandler aHandler )
    {
        super( aCorePoolSize, aMaximumPoolSize, aKeepAliveTime, aUnit, aWorkQueue, aHandler );
    }

    public ResponseWriterExecutor( int aCorePoolSize, int aMaximumPoolSize, long aKeepAliveTime, TimeUnit aUnit, BlockingQueue<Runnable> aWorkQueue, ThreadFactory aThreadFactory, RejectedExecutionHandler aHandler )
    {
        super( aCorePoolSize, aMaximumPoolSize, aKeepAliveTime, aUnit, aWorkQueue, aThreadFactory, aHandler );
    }

    
    // getters & setters
    // ---------------------------------------------------------------------------
    public ResponseWriter getWriter()
    {
        return writer;
    }

    public void setWriter( ResponseWriter aWriter )
    {
        writer = aWriter;
    }
    

    // routing logic
    // ---------------------------------------------------------------------------
    @Override
    protected void afterExecute( Runnable aRunnable, Throwable aThrowable )
    {
        if ( aRunnable instanceof ResponseWriterJob )
        {
            ( (ResponseWriterJob) aRunnable ).setResponseWriter( null );
        }
        super.afterExecute( aRunnable, aThrowable );
    }

    @Override
    protected void beforeExecute( Thread aThread, Runnable aRunnable )
    {
        if ( aRunnable instanceof ResponseWriterJob )
        {
            ( (ResponseWriterJob) aRunnable ).setResponseWriter( getWriter() );
        }
        super.beforeExecute( aThread, aRunnable );
    }
}
