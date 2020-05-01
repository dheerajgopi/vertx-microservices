package org.example.microservicecommon.http;

import io.vertx.core.json.JsonObject;

public abstract class AbstractQueryFilter implements QueryFilter {

    protected final JsonObject queryParams;

    public AbstractQueryFilter(final JsonObject params) {
        this.queryParams = params;
    }

}
