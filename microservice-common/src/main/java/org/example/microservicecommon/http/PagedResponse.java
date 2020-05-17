package org.example.microservicecommon.http;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Wrapper for paginated API response.
 */
public class PagedResponse implements JsonData {

    /**
     * key for list payload.
     */
    private String key;

    /**
     * items.
     */
    private List<JsonObject> items;

    /**
     * page number.
     */
    private Integer page;

    /**
     * page size.
     */
    private Integer size;

    /**
     * total items.
     */
    private Long totalElements;

    public PagedResponse(
            final String key,
            final List<? extends JsonData> list,
            final Integer page,
            final Integer size,
            final Long totalElements
    ) {
        this.key = key;
        this.items = list.stream().map(JsonData::toJson).collect(Collectors.toList());
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
    }

    @Override
    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        final JsonObject paginationJson = new JsonObject();

        if (this.items != null) {
            json.put(key, new JsonArray(this.items));
        }

        if (this.page != null) {
            paginationJson.put("page", this.page);
        }

        if (this.size != null) {
            paginationJson.put("size", this.size);
        }

        if (this.totalElements != null) {
            paginationJson.put("totalElements", this.totalElements);
        }

        json.put("_pagination", paginationJson);

        return json;
    }
}
