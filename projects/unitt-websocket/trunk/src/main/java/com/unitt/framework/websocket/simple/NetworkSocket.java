package com.unitt.framework.websocket.simple;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unitt.framework.websocket.NetworkSocketFacade;
import com.unitt.framework.websocket.NetworkSocketObserver;
import com.unitt.framework.websocket.WebSocketConnectConfig;


public class NetworkSocket implements NetworkSocketFacade, Runnable
{
    private static Logger            logger = LoggerFactory.getLogger( NetworkSocket.class );

    protected OutputStream           output;
    protected InputStream            input;
    protected Socket                 socket;
    protected WebSocketConnectConfig config;
    protected NetworkSocketObserver  observer;
    protected boolean isRunning = false;


    // getters & setters
    // ---------------------------------------------------------------------------
    public WebSocketConnectConfig getConfig()
    {
        return config;
    }

    public void setConfig( WebSocketConnectConfig aConfig )
    {
        config = aConfig;
    }


    // network facade logic
    // ---------------------------------------------------------------------------
    // @todo: cleanup socket & streams on failure
    protected void createSocket() throws java.io.IOException, NoSuchAlgorithmException
    {
        if ( getConfig().isSecure() )
        {
            // handle secure
            try
            {

                // handle config
                if ( getConfig().hasProxy() )
                {
                    // init
                    socket = new Socket( getConfig().getProxyHost(), getConfig().getProxyPort() );
                    output = socket.getOutputStream();
                    input = socket.getInputStream();

                    // write connect headers
                    output.write( getProxyConnectString( getConfig().getUrl().getHost(), getConfig().getUrl().getPort(), getConfig().getWebSocketVersion().getSpecVersionValue() ).getBytes() );
                    output.flush();

                    // verify we could connect
                    BufferedReader reader = new BufferedReader( new InputStreamReader( input ) );
                    String line = reader.readLine();
                    boolean isSuccess = false;
                    StringBuffer exMsg = new StringBuffer( "Cannot connect through proxy: [" );
                    while ( line != null && !isSuccess )
                    {
                        exMsg.append( line );
                        isSuccess = line.indexOf( "200" ) >= 0;
                        line = reader.readLine();
                    }
                    if ( !isSuccess )
                    {
                        exMsg.append( "]" );
                        throw new IllegalStateException( exMsg.toString() );
                    }
                }
                else
                {
                    socket = new Socket( getConfig().getUrl().getHost(), getConfig().getUrl().getPort() );
                    output = socket.getOutputStream();
                    input = socket.getInputStream();
                }

                InetSocketAddress remoteAddress = (InetSocketAddress) socket.getRemoteSocketAddress();

                // handle SSL
                String trustStore = System.getProperty( "javax.net.ssl.trustStore" );
                String trustStorePassword = System.getProperty( "javax.net.ssl.trustStorePassword" );

                // if we haven't specified truststore info - use defaults
                if ( trustStore == null && trustStorePassword == null )
                {
                    SocketFactory socketFactory = SSLSocketFactory.getDefault();
                    socket = socketFactory.createSocket( remoteAddress.getHostName(), socket.getPort() );
                    output = socket.getOutputStream();
                    input = socket.getInputStream();
                    return;
                }

                // validate truststore params
                if ( trustStore == null || trustStore.trim().length() == 0 )
                {
                    throw new IllegalArgumentException( "Truststore file missing." );
                }
                if ( trustStorePassword == null || trustStorePassword.trim().length() == 0 )
                {
                    throw new IllegalArgumentException( "Truststore password missing." );
                }

                // Key store for your own private key and signing certificates
                char[] trustStorePassphrase = trustStorePassword.toCharArray();
                KeyStore ks = KeyStore.getInstance( "JKS" );
                ks.load( new FileInputStream( trustStore ), trustStorePassphrase );
                KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
                kmf.init( ks, trustStorePassphrase );
                TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
                tmf.init( ks );

                // get SSL context & factory
                SSLContext sslContext = SSLContext.getInstance( "TLS" );
                sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );
                SSLSocketFactory sf = sslContext.getSocketFactory();

                // secure created socket with ssl socket
                SSLSocket sslSocket = (SSLSocket) ( sf.createSocket( socket, remoteAddress.getHostName(), socket.getPort(), true ) );
                sslSocket.setUseClientMode( true );
                socket = sslSocket;
                output = socket.getOutputStream();
                input = socket.getInputStream();
                return;
            }
            catch ( UnrecoverableKeyException e )
            {
                throw new IllegalStateException( "Could not create socket.", e );
            }
            catch ( KeyManagementException e )
            {
                throw new IllegalStateException( "Could not create socket.", e );
            }
            catch ( KeyStoreException e )
            {
                throw new IllegalStateException( "Could not create socket.", e );
            }
            catch ( CertificateException e )
            {
                throw new IllegalStateException( "Could not create socket.", e );
            }
        }
        else
        {
            // handle non-secure
            if ( getConfig().hasProxy() )
            {
                // proxy servers really dont play well with non-ssl websockets
                throw new IllegalArgumentException( "Using a proxy with non-secure web sockets is not supported." );
            }
            else
            {
                socket = new Socket( getConfig().getUrl().getHost(), getConfig().getUrl().getPort() );
                output = socket.getOutputStream();
                input = socket.getInputStream();
                return;
            }
        }
    }

    protected String getProxyConnectString( String aHost, int aPort, String aWebSocketVersion )
    {
        return "CONNECT " + aHost + ":" + aPort + " HTTP/1.1\r\nHost: " + aHost + ":" + aPort + "\r\nUser-Agent: ws/" + aWebSocketVersion + "\r\nProxy-Connection: keep-alive\r\n\r\n";
    }

    public void connect( WebSocketConnectConfig aConfig )
    {
        try
        {
            createSocket();
            isRunning = true;
            Thread bgThread = new Thread(this);
            bgThread.start();
        }
        catch ( NoSuchAlgorithmException e )
        {
            logger.error( "Could not connect.", e );
        }
        catch ( IOException e )
        {
            logger.error( "Could not connect.", e );
        }

        // notify observer
        if ( observer != null )
        {
            observer.onConnect();
        }
    }

    public void disconnect()
    {
        Exception exception = null;

        // disconnect
        try
        {
            isRunning = false;
            socket.close();
        }
        catch ( IOException e )
        {
            logger.error( "Error occurred while closing the socket.", e );
            exception = e;
        }

        // notify observer
        if ( observer != null )
        {
            observer.onDisconnect( exception );
        }
    }

    public void setObserver( NetworkSocketObserver aObserver )
    {
        observer = aObserver;
    }

    public void upgrade()
    {
        // nothing is required for this implementation
    }

    public void write( byte[] aBytes ) throws IOException
    {
        output.write( aBytes );
        output.flush();
    }
    
        
    // runnable logic
    // ---------------------------------------------------------------------------
    public void run()
    {
        while (isRunning)
        {
            try
            {
                int length = input.available();
                if (length > 0)
                {
                    byte[] bytesIn = new byte[length];
                    int read = input.read( bytesIn, 0, length );
                    if (read < 0)
                    {
                        isRunning = false;
                        //@todo: do we disconnect?
                    }
                    else
                    {
                        observer.onReceivedData( bytesIn );
                    }
                }
                else
                {
                    try
                    {
                        Thread.sleep( 10 );
                    }
                    catch ( InterruptedException e )
                    {
                        //do nothing, keep going
                    }
                }
            }
            catch ( IOException e )
            {
                logger.error( "An error occurred while reading data", e );
            }
        }
    }
    
    
}
