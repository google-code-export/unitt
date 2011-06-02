package com.unitt.commons.session.hazelcast;


import java.util.Collection;
import java.util.Map;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;


public class ExpireSessionsWorker implements Runnable
{
    protected HazelcastProvider provider;
    protected boolean           isRunning;
    protected long              timeToSleep;


    // getters & setters
    // ---------------------------------------------------------------------------
    public HazelcastProvider getProvider()
    {
        return provider;
    }

    public void setProvider( HazelcastProvider aProvider )
    {
        provider = aProvider;
    }

    public boolean isRunning()
    {
        return isRunning;
    }

    public void setRunning( boolean aIsRunning )
    {
        isRunning = aIsRunning;
    }

    public long getTimeToSleep()
    {
        return timeToSleep;
    }

    public void setTimeToSleep( long aTimeToSleep )
    {
        timeToSleep = aTimeToSleep;
    }


    // thread logic
    // ---------------------------------------------------------------------------
    public void run()
    {
        while ( isRunning() )
        {
            // clear out expired sessions
            try
            {
                IMap<String, SessionExpiration> map = Hazelcast.getDefaultInstance().getMap( HazelcastProvider.KEY_EXPIRATION );
                long currentTime = System.currentTimeMillis();
                Collection<Map.Entry<String, SessionExpiration>> expired = map.entrySet( new SqlPredicate( "expiresAt > " + currentTime ) );
                for ( Map.Entry<String, SessionExpiration> entry : expired )
                {
                    provider.close( entry.getKey() );
                }
            }
            catch ( Exception e )
            {
                // @todo: log error
                e.printStackTrace();
            }

            // sleep and run again
            try
            {
                Thread.sleep( getTimeToSleep() );
            }
            catch ( Exception e )
            {
                // do nothing
            }
        }
    }
}
