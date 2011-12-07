package com.unitt.commons.persist;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
public abstract class PersistedObjectSimpleKeyImpl <T extends Serializable> extends PersistedObjectImplBase implements SimpleKeyedPersistedObject<T>
{
    private static final long serialVersionUID = 1L;

    // PersistedObject fields
    // ------------------------------------------------
    @Id
    @Column( name = "id", unique = true, nullable = false )
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private T              id;


    // PersistedObject implementation
    // ------------------------------------------------
    public abstract boolean isPersisted();


    // SimpleKeyedPersistedObject implementation
    // ------------------------------------------------
    public T getId()
    {
        return id;
    }

    public void setId( T aId )
    {
        id = aId;
    }

	// java.lang.Object overrides
    // ------------------------------------------------
    @Override
    public String toString()
    {
        return "PersistedObjectImplBase [id=" + id + ", lastModifiedOn=" + getLastModifiedOn() + ", lastModifiedById=" + getLastModifiedById() + ", createdOn=" + getCreatedOn() + ", createdById=" + getCreatedById() + "]";
    }

	@Override
	public abstract int hashCode();


	@Override
	public abstract boolean equals(Object aObj);
    
    
    
}
