package com.unitt.framework.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * @author Josh Morris
 */
public class WebSocketClientConnection extends WebSocketConnection {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketClientConnection.class);
    protected ByteArrayOutputStream handshakeBytes = new ByteArrayOutputStream();

    // constructors
    // ---------------------------------------------------------------------------
    public WebSocketClientConnection() {
        //default
    }

    public WebSocketClientConnection(WebSocketObserver aObserver, NetworkSocketFacade aNetwork, WebSocketConnectConfig aConnectConfig) {
        super(aObserver, aNetwork, aConnectConfig);

        setHandshake(new WebSocketHandshake(aConnectConfig));
    }


    // client logic
    // ---------------------------------------------------------------------------
    @Override
    protected boolean sendWithMask() {
        return true;
    }

    @Override
    public void onReceivedData(byte[] aData) {
        if (getState() == WebSocketState.NeedsHandshake) {
            //if handshake data is munged with actual data, split it
            byte[] delimiter = "\r\n\r\n".getBytes();
            int end = WebSocketUtil.getIndexOf(aData, delimiter);
            if (end >= 0) {
                byte[] bytes = WebSocketUtil.copySubArray(aData, 0, end);
                if (handshakeBytes.size() > 0) {
                    try {
                        handshakeBytes.write(bytes);
                        bytes = handshakeBytes.toByteArray();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                handleHandshake(bytes);
                int start = end + delimiter.length;
                int length = aData.length - start;
                if (length > 0) {
                    bytes = WebSocketUtil.copySubArray(aData, start, length);
                    super.onReceivedData(bytes);
                }
            } else {
                try {
                    handshakeBytes.write(aData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onReceivedData(aData);
        }
    }

    @Override
    public void onConnect() {
        setState(WebSocketState.NeedsHandshake);
        try {
            getNetwork().write(getHandshake().getClientHandshakeBytes());
        } catch (IOException e) {
            sendErrorToObserver(e);
        }
    }

    protected boolean handleHandshake(byte[] aHandshakeBytes) {
        if (aHandshakeBytes != null && aHandshakeBytes.length > 0) {
            if (getHandshake().verifyServerHandshake(aHandshakeBytes)) {
                getNetwork().upgrade();
                setState(WebSocketState.Connected);
                sendOpenToObserver(getHandshake().getServerConfig().getSelectedProtocol(), getHandshake().getServerConfig().getSelectedExtensions());
                return true;
            } else {
                logger.warn("Bad handshake: (" + aHandshakeBytes.length + ")" + new String(aHandshakeBytes));
                setCloseMessage("Invalid Handshake");
                setState(WebSocketState.Disconnected);
                getNetwork().disconnect();
            }
        }
        return false;
    }
}
