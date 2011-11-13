package com.unitt.servicemanager.worker;

public abstract class WorkerImpl implements Worker
{
    protected boolean isRunning        = false;
    protected boolean isDestroyed      = false;
    protected String  name;


    // constructors
    // --------------------------------------------------------------
    public WorkerImpl()
    {
        // default
    }

    public WorkerImpl( String aName )
    {
        name = aName;
    }


    // Runnable implementation
    // --------------------------------------------------------------
    protected abstract void internalRun();

    public void run()
    {
        // keep looping while not destroyed
        while ( !isDestroyed )
        {
            // make sure we are running
            if ( isRunning )
            {
                internalRun();
            }
            else
            {
                try
                {
                    synchronized(this)
                    {
                        wait();
                    }
                }
                catch ( InterruptedException e )
                {
                    try
                    {
                        Thread.sleep( 100 );
                    }
                    catch ( InterruptedException ignored )
                    {
                        //do nothing
                    }
                }
            }
        }
    }


    // Destructable implementation
    // --------------------------------------------------------------
    public void destroy()
    {
        isDestroyed = true;
    }


    // WorkerThread implementation
    // --------------------------------------------------------------
    public boolean isRunning()
    {
        return isRunning;
    }

    public void start()
    {
        isRunning = true;
        synchronized(this)
        {
            notify();
        }
    }

    public void stop()
    {
        isRunning = false;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String aName )
    {
        name = aName;
    }
}
