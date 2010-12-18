package com.unitt.commons.authorization.jpa;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.unitt.commons.authorization.Assignable;
import com.unitt.commons.authorization.AssignedPermission;
import com.unitt.commons.authorization.AuthorizationManager;
import com.unitt.commons.authorization.InsufficentPrivilegesException;
import com.unitt.commons.authorization.Permissable;
import com.unitt.commons.authorization.PermissionKey;
import com.unitt.commons.authorization.ReservedPermission;
import com.unitt.commons.authorization.jpa.PermissionDao;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
public class JpaPermissionManagerTest implements ReservedPermission
{
    public static final PermissionKey EXISTING_PERMISSION = new PermissionKey(1L, 1L, 1L, 1L);
    public static final Permissable EXISTING_PERMISSABLE = new Permissable(1L, 1L);
    public static final Assignable EXISTING_ASSIGNABLE = new Assignable(1L, 1L);
    
    @Autowired
    protected ApplicationContext   applicationContext;
    
    protected IDataSet           dataSet;
    protected IDatabaseTester    dbHelper;

    protected AuthorizationManager manager;
    protected PermissionDao    dao;

    @Before
    public void setUp() throws Exception
    {
        // setup data
        DataSource dataSource = (DataSource) applicationContext.getBean( "datasource" );
        dbHelper = new DataSourceDatabaseTester( dataSource );
        dataSet = new FlatXmlDataSet( new FileInputStream( "./src/test/resources/dbunit/JpaPermissionManagerTestData.xml" ) );
        dbHelper.setDataSet( dataSet );
        dbHelper.setTearDownOperation( DatabaseOperation.DELETE_ALL );

        // load data set
        dbHelper.onSetup();
        manager = (AuthorizationManager) applicationContext.getBean( "AuthorizationManager" );
        Assert.assertNotNull( "Missing authorization manager.", manager );
        manager.initialize();
        dao = (PermissionDao) applicationContext.getBean( "PermissionDao" );
        Assert.assertNotNull( "Missing dao.", dao );
    }

    @After
    public void tearDown() throws Exception
    {
        // clear data set
        dbHelper.onTearDown();
        
        manager = null;
        dao = null;
        dataSet = null;
        dbHelper = null;
    }

    @Test
    public void testHasPermission()
    {
        Assert.assertTrue( "Did not find existing permission", manager.hasPermission( PERMISSION_CHPERMS, EXISTING_PERMISSABLE, Arrays.asList( new Assignable[] { EXISTING_ASSIGNABLE } ) ) );
        Assert.assertTrue( "Found non-existent permission", !manager.hasPermission( PERMISSION_READ, EXISTING_PERMISSABLE, Arrays.asList( new Assignable[] { EXISTING_ASSIGNABLE } ) ) );
    }

    @Test
    public void testChangePermission()
    {
        Permissable permissable = new Permissable(1L, 1L);
        Assignable assignable = new Assignable(1L, 1L);
        List<Assignable> assignables = new ArrayList<Assignable>();
        assignables.add(assignable);
        
        //add permission
        try
        {
            manager.applyPermission( assignables, PERMISSION_READ, true, permissable, assignables );
        }
        catch ( InsufficentPrivilegesException e )
        {
            Assert.fail("Did not have sufficient privileges.");
        }
        Assert.assertTrue( "Did not find new permission", manager.hasPermission( PERMISSION_READ, permissable, assignables));
        
        //remove permission
        try
        {
            manager.applyPermission( assignables, PERMISSION_READ, false, permissable, assignables );
        }
        catch ( InsufficentPrivilegesException e )
        {
            Assert.fail("Did not have sufficient privileges.");
        }
        Assert.assertTrue( "Did not remove new permission", !manager.hasPermission( PERMISSION_READ, permissable, assignables));
    }
    
    @Test
    public void testGetPermissions()
    {
        //verify non-specific perms
        List<AssignedPermission> permissions = manager.getPermissions( EXISTING_PERMISSABLE );
        Assert.assertEquals("Did not find the correct number of permissions.", 1, permissions.size());
        
        //verify specific perms
        permissions = manager.getPermissions( PERMISSION_CHPERMS, EXISTING_PERMISSABLE );
        Assert.assertEquals("Did not find the correct number of permissions.", 1, permissions.size());
        
        //verify missing specific perms
        permissions = manager.getPermissions( PERMISSION_DELETE, EXISTING_PERMISSABLE );
        Assert.assertEquals("Did not find the correct number of permissions.", 0, permissions.size());
    }
    
    @Test
    public void testRemovePermissions()
    {
        manager.removeAllPermissions( Arrays.asList( new Assignable[] { EXISTING_ASSIGNABLE } ), EXISTING_ASSIGNABLE );
        Assert.assertTrue("Did not remove all permissions.", dao.findAll().isEmpty());
    }
}
