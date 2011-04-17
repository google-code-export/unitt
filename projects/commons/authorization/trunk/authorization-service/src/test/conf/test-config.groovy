/*
 * Configuration file for integration test cases
 */
ITAuthorizationDeployTest {
    groups = "AuthorizationTest"
    numCybernodes = 1
    numMonitors = 1
    //numLookups = 1
    opstring = '../src/main/opstring/authorization.groovy'
    autoDeploy = true
    //harvest = true
}

