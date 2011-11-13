package com.unitt.servicemanager.worker;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DelegateWorker<D, P extends Processor<D>> extends WorkerImpl
{
    private static Logger logger = LoggerFactory.getLogger( DelegateWorker.class );
    
    protected Processor<D>     processor;
    protected BlockingQueue<D> queue;
    protected long queueTimeOutInMillis = 10000;


    // constructors
    // ---------------------------------------------------------------------------
    public DelegateWorker( BlockingQueue<D> aQueue, Processor<D> aProcessor )
    {
        super();

        processor = aProcessor;
        queue = aQueue;
    }

    public DelegateWorker( String aName, BlockingQueue<D> aQueue, Processor<D> aProcessor, long aQueueTimeOutInMillis )
    {
        super( aName );

        processor = aProcessor;
        queue = aQueue;
        queueTimeOutInMillis = aQueueTimeOutInMillis;
    }


    // service logic
    // ---------------------------------------------------------------------------
    @Override
    protected void internalRun()
    {
        try
        {
            D item = queue.poll( queueTimeOutInMillis, TimeUnit.MILLISECONDS );
            if (item != null)
            {
                processor.process( item );
            }
        }
        catch (Exception e)
        {
            logger.error( "An error occurred while acquiring and processing an item on the queue.", e );
        }
    }
}
