package com.unitt.commons.persist;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class PersistedObjectStringKey extends PersistedObjectSimpleKeyImpl<String>
{
	private static final long	serialVersionUID	= 1L;
	

	// PersistedObject implementation
    // ------------------------------------------------
	@Override
	public boolean isPersisted()
	{
		return getId() != null && getId().length() > 0;
	}

	// java.lang.Object overrides
    // ------------------------------------------------
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		if (getId() == null)
		{
			result = prime * result + 0;
		}
		else
		{
			result = getId().hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersistedObjectStringKey other = (PersistedObjectStringKey) obj;
		if (getId() == null)
		{
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}
