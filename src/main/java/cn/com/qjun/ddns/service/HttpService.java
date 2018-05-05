package cn.com.qjun.ddns.service;

import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author Ryan
 * @date 2018/5/5
 */
@Service
public class HttpService {
    private static final String HTTPS = "https";
    private String authCookie;

    public String post(String url, String data) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        URL url_ = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) url_.openConnection();
        if (url.startsWith(HTTPS)) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
            //信任所有证书
            trustAllCert(httpsConn);
            //不验证域名
            httpsConn.setHostnameVerifier((host, session) -> true);
            conn = httpsConn;
        }
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setUseCaches(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        if (authCookie != null) {
            conn.setRequestProperty("Cookie", "sess_key=" + authCookie);
        }
        conn.connect();
        OutputStreamWriter out = new OutputStreamWriter(
                conn.getOutputStream());
        out.write(data);
        out.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        if (conn.getHeaderFields().get("Set-Cookie") != null) {
            authCookie = conn.getHeaderFields().get("Set-Cookie").toString().substring(10, 42);
        }
        conn.disconnect();
        return builder.toString();
    }

    private void trustAllCert(HttpsURLConnection httpsConn) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }}, new SecureRandom());
        httpsConn.setSSLSocketFactory(sslContext.getSocketFactory());
    }
}
