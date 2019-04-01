package com.codyy.cocolibrary;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * 自定义证书加载
 * Created by lijian on 2017/6/1.
 */

public final class CustomTrust {
    private static final String CLIENT_KET_PASSWORD = "2342342342344433";
    //    public final OkHttpClient client;

    /**
     * okhttp ssl加载
     */
    /* public CustomTrust(Context context) {
         this.context = context;
         X509TrustManager trustManager;
         SSLSocketFactory sslSocketFactory = null;

         try {
             //  trustManager = trustManagerForCertificates(trustedCertificatesInputStream());
             SSLContext sslContext = trustManagerForCertificates(trustedCertificatesInputStream()); //SSLContext.getInstance("TLS");
             sslSocketFactory = sslContext.getSocketFactory();
         } catch (GeneralSecurityException e) {
             throw new RuntimeException(e);
         } catch (IOException e) {
             e.printStackTrace();
         }
         client = new OkHttpClient.Builder()
                 .sslSocketFactory(sslSocketFactory).hostnameVerifier(new HostnameVerifier() {
                     @Override
                     public boolean verify(String hostname, SSLSession session) {
                         return true;
                     }
                 })
                 .build();

     }
 */
    public Socket getSSLSocket(InputStream inputStream) {
        Socket socket;
        try {
            SSLContext sslContext = trustManagerForCertificates(inputStream);
            socket = sslContext.getSocketFactory().createSocket();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            socket = null;
        }
        return socket;
    }

    /**
     * Returns a trust manager that trusts {@code certificates} and none other. HTTPS services whose
     * certificates have not been signed by these certificates will fail with a {@code
     * SSLHandshakeException}.
     * <p>
     * <p>This can be used to replace the host platform's built-in trusted certificates with a custom
     * set. This is useful in development where certificate authority-trusted certificates aren't
     * available. Or in production, to avoid reliance on third-party certificate authorities.
     * <p>
     * <p>See also , which can limit trusted certificates while still using
     * the host platform's built-in trust store.
     * <p>
     * <h3>Warning: Customizing Trusted Certificates is Dangerous!</h3>
     * <p>
     * <p>Relying on your own trusted certificates limits your server team's ability to update their
     * TLS certificates. By installing a specific set of trusted certificates, you take on additional
     * operational complexity and limit your ability to migrate between certificate authorities. Do
     * not use custom trusted certificates in production without the blessing of your server's TLS
     * administrator.
     */
    private SSLContext trustManagerForCertificates(InputStream in)
            throws GeneralSecurityException, IOException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Certificate oCert = certificateFactory.generateCertificate(in);
//        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
        in.close();
        /*if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }

*/        // Put the certificates a key store.
        char[] password = CLIENT_KET_PASSWORD.toCharArray(); // Any password will work.
        KeyStore keyStore = newEmptyKeyStore(password);/*
        int index = 0;
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }*/
        String certificateAlias = Integer.toString(1);
        keyStore.setCertificateEntry(certificateAlias, oCert);
        //  keyStore.load(in,CLIENT_KET_PASSWORD.toCharArray());
        // Use it to build an X509 trust manager.
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }

        SSLContext ssContext = SSLContext.getInstance("SSL");
        ssContext.init(keyManagerFactory.getKeyManagers(), trustManagers, null);
        //return (X509TrustManager) trustManagers[0];
        return ssContext;
    }

    private KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = null; // By convention, 'null' creates an empty key store.
            keyStore.load(in, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}