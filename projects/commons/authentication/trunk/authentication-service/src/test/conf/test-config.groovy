/*
 * Configuration file for integration test cases
 */
ITAuthenticationDeployTest {
    groups = "AuthenticationTest"
    numCybernodes = 1
    numMonitors = 1
    //numLookups = 1
    opstring = '../src/main/opstring/authentication.groovy'
    autoDeploy = true
    //harvest = true
}

