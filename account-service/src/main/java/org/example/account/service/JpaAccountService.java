package org.example.account.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.example.account.entity.User;
import org.example.account.user.filter.UserListFilter;
import org.example.account.user.UserService;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class JpaAccountService implements AccountService {

    private Vertx vertx;

    private UserService userService;

    public JpaAccountService(final Vertx vertx, final ApplicationContext appContext) {
        this.vertx = vertx;
        this.userService = (UserService) appContext.getBean("userService");
    }

    @Override
    public void listAllUsers(final UserListFilter filter, final Handler<AsyncResult<List<User>>> resultHandler) {
        vertx.<List<User>>executeBlocking(promise -> {
            final List<User> users = userService.fetchAll(filter.generateBooleanBuilder());
            promise.complete(users);
        }, false, resultHandler);
    }

}
