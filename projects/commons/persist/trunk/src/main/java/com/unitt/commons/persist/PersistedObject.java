package com.unitt.commons.persist;


import java.io.Serializable;
import java.util.Calendar;



public interface PersistedObject extends Serializable
{
    public void applyChangeInfo();
    
    public Long getCreatedById();
    public void setCreatedById( Long aCreatedById );
    
    public Calendar getCreatedOn();
    public void setCreatedOn( Calendar aCreatedOn );

    public Long getLastModifiedById();
    public void setLastModifiedById( Long aLastModifiedById );
    
    public Calendar getLastModifiedOn();
    public void setLastModifiedOn( Calendar aLastModifiedOn );
    
    public String getComment();
    public void setComment( String aComment );
    
    public boolean isPersisted();
}
