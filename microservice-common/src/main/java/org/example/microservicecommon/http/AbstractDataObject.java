package org.example.microservicecommon.http;

import io.vertx.core.json.JsonObject;

/**
 * Abstract class for data objects to be transferred through eventbus.
 */
public abstract class AbstractDataObject implements JsonData {

    protected AbstractDataObject() {

    }

    protected AbstractDataObject(final JsonObject jsonObject) {

    }

    @Override
    public abstract JsonObject toJson();

}
