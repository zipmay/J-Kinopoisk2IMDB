package org.f0w.k2i.core.net;

import com.google.common.base.Joiner;

import java.io.*;
import java.net.*;
import java.util.*;

public class HttpRequest implements Request {
    private HttpURLConnection request;

    private HttpRequest() {}

    @Override
    public void createRequest(final String url) {
        try {
            createRequest(new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createRequest(URL url) {
        try {
            this.request = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createRequest(final String url, Map<String, String> query) {
        createRequest(makeURL(url, query));
    }

    @Override
    public void createRequest(URL url, Map<String, String> query) {
        createRequest(url.toExternalForm(), query);
    }

    @Override
    public void createRequest(URL url, String query) {
        createRequest(url.toExternalForm(), query);
    }

    @Override
    public void createRequest(String url, String query) {
        createRequest(url + query);
    }

    @Override
    public void setCookies(String cookies) {
        request.setRequestProperty("Cookie", cookies);
    }

    @Override
    public void setCookies(Map<String, String> cookies) {
        setCookies(Joiner.on(";").withKeyValueSeparator("=").join(cookies));
    }

    @Override
    public void setUserAgent(String userAgent) {
        request.setRequestProperty("User-Agent", userAgent);
    }

    @Override
    public void setPOSTData(String postData) {
        try {
            request.setRequestMethod("POST");
            request.setDoOutput(true);

            DataOutputStream stream = new DataOutputStream(request.getOutputStream());
            stream.writeBytes(postData);
            stream.flush();
            stream.close();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPOSTData(Map<String, String> postData) {
        setPOSTData(Joiner.on("&").withKeyValueSeparator("=").join(postData));
    }

    @Override
    public Response getResponse() {
        try {
            return new BasicResponse(request.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getStatusCode() {
        try {
            return request.getResponseCode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL makeURL(final String url, Map<String, String> query) {
        URL newURL = null;

        try {
            newURL = new URL(url + Joiner.on("&").withKeyValueSeparator("=").join(query));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return newURL;
    }

    public static class Builder implements Request.Builder {
        private String url;
        private String userAgent;

        private List<String> queryParams = new ArrayList<>();
        private List<String> cookies = new ArrayList<>();
        private List<String> postData = new ArrayList<>();

        public Builder() {}

        @Override
        public Request build() {
            Request request = new HttpRequest();

            if (queryParams.isEmpty()) {
                request.createRequest(url);
            } else {
                request.createRequest(url, Joiner.on("&").join(queryParams));
            }

            if (userAgent != null) {
                request.setUserAgent(userAgent);
            }

            if (!cookies.isEmpty()) {
                request.setCookies(Joiner.on(";").join(cookies));
            }

            if (!postData.isEmpty()) {
                request.setPOSTData(Joiner.on("&").join(postData));
            }

            return request;
        }

        @Override
        public Request.Builder setUrl(String url) {
            this.url = url;

            return this;
        }

        @Override
        public Request.Builder setUrl(URL url) {
            setUrl(url.toExternalForm());

            return this;
        }

        @Override
        public Request.Builder addQueryParam(String queryParam) {
            queryParams.add(queryParam);

            return this;
        }

        @Override
        public Request.Builder addQueryParam(String key, String value) {
            addQueryParam(key + "=" + value);

            return this;
        }

        @Override
        public Request.Builder addQueryParams(Map<String, String> queryParams) {
            for (Map.Entry<String, String> pair : queryParams.entrySet()) {
                addQueryParam(pair.getKey(), pair.getValue());
            }

            return this;
        }

        @Override
        public Request.Builder setUserAgent(String userAgent) {
            this.userAgent = userAgent;

            return this;
        }

        @Override
        public Request.Builder addCookie(String cookie) {
            cookies.add(cookie);

            return this;
        }

        @Override
        public Request.Builder addCookie(String key, String value) {
            addCookie(key + "=" + value);

            return this;
        }

        @Override
        public Request.Builder addCookies(Map<String, String> cookies) {
            for (Map.Entry<String, String> pair : cookies.entrySet()) {
                addCookie(pair.getKey(), pair.getValue());
            }

            return this;
        }

        @Override
        public Request.Builder addPOSTData(String postData) {
            this.postData.add(postData);

            return this;
        }

        @Override
        public Request.Builder addPOSTData(String key, String value) {
            addPOSTData(key + "=" + value);

            return this;
        }

        @Override
        public Request.Builder addPOSTData(Map<String, String> postData) {
            for (Map.Entry<String, String> pair : postData.entrySet()) {
                addPOSTData(pair.getKey(), pair.getValue());
            }

            return this;
        }
    }
}