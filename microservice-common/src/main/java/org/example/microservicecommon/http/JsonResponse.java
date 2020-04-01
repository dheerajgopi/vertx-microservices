package org.example.microservicecommon.http;

import io.vertx.core.json.JsonObject;

/**
 * Interface for objects which can be converted to a JSON.
 */
public interface JsonResponse {

    /**
     * Converts to {@link JsonObject}
     * @return {@link JsonObject}
     */
    JsonObject toJson();

}
