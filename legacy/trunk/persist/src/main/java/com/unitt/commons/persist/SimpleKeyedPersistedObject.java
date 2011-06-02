package com.unitt.commons.persist;

public interface SimpleKeyedPersistedObject extends PersistedObject
{
    public Long getId();
    public void setId( Long aId );
}
