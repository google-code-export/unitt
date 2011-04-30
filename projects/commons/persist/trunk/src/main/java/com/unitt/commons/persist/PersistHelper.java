package com.unitt.commons.persist;


import java.util.Calendar;


public class PersistHelper
{
    private static PersistHelper instance;
    private ActiveUserHelper activeUserHelper;


    // singleton (configured by Spring)
    // ---------------------------------------------------------------------------
    public static PersistHelper instance()
    {
        if (instance == null)
        {
            synchronized(PersistHelper.class)
            {
                if (instance == null)
                {
                    instance = new PersistHelper();
                }
            }
        }
        return instance;
    }


    // getters & setters
    // ---------------------------------------------------------------------------
    protected ActiveUserHelper getActiveUserHelper()
    {
        return activeUserHelper;
    }

    public void setActiveUserHelper( ActiveUserHelper aActiveUserHelper )
    {
        activeUserHelper = aActiveUserHelper;
    }


    // persist logic
    // ---------------------------------------------------------------------------
    public void onPersistOrUpdate( PersistedObject aObject )
    {
        // set create info if missing
        if ( aObject.getCreatedOn() == null )
        {
            aObject.setCreatedById( getActiveUserId() );
            aObject.setCreatedOn( Calendar.getInstance() );
        }

        // set last modified
        aObject.setLastModifiedById( getActiveUserId() );
        aObject.setLastModifiedOn( Calendar.getInstance() );
    }

    protected long getActiveUserId()
    {
        if ( getActiveUserHelper() != null )
        {
            return getActiveUserHelper().getActiveUserId();
        }

        return -1;
    }
}
