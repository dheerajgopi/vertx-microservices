package org.example.account;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.example.account.user.dataobject.dto.UserDto;
import org.example.account.user.dataobject.page.UserPage;
import org.example.account.user.filter.UserListFilter;
import org.example.account.service.AccountService;
import org.example.account.user.filter.UserListPageAndSort;
import org.example.microservicecommon.RestApiVerticle;
import org.example.microservicecommon.exception.RestApiException;
import org.example.microservicecommon.http.ApiResponse;
import org.example.microservicecommon.http.PagedResponse;
import org.example.microservicecommon.util.RequestUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API verticle for account related services.
 */
public class AccountRestVerticle extends RestApiVerticle {

    private static final String SERVICE_NAME = "account-rest-api";

    /**
     * Service class for account related operations.
     */
    private AccountService accountService;

    public AccountRestVerticle(final AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Register route handlers, start HTTP server and register in service discovery.
     * @param startPromise promise
     */
    @Override
    public void start(final Promise<Void> startPromise) {
        super.start();

        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/users").handler(this::listAllUsers);
        this.addSubRouter(router);

        final String host = config().getString("account.http.address", "localhost");
        final int port = config().getInteger("account.http.port", 8081);

        createHttpServer(apiRouter, host, port).future()
                .compose(serverCreated -> publishHttpEndpoint(
                        config().getString("api.name", "account"),
                        host,
                        port).future())
                .setHandler(res -> {
                    if (res.succeeded()) {
                        startPromise.complete();
                    } else {
                        startPromise.fail(res.cause());
                    }
                });
    }

    /**
     * Handler for user listing API.
     * @param ctx request context
     */
    private void listAllUsers(final RoutingContext ctx) {
        final JsonObject queryParams = RequestUtils.getQueryParams(ctx);
        final UserListFilter userListFilter = new UserListFilter(queryParams);
        final UserListPageAndSort pageAndSort = new UserListPageAndSort(queryParams);

        accountService.listAllUsers(userListFilter, pageAndSort, res -> {
            if (res.succeeded()) {
                final UserPage users = res.result();
                final List<UserDto> userList = users
                        .getContent()
                        .stream()
                        .map(UserDto::new)
                        .collect(Collectors.toList());

                final ApiResponse response = new ApiResponse(
                        200,
                        new PagedResponse(
                                "users",
                                userList,
                                users.getPage(),
                                users.getSize(),
                                users.getTotalElements()
                        ).toJson()
                );

                ctx.response().end(response.toJson().encode());
            } else {
                final RestApiException restException = new RestApiException(res.cause());

                ctx.fail(restException.getHttpStatus(), restException);
            }
        });
    }
}
