package io.github.asseco.pst.liberty.services

import io.github.asseco.pst.liberty.models.Profile

import javax.net.ssl.*
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

/**
 * Abstract class to setup default stuff
 *
 * @date 26/08/2020
 * @version 1.0.0
 */
abstract class AbstractDeployer implements IDeployerService {
    protected static String LIBERTY_DEPLOYMENT_PATH = '${server.output.dir}/dropins'
    protected final Profile profile

    AbstractDeployer(Profile profile, boolean acceptInsecureCertificates = false) {
        this.profile = profile

        if (acceptInsecureCertificates) {
            disableSSLVerification()
        }
    }

    private static void disableSSLVerification() {
        X509TrustManager nullTrustManager = new X509TrustManager() {
            @Override
            void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

            @Override
            void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

            @Override
            X509Certificate[] getAcceptedIssuers() {
                return null
            }
        }

        HostnameVerifier nullHostnameVerifier = new HostnameVerifier() {
            @Override
            boolean verify(String s, SSLSession sslSession) {
                return true
            }
        }
        SSLContext sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, [nullTrustManager] as TrustManager[], null)
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory())
        HttpsURLConnection.setDefaultHostnameVerifier(nullHostnameVerifier)
    }
}
