package org.example.account.service;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.example.account.entity.User;
import org.example.account.user.filter.UserListFilter;

import java.util.List;

/**
 * Event bus service for managing users.
 */
@VertxGen
@ProxyGen
public interface AccountService {

    /**
     * The name of the event bus service.
     */
    String SERVICE_NAME = "user-account-eb-service";

    /**
     * The address on which the service is published.
     */
    String SERVICE_ADDRESS = "service.user.account";

    /**
     * List all users
     * @param resultHandler handler to be called once all users are fetched
     */
    void listAllUsers(UserListFilter filter, Handler<AsyncResult<List<User>>> resultHandler);
}
