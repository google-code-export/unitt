package com.unitt.commons.persist;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class PersistedObjectLongKey extends PersistedObjectSimpleKeyImpl<Long>
{
	private static final long	serialVersionUID	= 1L;

	// PersistedObject implementation
    // ------------------------------------------------
	@Override
	public boolean isPersisted()
	{
		return getId() != null && getId() != 0;
	}

	// java.lang.Object overrides
    // ------------------------------------------------
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		PersistedObjectLongKey other = (PersistedObjectLongKey) obj;
		if (getId() == null)
		{
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
}
