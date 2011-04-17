deployment(name:'Authorization') 
{
    groups 'unitt'

    artifact id:'service', 'com.unitt.commons:authorization:1.0.2'
    artifact id:'service-dl', 'com.unitt.commons:authorization:dl:1.0.2'

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