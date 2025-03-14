/*
 * Copyright (C) 2013 4th Line GmbH, Switzerland
 *
 * The contents of this file are subject to the terms of either the GNU
 * Lesser General Public License Version 2 or later ("LGPL") or the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.fourthline.cling.transport.impl;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * The SUNW morons restrict the JDK handlers to GET/POST/etc for "security" reasons.
 * <p>
 * They do not understand HTTP. This is the hilarious comment in their source:
 * </p>
 * <p>
 * "This restriction will prevent people from using this class to experiment w/ new
 * HTTP methods using java.  But it should be placed for security - the request String
 * could be arbitrarily long."
 * </p>
 *
 * @author Christian Bauer
 */
public class FixedSunURLStreamHandler implements URLStreamHandlerFactory {

    final private static Logger log = Logger.getLogger(FixedSunURLStreamHandler.class.getName());

    public URLStreamHandler createURLStreamHandler(String protocol) {
        log.fine("Creating new URLStreamHandler for protocol: " + protocol);
        if ("http".equals(protocol)) {
            return new MyHttpURLStreamHandler();
        } else {
            return null;
        }
    }

    // 自定义的 HTTP 协议处理器
    private class MyHttpURLStreamHandler extends URLStreamHandler {
        @Override
        protected URLConnection openConnection(URL u) throws IOException {
            return new UpnpURLConnection(u);
        }

        @Override
        protected URLConnection openConnection(URL u, Proxy p) throws IOException {
            // 目前暂不支持代理，直接忽略 Proxy 参数
            return new UpnpURLConnection(u);
        }
    }

    static class UpnpURLConnection extends HttpURLConnection {

        private static final List<String> ALLOWED_METHODS = Arrays.asList(
                "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE",
                "SUBSCRIBE", "UNSUBSCRIBE", "NOTIFY"
        );

        private final HttpClient httpClient;
        private ByteArrayOutputStream requestBody;
        private HttpResponse<byte[]> response;

        protected UpnpURLConnection(URL url) throws IOException {
            super(url);
            this.httpClient = HttpClient.newHttpClient();
            this.requestBody = new ByteArrayOutputStream();
        }
        @Override
        public void connect() throws IOException {
            if (connected) {
                return;
            }
            try {
                HttpRequest.Builder builder = HttpRequest.newBuilder()
                        .uri(url.toURI());
                // 如果请求方法支持且有请求体，则附加请求体，否则发送空体请求
                if (("PUT".equalsIgnoreCase(method) ||
                        "POST".equalsIgnoreCase(method) ||
                        "NOTIFY".equalsIgnoreCase(method))
                        && requestBody.size() > 0) {
                    builder.method(method, HttpRequest.BodyPublishers.ofByteArray(requestBody.toByteArray()));
                } else {
                    builder.method(method, HttpRequest.BodyPublishers.noBody());
                }
                HttpRequest request = builder.build();
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
                responseCode = response.statusCode();
                connected = true;
            } catch (Exception e) {
                throw new IOException("请求发送失败", e);
            }
        }

        /**
         * 获取响应的输入流，若未连接则先发起连接
         */
        @Override
        public InputStream getInputStream() throws IOException {
            if (!connected) {
                connect();
            }
            if (response == null) {
                throw new IOException("无响应数据");
            }
            return new ByteArrayInputStream(response.body());
        }

        /**
         * 获取输出流，仅支持 PUT、POST、NOTIFY 方法
         */
        @Override
        public OutputStream getOutputStream() throws IOException {
            if (!("PUT".equalsIgnoreCase(method) ||
                    "POST".equalsIgnoreCase(method) ||
                    "NOTIFY".equalsIgnoreCase(method))) {
                throw new ProtocolException("方法 " + method + " 不支持输出");
            }
            return requestBody;
        }

        /**
         * 设置请求方法时进行合法性校验
         */
        @Override
        public void setRequestMethod(String method) throws ProtocolException {
            if (connected) {
                throw new ProtocolException("连接后无法修改请求方法");
            }
            if (!ALLOWED_METHODS.contains(method.toUpperCase())) {
                throw new ProtocolException("无效的 UPnP HTTP 方法: " + method);
            }
            this.method = method.toUpperCase();
        }

        /*public synchronized OutputStream getOutputStream() throws IOException {
            OutputStream os;
            String savedMethod = method;
            // see if the method supports output
            if (method.equals("PUT") || method.equals("POST") || method.equals("NOTIFY")) {
                // fake the method so the superclass method sets its instance variables
                method = "PUT";
            } else {
                // use any method that doesn't support output, an exception will be
                // raised by the superclass
                method = "GET";
            }
            os = super.getOutputStream();
            method = savedMethod;
            return os;
        }*/

        /*public void setRequestMethod(String method) throws ProtocolException {
            if (connected) {
                throw new ProtocolException("Cannot reset method once connected");
            }
            for (String m : methods) {
                if (m.equals(method)) {
                    this.method = method;
                    return;
                }
            }
            throw new ProtocolException("Invalid UPnP HTTP method: " + method);
        }*/

        @Override
        public void disconnect() {
// 本例中 HttpClient 不支持断开连接，因此这里只是标记状态
            connected = false;
        }

        @Override
        public boolean usingProxy() {
            return false;
        }
    }
}
