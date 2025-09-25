package io.github.aicyi.commons.lang;

/**
 * @author Mr.Min
 * @description Web层通用响应
 * @date 21:34
 **/
public class Response implements IResponse {

    private boolean status = true;
    private String code;
    private String message;

    // 私有构造函数
    private Response(Builder builder) {
        this.status = builder.status;
        this.code = builder.code;
        this.message = builder.message;
    }


    @Override
    public boolean getStatus() {
        return this.status;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public static class Builder {
        private boolean status = true;
        private String code;
        private String message;

        public Builder success() {
            this.status = true;
            return this;
        }

        public Builder failure() {
            this.status = false;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder withError(String code, String message) {
            this.status = false;
            this.code = code;
            this.message = message;
            return this;
        }

        public Response build() {
            if (!status && (code == null || message == null)) {
                throw new IllegalStateException("Error response must have code and message");
            }
            return new Response(this);
        }
    }

    // 静态工厂方法
    public static Builder builder() {
        return new Builder();
    }
}
