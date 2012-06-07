package com.unitt.servicemanager.worker;

import com.unitt.servicemanager.routing.Pulls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelegateMaster<D> {
    private static Logger logger = LoggerFactory.getLogger(DelegateMaster.class);

    protected DelegateWorker<D>[] workers;
    protected Processor<D> processor;
    protected Pulls<D> pulls;
    protected long queueTimeOutInMillis = 10000;
    protected String name = "DelegateMaster";
    protected int numberOfThreads = 10;


    // constructors
    // ---------------------------------------------------------------------------
    public DelegateMaster() {
        //default
    }

    public DelegateMaster(String aName, Pulls<D> aPulls, Processor<D> aProcessor, long aQueueTimeOutInMillis, int aNumberOfThreads) {
        //set properties
        if (aName != null && !aName.isEmpty()) {
            name = aName;
        }
        pulls = aPulls;
        processor = aProcessor;
        if (aQueueTimeOutInMillis > 0) {
            queueTimeOutInMillis = aQueueTimeOutInMillis;
        }
        if (aNumberOfThreads > 0) {
            numberOfThreads = aNumberOfThreads;
        }
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    public Processor<D> getProcessor() {
        return processor;
    }

    public void setProcessor(Processor<D> aProcessor) {
        processor = aProcessor;
    }

    public Pulls<D> getPulls() {
        return pulls;
    }

    public void setPulls(Pulls<D> aPulls) {
        pulls = aPulls;
    }

    public long getQueueTimeOutInMillis() {
        return queueTimeOutInMillis;
    }

    public void setQueueTimeOutInMillis(long aQueueTimeOutInMillis) {
        queueTimeOutInMillis = aQueueTimeOutInMillis;
    }

    public String getName() {
        return name;
    }

    public void setName(String aName) {
        name = aName;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int aNumberOfThreads) {
        numberOfThreads = aNumberOfThreads;
    }


    // master logic
    // ---------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void startup() {
        //init
        Thread[] threads = new Thread[getNumberOfThreads()];
        workers = new DelegateWorker[getNumberOfThreads()];

        //build threads
        for (int i = 0; i < getNumberOfThreads(); i++) {
            workers[i] = new DelegateWorker<D>(getName(), getPulls(), getProcessor(), getQueueTimeOutInMillis());
            threads[i] = new Thread(workers[i], getName() + " #" + i);
        }

        //start threads
        for (int i = 0; i < getNumberOfThreads(); i++) {
            try {
                workers[i].start();
                threads[i].start();
            } catch (Exception e) {
                logger.error("Could not start worker: " + workers[i].getName(), e);
            }
        }
    }

    public void shutdown() {
        //stop threads
        for (int i = 0; i < getNumberOfThreads(); i++) {
            try {
                workers[i].stop();
            } catch (Exception e) {
                logger.warn("An error occurred stopping workers.", e);
            }
        }

        workers = null;
    }
}
