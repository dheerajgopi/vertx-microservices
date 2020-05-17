package org.example.microservicecommon.http;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import org.example.microservicecommon.exception.RestApiException;

/**
 * Container for all REST API responses.
 */
public class ApiResponse implements JsonData {

    /**
     * HTTP status.
     */
    private Integer status;

    /**
     * Error.
     */
    private Error error;

    /**
     * Payload.
     */
    private JsonObject data;

    public ApiResponse(final Integer status, final JsonObject data) {
        this.status = status;
        this.data = data;
    }

    public ApiResponse(final Integer status, final Error error) {
        this.status = status;
        this.error = error;
    }

    public ApiResponse(final Throwable ex) {
        final Error error = new Error(ex.getMessage());

        if (ex instanceof RestApiException) {
            this.status = ((RestApiException) ex).getHttpStatus();
        } else {
            this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR.code();
            error.message = "internal server error";
        }

        this.error = error;
    }

    public Integer getStatus() {
        return status;
    }

    public Error getError() {
        return error;
    }

    public JsonObject getData() {
        return data;
    }

    @Override
    public JsonObject toJson() {
        final JsonObject jsonResponse = new JsonObject();

        jsonResponse.put("status", this.status);

        if (this.error != null) {
            jsonResponse.put("error", this.error.toJson());
        }

        if (this.data != null) {
            jsonResponse.put("data", this.data);
        }

        return jsonResponse;
    }

    public static class Error {

        private String message;

        public Error(final String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public JsonObject toJson() {
            final JsonObject errorJson = new JsonObject();
            errorJson.put("message", message);

            return errorJson;
        }
    }

}
