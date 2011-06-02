package com.unitt.commons.persist;


import org.springframework.transaction.annotation.Transactional;


public class SimpleKeyedDaoTemplate<D extends SimpleKeyedPersistedObject> extends DaoTemplate<D>
{
    // dao logic
    // ------------------------------------------------
    @SuppressWarnings( "unchecked" )
    public D find( long aId )
    {
        return (D) getEntityManager().find( getDomainClass(), aId );
    }
    
    public String formatLikeValue(String value){
        if (value == null)
        {
            return "%";
        }
    	String returnValue = value;
    	if(value.contains("*")){ 
    		returnValue = returnValue.replace("*", "%");
    	} else if(value.contains("%")){
    		//do nothing
    	} else {
    		returnValue = "%"+returnValue+"%";
    	}
    	return returnValue;
    }

    @SuppressWarnings( "unchecked" )
    public D find( String aId )
    {
        try
        {
            return (D) getEntityManager().find( getDomainClass(), Long.parseLong( aId ) );
        }
        catch ( NumberFormatException e )
        {
            // do nothing, add to log later
        }

        return null;
    }

    public void delete( long aId )
    {
        getEntityManager().remove( find( aId ) );
    }

    @Transactional
    public void safeDelete( long aId )
    {
        delete( aId );
    }


    public void delete( String aId )
    {
        getEntityManager().remove( find( aId ) );
    }

    @Transactional
    public void safeDelete( String aId )
    {
        delete( aId );
    }
}
