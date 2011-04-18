deployment(name:'Authentication') 
{
    groups 'unitt'

    artifact id:'service', 'com.unitt.commons:authentication:1.0.2'
    artifact id:'service-dl', 'com.unitt.commons:authentication:dl:1.0.2'

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