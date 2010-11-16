package com.unitt.commons.persist;


import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
public class PersistedObjectSimpleKeyImpl extends PersistedObjectImplBase implements SimpleKeyedPersistedObject
{
    private static final long serialVersionUID = 1L;

    // PersistedObject fields
    // ------------------------------------------------
    @Id
    @Column( name = "id", unique = true, nullable = false )
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long              id;


    // PersistedObject implementation
    // ------------------------------------------------
    public boolean isPersisted()
    {
        return getId() != null && getId() != 0;
    }


    // SimpleKeyedPersistedObject implementation
    // ------------------------------------------------
    public Long getId()
    {
        return id;
    }

    public void setId( Long aId )
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
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) ( id ^ ( id >>> 32 ) );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( obj.getClass() != getClass() )
        {
            return false;
        }
        PersistedObjectSimpleKeyImpl other = (PersistedObjectSimpleKeyImpl) obj;
        if ( id != other.id )
        {
            return false;
        }
        return true;
    }
}
