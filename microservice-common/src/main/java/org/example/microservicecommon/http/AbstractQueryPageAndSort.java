package org.example.microservicecommon.http;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.example.microservicecommon.exception.InvalidSortFieldException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Abstract class for wrapping pagination and sort parameters passed in the request.
 */
public abstract class AbstractQueryPageAndSort implements QueryPagination, QuerySort {

    /**
     * sort parameter key.
     */
    public static final String sortKey = "sort";

    /**
     * page number parameter key.
     */
    public static final String pageKey = "page";

    /**
     * page size parameter key.
     */
    public static final String sizeKey = "size";

    /**
     * list for sort values.
     */
    protected final List<String> sort = new ArrayList<>();

    /**
     * page number value.
     */
    protected final Integer page;

    /**
     * page size value.
     */
    protected final Integer size;

    /**
     * all query params.
     */
    protected final JsonObject queryParams;

    /**
     * list of {@link Sort} objects parsed from <code>sort</code> field.
     * Stores both sort field and direction.
     */
    protected final List<Sort> sortParams = new ArrayList<>();

    /**
     * Parses pagination and sort parameters.
     * Default values are considered in case no value is provided.
     * @param queryParams all query params
     */
    public AbstractQueryPageAndSort(final JsonObject queryParams) {
        this.queryParams = queryParams;
        final JsonArray sortFields = queryParams.getJsonArray(sortKey);
        final JsonArray pageValues = queryParams.getJsonArray(pageKey);
        final JsonArray sizeValues = queryParams.getJsonArray(sizeKey);

        if (sortFields != null) {
            sortFields.forEach(each -> this.sort.add(each.toString()));
        }

        if (this.sort != null) {
            this.sort.forEach(each -> this.sortParams.add(new Sort(each)));
        }

        if (pageValues != null && !pageValues.getList().isEmpty()) {
            final String pageVal = pageValues.getList().get(0) == null
                    ? null
                    : pageValues.getList().get(0).toString();

            if (StringUtils.isBlank(pageVal)) {
                this.page = 0;
            } else {
                this.page = StringUtils.isNumeric(pageVal) ? Integer.valueOf(pageVal) : 0;
            }
        } else {
            this.page = 0;
        }

        if (sizeValues != null && !sizeValues.getList().isEmpty()) {
            final String sizeVal = sizeValues.getList().get(0) == null
                    ? null
                    : sizeValues.getList().get(0).toString();

            if (StringUtils.isBlank(sizeVal)) {
                this.size = 10;
            } else {
                this.size = StringUtils.isNumeric(sizeVal) ? Integer.valueOf(sizeVal) : 10;
            }
        } else {
            this.size = 10;
        }
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();

        if (this.sort != null) {
            json.put(sortKey, new JsonArray(this.sort));
        }

        if (this.page != null) {
            json.put(pageKey, new JsonArray(Arrays.asList(this.page)));
        }

        if (this.size != null) {
            json.put(sizeKey, new JsonArray(Arrays.asList(this.size)));
        }

        return json;
    }

    public abstract List<String> fields();

    /**
     * Validate sort parameter fields and throw <code>InvalidSortFieldException</code> if invalid.
     * @throws InvalidSortFieldException
     */
    public void validateSort() {
        final Set<String> sortFields = sortParams.stream().map(Sort::getField).collect(Collectors.toSet());
        final List<String> invalidFields = new ArrayList<>();

        for (final String field : sortFields) {
            if (!fields().contains(field)) {
                invalidFields.add(field);
            }
        }

        if (!invalidFields.isEmpty()) {
            throw new InvalidSortFieldException("invalid sort fields: ".concat(String.join(",", invalidFields)));
        }
    }

    /**
     * Returns limit.
     */
    @Override
    public Integer limit() {
        return size;
    }

    /**
     * Calculate offset and return.
     */
    @Override
    public Integer offset() {
        return page * size;
    }

    /**
     * Returns page number.
     */
    @Override
    public Integer pageNumber() {
        return page;
    }

    /**
     * Returns sort fields and its corresponding sort directions.
     */
    @Override
    public List<Sort> sortParams() {
        return sortParams;
    }

    /**
     * Returns spring {@link Pageable} corresponding to the sort parameters provided.
     * @return {@link Pageable} object
     */
    public Pageable getJpaPageable() {
        final List<org.springframework.data.domain.Sort.Order> jpaSorts = new ArrayList<>();

        for (final Sort eachSort : sortParams) {
            final org.springframework.data.domain.Sort.Direction dir;

            if (eachSort.isAscending()) {
                dir = org.springframework.data.domain.Sort.Direction.ASC;
            } else {
                dir = org.springframework.data.domain.Sort.Direction.DESC;
            }

            jpaSorts.add(new org.springframework.data.domain.Sort.Order(dir, eachSort.getField()));
        }

        final org.springframework.data.domain.Sort jpaSort = org.springframework.data.domain.Sort.by(jpaSorts);

        return PageRequest.of(pageNumber(), limit(), jpaSort);
    }
}
