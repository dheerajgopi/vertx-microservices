package org.example.account.user.filter;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.example.microservicecommon.http.AbstractQueryPageAndSort;

import java.util.Arrays;
import java.util.List;

@DataObject
public class UserListPageAndSort extends AbstractQueryPageAndSort {

    private static final List<String> fields = Arrays.asList("username");

    public UserListPageAndSort(final JsonObject queryParams) {
        super(queryParams);
    }

    @Override
    public List<String> fields() {
        return fields;
    }

}
