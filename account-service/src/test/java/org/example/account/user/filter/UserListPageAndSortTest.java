package org.example.account.user.filter;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.example.microservicecommon.exception.InvalidSortFieldException;
import org.example.microservicecommon.http.Sort;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserListPageAndSortTest {

    @Test
    void testFields() {
        final UserListPageAndSort pageAndSort = new UserListPageAndSort(new JsonObject());

        assertEquals(Arrays.asList("username"), pageAndSort.fields());
    }

    @Test
    void testValidateSortForInvalidField() {
        final JsonObject json = new JsonObject();
        json.put("sort", new JsonArray(Arrays.asList("invalid")));

        final UserListPageAndSort pageAndSort = new UserListPageAndSort(json);

        assertThrows(InvalidSortFieldException.class, () -> {
            pageAndSort.validateSort();
        });
    }

    @Test
    void testValidateSortForDefaultPaginationValues() {
        final JsonObject json = new JsonObject();
        json.put("sort", new JsonArray(Arrays.asList("username")));

        final UserListPageAndSort pageAndSort = new UserListPageAndSort(json);

        assertDoesNotThrow(() -> {
            pageAndSort.validateSort();
        });
        assertEquals(0, pageAndSort.pageNumber());
        assertEquals(10, pageAndSort.limit());
        assertEquals(0, pageAndSort.offset());
        assertEquals("username", pageAndSort.sortParams().get(0).getField());
        assertEquals(Sort.Direction.DESC, pageAndSort.sortParams().get(0).getDirection());
    }

    @Test
    void testValidateSortForPaginationValues() {
        final JsonObject json = new JsonObject();
        json.put("sort", new JsonArray(Arrays.asList("username,asc")));
        json.put("page", new JsonArray(Arrays.asList("2")));
        json.put("size", new JsonArray(Arrays.asList("11")));

        final UserListPageAndSort pageAndSort = new UserListPageAndSort(json);

        assertDoesNotThrow(() -> {
            pageAndSort.validateSort();
        });
        assertEquals(2, pageAndSort.pageNumber());
        assertEquals(11, pageAndSort.limit());
        assertEquals(22, pageAndSort.offset());
        assertEquals("username", pageAndSort.sortParams().get(0).getField());
        assertEquals(Sort.Direction.ASC, pageAndSort.sortParams().get(0).getDirection());
    }

    @Test
    void testGetJpaPageable() {
        final JsonObject json = new JsonObject();
        json.put("sort", new JsonArray(Arrays.asList("username,asc")));
        json.put("page", new JsonArray(Arrays.asList("2")));
        json.put("size", new JsonArray(Arrays.asList("11")));

        final UserListPageAndSort pageAndSort = new UserListPageAndSort(json);
        final List<org.springframework.data.domain.Sort.Order> sorts = new ArrayList<>();

        sorts.add(new org.springframework.data.domain.Sort.Order(
                org.springframework.data.domain.Sort.Direction.ASC,
                "username"
        ));

        final Pageable expected = PageRequest.of(
                2,
                11,
                org.springframework.data.domain.Sort.by(sorts)
        );
        final Pageable actual = pageAndSort.getJpaPageable();

        assertEquals(expected, actual);
    }
}
