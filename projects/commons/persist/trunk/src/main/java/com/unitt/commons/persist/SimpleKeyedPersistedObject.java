package com.unitt.commons.persist;

import java.io.Serializable;

public interface SimpleKeyedPersistedObject<T extends Serializable> extends PersistedObject
{
    public T getId();
    public void setId( T aId );
}
