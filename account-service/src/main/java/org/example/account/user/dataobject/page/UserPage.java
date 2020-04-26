package org.example.account.user.dataobject.page;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.example.account.entity.User;
import org.example.microservicecommon.http.PageList;
import org.springframework.data.domain.Page;

/**
 * Paginated list of users {@link User}.
 */
@DataObject
public class UserPage extends PageList<User> {

    public UserPage(final Page<User> page) {
        super(page);
    }

    public UserPage(final JsonObject json) {
        super(json);
    }
}
