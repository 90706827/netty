package com.jangni.http;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

/**
 * Author ZhangGuoQiang
 * Date: 2018/6/28/028
 * Time: 16:41
 * Description:
 */
public class HhapClient implements IHhapClient {

    private String host;
    private int port;
    private int connectTimeout = 3000;
    private int connectRequestTimeout = 5;
    private int socketTimeout = 60000;
    private IMsgContextDecoder msgContextDecoder;
    private IMsgContextEncoder msgContextEncoder;
    Logger logger = LoggerFactory.getLogger("flow");

    public HhapClient(String host, int port, int timeout, IMsgContextDecoder msgContextDecoder, IMsgContextEncoder msgContextEncoder) {
        this.host = host;
        this.port = port;
        this.socketTimeout = timeout;
        this.msgContextDecoder = msgContextDecoder;
        this.msgContextEncoder = msgContextEncoder;
    }

    public HhapClient(String host, int port, IMsgContextDecoder msgContextDecoder, IMsgContextEncoder msgContextEncoder) {
        this.host = host;
        this.port = port;
        this.msgContextDecoder = msgContextDecoder;
        this.msgContextEncoder = msgContextEncoder;
    }

    private RequestConfig requestConfig = RequestConfig
            .custom()
            .setConnectTimeout(connectTimeout)
            .setConnectionRequestTimeout(connectRequestTimeout)
            .setSocketTimeout(socketTimeout)
            .build();

    private CloseableHttpClient httpClient =
            HttpClients.custom()
                    .setMaxConnTotal(2048)
                    .setMaxConnPerRoute(2048)
                    .setConnectionTimeToLive(1, TimeUnit.MINUTES)
                    .setDefaultRequestConfig(requestConfig)
                    .build();


    @Override
    public void send(MsgContext msgContext) {
        String reqBody = msgContextEncoder.decode(msgContext);
        HttpPost httpPost = new HttpPost("http://" + host + ":" + port);
        httpPost.setEntity(new StringEntity(reqBody, ContentType.APPLICATION_XML.withCharset("utf-8")));
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                String respBody = EntityUtils.toString(httpResponse.getEntity());
                msgContextDecoder.decode(respBody, msgContext);
            } else {
                msgContext.setMsgCode("91");
                msgContext.setMsgText("请求失败");
            }

        } catch (SocketTimeoutException ste) {
            logger.warn("客户端未在时间[" + socketTimeout + "毫秒]内收到响应");
            ste.printStackTrace();
        } catch (HttpHostConnectException hhce) {
            logger.warn("客户端未在时间[" + connectTimeout + "毫秒]内连接到目标地址");
            hhce.printStackTrace();
        } catch (ConnectionPoolTimeoutException cpte) {
            logger.warn("客户端未在时间[" + connectRequestTimeout + "秒]内从连接池中获取到可用连接");
            cpte.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destory() {
        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
