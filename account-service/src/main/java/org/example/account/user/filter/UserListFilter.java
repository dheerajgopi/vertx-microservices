package org.example.account.user.filter;

import com.querydsl.core.BooleanBuilder;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.example.account.entity.QUser;
import org.example.microservicecommon.http.AbstractQueryFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for query filters for user listing API.
 */
@DataObject(generateConverter = true)
public class UserListFilter extends AbstractQueryFilter {

    /**
     * username filter.
     */
    private List<String> username = new ArrayList<>();

    public UserListFilter(final JsonObject json) {
        super(json);
        UserListFilterConverter.fromJson(json, this);
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        UserListFilterConverter.toJson(this, json);

        return json;
    }

    public List<String> getUsername() {
        return username;
    }

    public void setUsername(List<String> username) {
        this.username = username;
    }

    /**
     * Returns QueryDSL predicate for filters.
     * @return Boolean Builder
     */
    public BooleanBuilder generateBooleanBuilder() {
        final BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (username != null && !username.isEmpty()) {
            username.forEach(each -> {
                if (StringUtils.isNotBlank(each)) {
                    booleanBuilder.or(QUser.user.username.equalsIgnoreCase(each));
                }
            });
        }

        return booleanBuilder;
    }

    @Override
    public void validate() {

    }
}
