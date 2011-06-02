deployment(name:'Authorization') 
{
    groups 'unitt'

    artifact id:'service', 'com.unitt.commons:authorization:2.0.0-SNAPSHOT'
    artifact id:'service-dl', 'com.unitt.commons:authorization:dl:2.0.0-SNAPSHOT'

    spring(name: 'Authorization', config:'com/unitt/commons/authorization/hazelcast/HazelcastPermissionManagerTest-context.xml') 
    {
        interfaces 
        {
            classes 'com.unitt.commons.authorization.Authorization'
            artifact ref:'service-dl'
        }
        implementation(class:'com.unitt.commons.authorization.AuthorizationImpl') 
        {
            artifact ref:'service'
        }
        maintain 1
    }
}
