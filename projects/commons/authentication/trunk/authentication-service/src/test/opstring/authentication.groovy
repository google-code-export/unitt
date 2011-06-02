deployment(name:'Authentication') 
{
    groups 'unitt'

    artifact id:'service', 'com.unitt.commons:authentication:2.0.0-SNAPSHOT'
    artifact id:'service-dl', 'com.unitt.commons:authentication:dl:2.0.0-SNAPSHOT'

    spring(name: 'Authentication', config:'com/unitt/commons/authentication/AuthenticationTest-context.xml') 
    {
        interfaces 
        {
            classes 'com.unitt.commons.authentication.Authentication'
            artifact ref:'service-dl'
        }
        implementation(class:'com.unitt.commons.authentication.AuthenticationImpl') 
        {
            artifact ref:'service'
        }
        maintain 1
    }
}