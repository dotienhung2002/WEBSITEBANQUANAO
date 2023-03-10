package com.application.fusamate.exception;

public class ApiErrorResponseBuilder {
    private static ApiErrorResponseBuilder builder;

    private ApiErrorResponseBuilder() {

    }

    private Integer status;
//    private String message;
//    private String path;
    private String error;

    public synchronized static ApiErrorResponseBuilder getInstance() {
        if(builder == null) {
            builder = new ApiErrorResponseBuilder();
        }
        return builder;
    }

    public ApiErrorResponseBuilder withStatus(Integer status) {
        builder.status = status;
        return builder;
    }

    public ApiErrorResponseBuilder withError(String error) {
        builder.error = error;
        return builder;
    }

    public ApiErrorResponse build() {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setStatus(builder.status);
        apiErrorResponse.setError(builder.error);
        return apiErrorResponse;
    }
}
