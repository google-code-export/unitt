package com.unitt.servicemanager.worker;


import com.unitt.servicemanager.routing.Pulls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DelegateWorker<D> extends WorkerImpl {
    private static Logger logger = LoggerFactory.getLogger(DelegateWorker.class);

    protected Processor<D> processor;
    protected Pulls<D> pulls;
    protected long queueTimeOutInMillis = 10000;


    // constructors
    // ---------------------------------------------------------------------------
    public DelegateWorker(Pulls<D> aPulls, Processor<D> aProcessor) {
        super();

        pulls = aPulls;
        processor = aProcessor;
    }

    public DelegateWorker(String aName, Pulls<D> aPulls, Processor<D> aProcessor, long aQueueTimeOutInMillis) {
        super(aName);

        processor = aProcessor;
        pulls = aPulls;
        queueTimeOutInMillis = aQueueTimeOutInMillis;
    }


    // service logic
    // ---------------------------------------------------------------------------
    @Override
    protected void internalRun() {
        try {
            D item = pulls.pull(queueTimeOutInMillis);
            if (item != null) {
                processor.process(item);
            }
        } catch (Exception e) {
            logger.error("An error occurred while acquiring and processing an item.", e);
        }
    }
}
