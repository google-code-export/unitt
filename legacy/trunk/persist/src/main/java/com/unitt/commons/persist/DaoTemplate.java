package com.unitt.commons.persist;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;


public class DaoTemplate<D extends PersistedObject>
{
    private Class<?>      domainClass;
    private EntityManager entityManager;


    // getters & setters
    // ------------------------------------------------
    @PersistenceContext
    public void setEntityManager( EntityManager aEntityManager )
    {
        entityManager = aEntityManager;
    }

    protected EntityManager getEntityManager()
    {
        return entityManager;
    }

    protected Class<?> getDomainClass()
    {
        return domainClass;
    }

    protected void setDomainClass( Class<?> aDomainClass )
    {
        domainClass = aDomainClass;
    }
    

    // transaction logic
    // ------------------------------------------------
    public EntityTransaction getTransaction()
    {
        if ( getEntityManager() == null )
        {
            throw new IllegalStateException( "Missing Entity Manager" );
        }
        return getEntityManager().getTransaction();
    }
    

    // dao logic
    // ------------------------------------------------
    @SuppressWarnings( "unchecked" )
    public List<D> findAll()
    {
        return (List<D>) getEntityManager().createQuery( "from " + getDomainClass().getSimpleName() ).getResultList();
    }


    public D save( D aObject )
    {
        if ( !aObject.isPersisted() )
        {
            getEntityManager().persist( aObject );
            return aObject;
        }
        else
        {
            return getEntityManager().merge( aObject );
        }
    }

    @Transactional
    public D safeSave( D aObject )
    {
        return save( aObject );
    }


    public void delete( D aObject )
    {
        getEntityManager().remove( aObject );
    }

    @Transactional
    public void safeDelete( D aObject )
    {
        delete( aObject );
    }


    public void refresh( D aObject )
    {
        getEntityManager().refresh( aObject );
    }


    public void flush()
    {
        getEntityManager().flush();
    }
}
