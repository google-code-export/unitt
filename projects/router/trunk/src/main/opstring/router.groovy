deployment(name:'Router') {
    /* Configuration for the discovery group that the service should join. */
    groups 'unitt'

    /* Declares the artifacts required for deployment. Note the 'dl'
     * classifier used for the 'download' jar */
    artifact id:'service', 'com.unitt.framework.router:router-service:1.0.0-SNAPSHOT'
    artifact id:'service-dl', 'com.unitt.framework.router:router-api:1.0.0-SNAPSHOT'

    /*
     * Declare the service to be deployed. The number of instances deployed
     * defaults to 1. If you require > 1 instances change as needed
     */
    spring(name: 'Router', config: 'routerContext.xml' ) {
        interfaces {
            classes 'com.unitt.router.Router'
            artifact ref:'service-dl'
        }
        implementation(class:'com.unitt.router.RouterImpl') {
            artifact ref:'service'
        }
        maintain 2
    }
}
