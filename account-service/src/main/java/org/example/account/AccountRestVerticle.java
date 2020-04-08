package org.example.account;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.example.account.dto.UserDto;
import org.example.account.entity.User;
import org.example.account.service.AccountService;
import org.example.microservicecommon.RestApiVerticle;
import org.example.microservicecommon.exception.RestApiException;
import org.example.microservicecommon.http.ApiResponse;

import java.util.List;
import java.util.stream.Collectors;

public class AccountRestVerticle extends RestApiVerticle {

    private static final String SERVICE_NAME = "account-rest-api";

    private AccountService accountService;

    public AccountRestVerticle(final AccountService accountService) {
        this.accountService = accountService;
    }

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

    private void listAllUsers(final RoutingContext ctx) {
        accountService.listAllUsers(res -> {
            if (res.succeeded()) {
                final List<User> users = res.result();
                final List<JsonObject> userList = users
                        .stream()
                        .map(UserDto::new)
                        .map(UserDto::toJson)
                        .collect(Collectors.toList());
                final JsonArray payload = new JsonArray(userList);

                final ApiResponse response = new ApiResponse(
                        200,
                        new JsonObject().put("users", payload)
                );

                ctx.response().end(response.toJson().encode());
            } else {
                final RestApiException restException = new RestApiException(res.cause());

                ctx.fail(restException.getHttpStatus(), restException);
            }
        });
    }
}
