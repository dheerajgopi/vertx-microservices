package org.example.microservicecommon.util;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public final class RequestUtils {

    private RequestUtils() {
    }

    public static JsonObject getQueryParams(final RoutingContext ctx) {
        final JsonObject paramJson = new JsonObject();

        ctx.queryParams().forEach(each -> {
            paramJson.put(each.getKey(), each.getValue());
        });

        return paramJson;
    }
}
