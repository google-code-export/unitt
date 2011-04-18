import org.rioproject.config.Constants

deployment(name:'Authentication') {
    /* Configuration for the discovery group that the service should join.
     * This first checks if the org.rioproject.groups property is set, if not
     * the user name is used */
    groups System.getProperty(Constants.GROUPS_PROPERTY_NAME,
                              System.getProperty('user.name'))

    /* Declares the artifacts required for deployment. Note the 'dl'
     * classifier used for the 'download' jar */
    artifact id:'service', 'com.unitt.commons.authentication:authentication-service:1.0.2'
    artifact id:'service-dl', 'com.unitt.commons.authentication:authentication-api:1.0.2'

    /*
     * Declare the service to be deployed. The number of instances deployed
     * defaults to 1. If you require > 1 instances change as needed
     */
    service(name: 'Authentication') {
        interfaces {
            classes 'com.unitt.commons.authentication.Authentication'
            artifact ref:'service-dl'
        }
        implementation(class:'com.unitt.commons.authentication.AuthenticationImpl') {
            artifact ref:'service'
        }
        maintain 1
    }
}