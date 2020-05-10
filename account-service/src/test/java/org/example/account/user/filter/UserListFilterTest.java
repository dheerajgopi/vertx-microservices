package org.example.account.user.filter;

import com.querydsl.core.BooleanBuilder;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.example.account.entity.QUser;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserListFilterTest {

    private static final QUser qUser = QUser.user;

    @Test
    void testGenerateBooleanBuilderWithNoFilters() {
        final JsonObject json = new JsonObject();
        final UserListFilter userListFilter = new UserListFilter(json);

        final BooleanBuilder expected = new BooleanBuilder();
        final BooleanBuilder actual = userListFilter.generateBooleanBuilder();

        assertEquals(expected, actual);
    }

    @Test
    void testGenerateBooleanBuilder() {
        final List<String> usernames = Arrays.asList("a", "b");
        final JsonObject json = new JsonObject();
        json.put("username", new JsonArray(usernames));
        final UserListFilter userListFilter = new UserListFilter(json);

        final BooleanBuilder expected = new BooleanBuilder();
        usernames.stream().forEach(each -> {
            expected.or(qUser.username.equalsIgnoreCase(each));
        });

        final BooleanBuilder actual = userListFilter.generateBooleanBuilder();

        assertEquals(expected, actual);
    }
}
