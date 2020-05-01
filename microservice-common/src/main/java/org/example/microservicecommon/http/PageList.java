package org.example.microservicecommon.http;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wrapper for paginated list of objects.
 * @param <T> data object type to be transferred through eventbus.
 */
public abstract class PageList<T extends AbstractDataObject> {

    /**
     * List of items.
     */
    protected List<T> content;

    /**
     * Page size.
     */
    protected Integer size;

    /**
     * Page number.
     */
    protected Integer page;

    /**
     * Total elements present in the store.
     */
    protected Long totalElements;

    protected PageList() {
    }

    public PageList(final Page<T> page) {
        this.content = page.getContent();
        this.size = page.getSize();
        this.page = page.getNumber();
        this.totalElements = page.getTotalElements();
    }

    public PageList(final JsonObject json) {
        for (java.util.Map.Entry<String, Object> member : json) {
            switch (member.getKey()) {
            case "content":
                final List<T> content = new ArrayList<>();

                if (member.getValue() instanceof JsonArray) {
                    ((JsonArray) member.getValue()).forEach(item -> {
                        if (item instanceof JsonObject) {
                            content.add((T)(item));
                        }
                    });
                }

                this.content = content;
                break;
            case "size":
                if (member.getValue() instanceof Number) {
                    this.size = ((Number)member.getValue()).intValue();
                }
                break;
            case "page":
                if (member.getValue() instanceof Number) {
                    this.page = ((Number)member.getValue()).intValue();
                }
                break;
            case "totalElements":
                if (member.getValue() instanceof Number) {
                    this.totalElements = ((Number)member.getValue()).longValue();
                }
                break;
            }
        }
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();

        if (this.content != null) {
            json.put("content", new JsonArray(
                    this.content.stream().map(each -> JsonObject.mapFrom(each)).collect(Collectors.toList()))
            );
        }

        if (this.page != null) {
            json.put("page", this.page);
        }

        if (this.size != null) {
            json.put("size", this.size);
        }

        if (this.totalElements != null) {
            json.put("totalElements", this.totalElements);
        }

        return json;
    }

    /**
     * Returns the list of items.
     */
    public List<T> getContent() {
        return content;
    }

    /**
     * Returns the page size.
     */
    public Integer getSize() {
        return size;
    }

    /**
     * Returns the page number.
     */
    public Integer getPage() {
        return page;
    }

    /**
     * Returns the total items in store.
     */
    public Long getTotalElements() {
        return totalElements;
    }
}
