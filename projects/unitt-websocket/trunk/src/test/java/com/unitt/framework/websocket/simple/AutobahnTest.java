package com.unitt.framework.websocket.simple;

import com.unitt.framework.websocket.WebSocket;
import com.unitt.framework.websocket.WebSocketConnectConfig;
import com.unitt.framework.websocket.WebSocketObserver;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

public class AutobahnTest implements WebSocketObserver {
    public enum TestState {GettingTestCount, ExecutingTest, UpdatingReports}

    protected static final Charset utf8Charset = Charset.forName("UTF-8");
    private static final Logger logger = LoggerFactory.getLogger(AutobahnTest.class);

    protected WebSocket ws;
    protected WebSocketConnectConfig config;
    protected int totalTestCount;
    protected int currentTest;
    protected TestState testState;
    protected String[] testNames;
    protected String testName = "1.1.7";


    // lifecycle logic
    // ---------------------------------------------------------------------------
    @Before
    public void setup() throws URISyntaxException {
        //setup web socket
        config = new WebSocketConnectConfig();
        config.setUrl(new URI("ws://localhost:9001/getCaseCount"));
//        ws = ClientWebsocketFactory.create(config, this);
        ws = SimpleSocketFactory.create(config, this);
        testState = TestState.GettingTestCount;
        testNames = new String[] {"1.1.1", "1.1.2", "1.1.3", "1.1.4", "1.1.5", "1.1.6", "1.1.7", "1.1.8", "1.2.1", "1.2.2", "1.2.3", "1.2.4", "1.2.5", "1.2.6", "1.2.7", "1.2.8", "2.1", "2.2", "2.3", "2.4", "2.5", "2.6", "2.7", "2.8", "2.9", "2.10", "2.11", "3.1", "3.2", "3.3", "3.4", "3.5", "3.6", "3.7", "4.1.1", "4.1.2", "4.1.3", "4.1.4", "4.1.5", "4.2.1", "4.2.2", "4.2.3", "4.2.4", "4.2.5", "5.1", "5.2", "5.3", "5.4", "5.5", "5.6", "5.7", "5.8", "5.9", "5.10", "5.11", "5.12", "5.13", "5.14", "5.15", "5.16", "5.17", "5.18", "5.19", "5.20", "6.1.1", "6.1.2", "6.1.3", "6.2.1", "6.2.2", "6.2.3", "6.2.4", "6.3.1", "6.3.2", "6.4.1", "6.4.2", "6.4.3", "6.4.4", "6.5.1", "6.6.1", "6.6.2", "6.6.3", "6.6.4", "6.6.5", "6.6.6", "6.6.7", "6.6.8", "6.6.9", "6.6.10", "6.6.11", "6.7.1", "6.7.2", "6.7.3", "6.7.4", "6.8.1", "6.8.2", "6.9.1", "6.9.2", "6.9.3", "6.9.4", "6.10.1", "6.10.2", "6.10.3", "6.11.1", "6.11.2", "6.11.3", "6.11.4", "6.11.5", "6.12.1", "6.12.2", "6.12.3", "6.12.4", "6.12.5", "6.12.6", "6.12.7", "6.12.8", "6.13.1", "6.13.2", "6.13.3", "6.13.4", "6.13.5", "6.14.1", "6.14.2", "6.14.3", "6.14.4", "6.14.5", "6.14.6", "6.14.7", "6.14.8", "6.14.9", "6.14.10", "6.15.1", "6.16.1", "6.16.2", "6.16.3", "6.17.1", "6.17.2", "6.17.3", "6.17.4", "6.17.5", "6.18.1", "6.18.2", "6.18.3", "6.18.4", "6.18.5", "6.19.1", "6.19.2", "6.19.3", "6.19.4", "6.19.5", "6.20.1", "6.20.2", "6.20.3", "6.20.4", "6.20.5", "6.20.6", "6.20.7", "6.21.1", "6.21.2", "6.21.3", "6.21.4", "6.21.5", "6.21.6", "6.21.7", "6.21.8", "6.22.1", "6.22.2", "6.22.3", "6.22.4", "6.22.5", "6.22.6", "6.22.7", "6.22.8", "6.22.9", "6.22.10", "6.22.11", "6.22.12", "6.22.13", "6.22.14", "6.22.15", "6.22.16", "6.22.17", "6.22.18", "6.22.19", "6.22.20", "6.22.21", "6.22.22", "6.22.23", "6.22.24", "6.22.25", "6.22.26", "6.22.27", "6.22.28", "6.22.29", "6.22.30", "6.22.31", "6.22.32", "6.22.33", "6.22.34", "6.23.1", "7.1.1", "7.1.2", "7.1.3", "7.1.4", "7.1.5", "7.1.6", "7.3.1", "7.3.2", "7.3.3", "7.3.4", "7.3.5", "7.3.6", "7.5.1", "7.7.1", "7.7.2", "7.7.3", "7.7.4", "7.7.5", "7.7.6", "7.7.7", "7.7.8", "7.7.9", "7.7.10", "7.7.11", "7.7.12", "7.7.13", "7.9.1", "7.9.2", "7.9.3", "7.9.4", "7.9.5", "7.9.6", "7.9.7", "7.9.8", "7.9.9", "7.9.10", "7.9.11", "7.9.12", "7.9.13", "7.13.1", "7.13.2", "9.1.1", "9.1.2", "9.1.3", "9.1.4", "9.1.5", "9.1.6", "9.2.1", "9.2.2", "9.2.3", "9.2.4", "9.2.5", "9.2.6", "9.3.1", "9.3.2", "9.3.3", "9.3.4", "9.3.5", "9.3.6", "9.3.7", "9.3.8", "9.3.9", "9.4.1", "9.4.2", "9.4.3", "9.4.4", "9.4.5", "9.4.6", "9.4.7", "9.4.8", "9.4.9", "9.5.1", "9.5.2", "9.5.3", "9.5.4", "9.5.5", "9.5.6", "9.6.1", "9.6.2", "9.6.3", "9.6.4", "9.6.5", "9.6.6", "9.7.1", "9.7.2", "9.7.3", "9.7.4", "9.7.5", "9.7.6", "9.8.1", "9.8.2", "9.8.3", "9.8.4", "9.8.5", "9.8.6", "10.1.1"};
    }

