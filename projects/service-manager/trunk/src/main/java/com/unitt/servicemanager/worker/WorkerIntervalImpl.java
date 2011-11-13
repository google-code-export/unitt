package com.unitt.servicemanager.worker;

public abstract class WorkerIntervalImpl extends WorkerImpl implements IntervalWorker
{
    protected long    intervalInMillis = DEFAULT_INTERVAL;
    protected long    timeLastRanInMillis;


    // constructors
    // --------------------------------------------------------------
    public WorkerIntervalImpl()
    {
        // default
    }

    public WorkerIntervalImpl( String aName )
    {
        super(aName);
    }

    public WorkerIntervalImpl( String aName, long aIntervalInMillis )
    {
        this( aName );

        intervalInMillis = aIntervalInMillis;
    }


    // Runnable implementation
    // --------------------------------------------------------------
    public void run()
    {
        // keep looping while not destroyed
        while ( !isDestroyed )
        {
            // make sure we are running
            if ( isRunning )
            {
                // is it time to run again
                if ( System.currentTimeMillis() > ( timeLastRanInMillis + getIntervalInMillis() ) )
                {
                    timeLastRanInMillis = System.currentTimeMillis();
                    internalRun();
                }
            }

            // sleep
            try
            {
                Thread.sleep( intervalInMillis );
            }
            catch ( Exception e )
            {
                // do nothing
            }
        }
    }


    // WorkerThread implementation
    // --------------------------------------------------------------
    public long getIntervalInMillis()
    {
        return intervalInMillis;
    }

    public void setIntervalInMillis( long aIntervalInMillis )
    {
        intervalInMillis = aIntervalInMillis;
    }
}
