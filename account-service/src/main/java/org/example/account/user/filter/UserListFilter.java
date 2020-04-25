package org.example.account.user.filter;

import com.querydsl.core.BooleanBuilder;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.example.account.entity.QUser;

/**
 * Wrapper for query filters for user listing API.
 */
@DataObject(generateConverter = true)
public class UserListFilter {

    /**
     * username filter.
     */
    private String username;

    public UserListFilter(final JsonObject json) {
        UserListFilterConverter.fromJson(json, this);
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        UserListFilterConverter.toJson(this, json);

        return json;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns QueryDSL predicate for filters.
     * @return Boolean Builder
     */
    public BooleanBuilder generateBooleanBuilder() {
        final BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (StringUtils.isNotBlank(username)) {
            booleanBuilder.and(QUser.user.username.equalsIgnoreCase(username));
        }

        return booleanBuilder;
    }
}
