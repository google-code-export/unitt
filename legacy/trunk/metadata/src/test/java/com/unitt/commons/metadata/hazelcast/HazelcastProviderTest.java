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
package com.unitt.commons.metadata.hazelcast;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.unitt.commons.metadata.Metadata;
import com.unitt.commons.metadata.MetadataManager;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
public class HazelcastProviderTest
{
    @Autowired
    protected ApplicationContext applicationContext;
    
    protected MetadataManager manager;
    protected MockMetadataDao dao;

    @Before
    public void setUp() throws Exception
    {
        manager = (MetadataManager) applicationContext.getBean( "MetadataService" );
        dao = (MockMetadataDao) applicationContext.getBean( "MetadataDao" );
    }

    @After
    public void tearDown() throws Exception
    {
        manager = null;
        dao = null;
    }

    @Test
    public void testFinds()
    {
        //find by id
        Metadata metadata = manager.getMetadata( MockMetadataDao.INITIAL_ID );
        Assert.assertNotNull( "Did not find initial metadata", metadata );
        Assert.assertEquals("Did not load id correctly.", MockMetadataDao.INITIAL_ID, metadata.getId());
        Assert.assertEquals("Did not load type correctly.", MockMetadataDao.INITIAL_TYPE, metadata.getType());
        
        //find by type
        metadata = manager.getMetadata( MockMetadataDao.INITIAL_TYPE );
        Assert.assertNotNull( "Did not find initial metadata", metadata );
        Assert.assertEquals("Did not load id correctly.", MockMetadataDao.INITIAL_ID, metadata.getId());
        Assert.assertEquals("Did not load type correctly.", MockMetadataDao.INITIAL_TYPE, metadata.getType());
    }
    
    @Test
    public void testSaves()
    {
        String newDescription = "My New Description";
        
        //test update
        MockMetadata metadata = (MockMetadata) manager.getMetadata( MockMetadataDao.INITIAL_ID );
        metadata.setDescription( newDescription );
        manager.putMetadata( metadata );
        
        //verify update by type and id
        metadata = (MockMetadata) manager.getMetadata( MockMetadataDao.INITIAL_ID );
        Assert.assertNotNull( "Did not find changed metadata by id", metadata );
        Assert.assertEquals("Did not set description correctly in ids.", newDescription, metadata.getDescription());
        metadata = (MockMetadata) manager.getMetadata( MockMetadataDao.INITIAL_TYPE );
        Assert.assertNotNull( "Did not find changed metadata by type", metadata );
        Assert.assertEquals("Did not set description correctly in ids.", newDescription, metadata.getDescription());
        
        //test insert
        String type = "testInsertName";
        String description = "testInsertDesc";
        metadata = new MockMetadata(null, type, null, description);
        metadata = (MockMetadata) manager.putMetadata( metadata );
        
        //verify insert
        Long id = metadata.getId();
        Assert.assertNotNull( "Did not find inserted metadata", metadata );
        Assert.assertNotNull("Did not insert id correctly.", id);
        Assert.assertEquals("Did not insert type correctly.", type, metadata.getType());
        Assert.assertEquals("Did not insert description correctly.", description, metadata.getDescription());

        //verify insert by type and id
        metadata = (MockMetadata) manager.getMetadata( id );
        Assert.assertEquals("Did not load id correctly in ids.", id, metadata.getId());
        Assert.assertEquals("Did not load type correctly in ids.", type, metadata.getType());
        Assert.assertEquals("Did not load description correctly in ids.", description, metadata.getDescription());
        metadata = (MockMetadata) manager.getMetadata( type );
        Assert.assertEquals("Did not load id correctly in types.", id, metadata.getId());
        Assert.assertEquals("Did not load type correctly in types.", type, metadata.getType());
        Assert.assertEquals("Did not load description correctly in types.", description, metadata.getDescription());
    }
    
    @Ignore
    public void testDeletes()
    {
        //@todo: test deletes
    }
}
