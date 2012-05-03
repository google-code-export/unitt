/*
 * Configuration file for integration test cases
 */
ITRouterDeployTest {
    groups = "RouterTest"
    numCybernodes = 1
    numMonitors = 1
    //numLookups = 1
    opstring = '../src/main/opstring/router.groovy'
    autoDeploy = true
    //harvest = true
}

