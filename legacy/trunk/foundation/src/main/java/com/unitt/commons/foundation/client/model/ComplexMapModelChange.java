/*
 * Copyright 2009 UnitT Software Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitt.commons.foundation.client.model;

public class ComplexMapModelChange<K,L>
{
    protected K key;
    protected L value;
    
    
    // constructors
    // ---------------------------------------------------------------------------
    public ComplexMapModelChange()
    {
        //default
    }
    
    public ComplexMapModelChange( K aKey, L aValue )
    {
        super();
        key = aKey;
        value = aValue;
    }
    
    
    // getters & setters
    // ---------------------------------------------------------------------------
    public K getKey()
    {
        return key;
    }
   
    public void setKey( K aKey )
    {
        key = aKey;
    }
    public L getValue()
    {
        return value;
    }
    public void setValue( L aValue )
    {
        value = aValue;
    }


    // Object overrides
    // ------------------------------------------------
    @Override
    public String toString()
    {
        return "ComplexMapModelChange [key=" + key + ", value=" + value + "]";
    }
}
