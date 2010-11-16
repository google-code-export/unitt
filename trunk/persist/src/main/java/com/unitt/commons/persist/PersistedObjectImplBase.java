package com.unitt.commons.persist;


import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@MappedSuperclass
public abstract class PersistedObjectImplBase implements PersistedObject
{
    private static final long serialVersionUID = 1L;

    // PersistedObject fields
    // ------------------------------------------------
    @Column( name = "createdById" )
    private Long              createdById;

    @Column( name = "createdOn" )
    @Temporal( TemporalType.TIMESTAMP )
    private Calendar          createdOn;

    @Column( name = "lastModifiedById" )
    private Long              lastModifiedById;

    @Column( name = "lastModifiedOn" )
    @Temporal( TemporalType.TIMESTAMP )
    private Calendar          lastModifiedOn;

    @Column( name = "comment" )
    private String            comment;


    // PersistedObject implementation
    // ------------------------------------------------
    @PrePersist
    @PreUpdate
    public void applyChangeInfo()
    {
        PersistHelper.instance().onPersistOrUpdate( this );
    }

    public Long getCreatedById()
    {
        return createdById;
    }

    public void setCreatedById( Long aCreatedById )
    {
        createdById = aCreatedById;
    }

    public Calendar getCreatedOn()
    {
        return createdOn;
    }

    public void setCreatedOn( Calendar aCreatedOn )
    {
        createdOn = aCreatedOn;
    }

    public Long getLastModifiedById()
    {
        return lastModifiedById;
    }

    public void setLastModifiedById( Long aLastModifiedById )
    {
        lastModifiedById = aLastModifiedById;
    }

    public Calendar getLastModifiedOn()
    {
        return lastModifiedOn;
    }

    public void setLastModifiedOn( Calendar aLastModifiedOn )
    {
        lastModifiedOn = aLastModifiedOn;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment( String aComment )
    {
        comment = aComment;
    }
}
