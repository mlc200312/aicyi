package io.github.aicyi.midware.web.log;

import io.github.aicyi.commons.lang.model.BaseBean;

import java.util.Map;

/**
 * @author Mr.Min
 * @description Web 请求日志
 * @date 2026/8/12
 */
public class WebRequestLog extends BaseBean {

    /**
     * 请求 ID
     */
    private String requestId;

    /**
     * 请求信息
     */
    private RequestInfo request;

    /**
     * 响应信息
     */
    private ResponseInfo response;

    /**
     * 请求耗时，单位：ms
     */
    private Long costTime;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 异常信息
     */
    private String errorMessage;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public RequestInfo getRequest() {
        return request;
    }

    public void setRequest(RequestInfo request) {
        this.request = request;
    }

    public ResponseInfo getResponse() {
        return response;
    }

    public void setResponse(ResponseInfo response) {
        this.response = response;
    }

    public Long getCostTime() {
        return costTime;
    }

    public void setCostTime(Long costTime) {
        this.costTime = costTime;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private WebRequestLog() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final WebRequestLog target = new WebRequestLog();

        public Builder requestId(String requestId) {
            target.requestId = requestId;
            return this;
        }

        public Builder request(RequestInfo request) {
            target.request = request;
            return this;
        }

        public Builder response(ResponseInfo response) {
            target.response = response;
            return this;
        }

        public Builder costTime(Long costTime) {
            target.costTime = costTime;
            return this;
        }

        public Builder success(Boolean success) {
            target.success = success;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            target.errorMessage = errorMessage;
            return this;
        }

        public WebRequestLog build() {
            return target;
        }
    }

    /**
     * 请求信息
     */
    public static class RequestInfo extends BaseBean {

        /**
         * 请求 URL
         */
        private String url;

        /**
         * HTTP Method
         */
        private String method;

        /**
         * 请求 Header
         */
        private Map<String, String> headers;

        /**
         * Query 参数
         */
        private Map<String, Object> queryParams;

        /**
         * Path 参数
         */
        private Map<String, Object> pathParams;

        /**
         * Body
         */
        private Object body;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        public Map<String, Object> getQueryParams() {
            return queryParams;
        }

        public void setQueryParams(Map<String, Object> queryParams) {
            this.queryParams = queryParams;
        }

        public Map<String, Object> getPathParams() {
            return pathParams;
        }

        public void setPathParams(Map<String, Object> pathParams) {
            this.pathParams = pathParams;
        }

        public Object getBody() {
            return body;
        }

        public void setBody(Object body) {
            this.body = body;
        }

        private RequestInfo() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private final RequestInfo target = new RequestInfo();

            public Builder url(String url) {
                target.url = url;
                return this;
            }

            public Builder method(String method) {
                target.method = method;
                return this;
            }

            public Builder headers(Map<String, String> headers) {
                target.headers = headers;
                return this;
            }

            public Builder queryParams(Map<String, Object> queryParams) {
                target.queryParams = queryParams;
                return this;
            }

            public Builder pathParams(Map<String, Object> pathParams) {
                target.pathParams = pathParams;
                return this;
            }

            public Builder body(Object body) {
                target.body = body;
                return this;
            }

            public RequestInfo build() {
                return target;
            }
        }
    }

    /**
     * 响应信息
     */
    public static class ResponseInfo extends BaseBean {

        /**
         * HTTP 状态码
         */
        private Integer statusCode;

        /**
         * Response Header
         */
        private Map<String, String> headers;

        /**
         * Response Body
         */
        private Object body;

        public Integer getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        public Object getBody() {
            return body;
        }

        public void setBody(Object body) {
            this.body = body;
        }

        private ResponseInfo() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private final ResponseInfo target = new ResponseInfo();

            public Builder statusCode(Integer statusCode) {
                target.statusCode = statusCode;
                return this;
            }

            public Builder headers(Map<String, String> headers) {
                target.headers = headers;
                return this;
            }

            public Builder body(Object body) {
                target.body = body;
                return this;
            }

            public ResponseInfo build() {
                return target;
            }
        }
    }
}
