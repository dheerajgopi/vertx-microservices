package org.example.account.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.example.account.entity.User;
import org.example.account.user.dataobject.dto.CreateUserReqDto;
import org.example.account.user.dataobject.dto.CreateUserResDto;
import org.example.account.user.dataobject.dto.UserDto;
import org.example.account.user.dataobject.page.UserPage;
import org.example.account.user.filter.UserListFilter;
import org.example.account.user.UserService;
import org.example.account.user.filter.UserListPageAndSort;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;

public class JpaAccountService implements AccountService {

    private Vertx vertx;

    private UserService userService;

    public JpaAccountService(final Vertx vertx, final ApplicationContext appContext) {
        this.vertx = vertx;
        this.userService = (UserService) appContext.getBean("userService");
    }

    @Override
    public void listAllUsers(
            final UserListFilter filter,
            final UserListPageAndSort pageAndSort,
            final Handler<AsyncResult<UserPage>> resultHandler) {
        vertx.<UserPage>executeBlocking(promise -> {
            pageAndSort.validateSort();

            final Page<User> users = userService.fetchAll(
                    filter.generateBooleanBuilder(),
                    pageAndSort.getJpaPageable()
            );

            promise.complete(new UserPage(users));
        }, false, resultHandler);
    }

    @Override
    public void createUser(
            final CreateUserReqDto newUserDto,
            final Handler<AsyncResult<CreateUserResDto>> resultHandler
    ) {
        vertx.<CreateUserResDto>executeBlocking(promise -> {
            final User user = newUserDto.validateAndBuildEntity();
            userService.createUser(user);
            final CreateUserResDto responsePayload = new CreateUserResDto(new UserDto(user));

            promise.complete(responsePayload);
        }, resultHandler);
    }
}