    @After
    public void tearDown() {
        config = null;
        ws = null;
    }


    // test logic
    // ---------------------------------------------------------------------------
    @Test
    public void testAllCases() {
        //open web socket
        ws.open();

        //keep running until we are done
        while (currentTest <= totalTestCount) {
            pauseTest(5);
        }
    }

//    @Test
    public void testNamedCase() {
        //setup web socket
        testState = TestState.ExecutingTest;
        ws = null;
        currentTest = getIndexOfTest(testName);
        Assert.assertNotNull("Did not find the test named: " + testName, currentTest >= 0);
        totalTestCount = currentTest + 1;
        runNextTest();

        //keep running until we are done
        while (currentTest <= totalTestCount) {
            pauseTest(5);
        }
    }

    protected int getIndexOfTest(String aTestName) {
        int index = 0;
        for (String name : testNames) {
            if (name.equals(aTestName)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public void runNextTest() {
        currentTest++;
        testState = TestState.ExecutingTest;
        if (currentTest <= totalTestCount) {
            if (currentTest == 1) {
                logger.info("Running first test...");
            }
            try {
                config.setUrl(new URI("ws://localhost:9001/runCase?case=" + currentTest + "&agent=UnitT"));
                if (config.getClientHeaders() != null) {
                    config.getClientHeaders().clear();
                }
//                ws = ClientWebsocketFactory.create(config, this);
                ws = SimpleSocketFactory.create(config, this);
                ws.open();
            } catch (URISyntaxException e) {
                logger.error("Error occurred on test: " + currentTest, e);
                ws.close();
            }
        }
    }

    public void runUpdate() {
        testState = TestState.UpdatingReports;
        try {
            config.setUrl(new URI("ws://localhost:9001/updateReports?agent=UnitT"));
            config.getClientHeaders().clear();
//            ws = ClientWebsocketFactory.create(config, this);
            ws = SimpleSocketFactory.create(config, this);
            ws.open();
        } catch (URISyntaxException e) {
            logger.error("Error occurred updating reports...", e);
        }
    }

    public void pauseTest(int aSecondsToPause) {
        try {
            Thread.sleep(aSecondsToPause * 1000);
        } catch (InterruptedException e) {
            //do nothing for now
        }
    }


    //web socket observer implementation
    // ---------------------------------------------------------------------------
    public void onOpen(String aProtocol, List<String> aExtensions) {
        switch (testState) {
            case GettingTestCount:
                logger.info("Fetching test count.");
                break;
            case ExecutingTest:
                logger.info("Executing test " + testNames[currentTest - 1] + " (" + currentTest + ")...");
                break;
            case UpdatingReports:
                logger.info("Updating Report...");
                break;
        }
    }

    public void onError(Exception aException) {
        if (testState == TestState.ExecutingTest) {
            logger.error("Received Error on test: " + currentTest, aException);
            ws.close();
        }
    }

    public void onClose(int aStatusCode, String aMessage, Exception aException) {
        if (aException != null) {
            logger.error("onClose: code=" + aStatusCode + ", message=" + aMessage, aException);
        } else {
            logger.debug("onClose: code=" + aStatusCode + ", message=" + aMessage);
        }
        switch (testState) {
            case GettingTestCount:
                testState = TestState.ExecutingTest;
                ws = null;
                runNextTest();
                break;
            case ExecutingTest:
                testState = TestState.UpdatingReports;
                ws = null;
                runUpdate();
                break;
            case UpdatingReports:
                testState = TestState.UpdatingReports;
                ws = null;
                runNextTest();
                break;
        }
    }

    public void onPong(String aMessage) {
    }

    public void onBinaryMessage(byte[] aMessage) {
        switch (testState)
        {
            case GettingTestCount:
                break;
            case ExecutingTest:
                logger.info("Echoing binary of length: " + aMessage.length);
                ws.sendMessage(aMessage);
                break;
            case UpdatingReports:
                break;
        }
    }

    public void onTextMessage(String aMessage) {
        switch (testState) {
            case GettingTestCount:
                logger.info("Found test count: " + aMessage);
                totalTestCount = Integer.parseInt(aMessage);
                break;
            case ExecutingTest:
                logger.info("Echoing text of length: " + aMessage.length());
                ws.sendMessage(aMessage);
                break;
            case UpdatingReports:
                break;
        }
    }
}
