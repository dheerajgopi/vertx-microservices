package org.example.microservicecommon.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Set;

public final class RequestUtils {

    private RequestUtils() {
    }

    public static JsonObject getQueryParams(final RoutingContext ctx) {
        final JsonObject paramJson = new JsonObject();
        final Set<String> paramKeys = ctx.queryParams().names();

        paramKeys.forEach(each -> {
            paramJson.put(each, new JsonArray(ctx.queryParam(each)));
        });

        return paramJson;
    }
}
