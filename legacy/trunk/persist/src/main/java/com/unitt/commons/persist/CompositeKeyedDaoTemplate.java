package com.unitt.commons.persist;


import java.util.List;

import org.springframework.transaction.annotation.Transactional;


public class CompositeKeyedDaoTemplate<D extends CompositeKeyedPersistedObject, K> extends DaoTemplate<D>
{
    // dao logic
    // ------------------------------------------------
    @SuppressWarnings( "unchecked" )
    public D find( K aId )
    {
        return (D) getEntityManager().find( getDomainClass(), aId );
    }

    
    public void delete( K aId )
    {
        getEntityManager().remove( find( aId ) );
    }

    @Transactional
    public void safeDelete( K aId )
    {
        delete( aId );
    }


    public void deleteAll( List<K> aIds )
    {
        for ( K id : aIds )
        {
            delete( id );
        }
    }

    @Transactional
    public void safeDeleteAll( List<K> aIds )
    {
        deleteAll( aIds );
    }
}
