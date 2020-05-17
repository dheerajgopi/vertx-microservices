package org.example.account.service;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.example.account.user.dataobject.dto.CreateUserReqDto;
import org.example.account.user.dataobject.dto.CreateUserResDto;
import org.example.account.user.dataobject.page.UserPage;
import org.example.account.user.filter.UserListFilter;
import org.example.account.user.filter.UserListPageAndSort;

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
     * @param filter query filters
     * @param pageAndSort pagination and sort
     * @param resultHandler handler to be called once all users are fetched
     */
    void listAllUsers(
            UserListFilter filter,
            UserListPageAndSort pageAndSort,
            Handler<AsyncResult<UserPage>> resultHandler
    );

    /**
     * Create an user.
     * @param newUserDto request body containing user details
     * @param resultHandler handler to be called once all user is created
     */
    void createUser(CreateUserReqDto newUserDto, Handler<AsyncResult<CreateUserResDto>> resultHandler);
}
