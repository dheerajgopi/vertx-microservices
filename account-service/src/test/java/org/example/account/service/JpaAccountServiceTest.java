package org.example.account.service;

import com.querydsl.core.types.Predicate;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.example.account.entity.User;
import org.example.account.user.UserService;
import org.example.account.user.dataobject.dto.CreateUserReqDto;
import org.example.account.user.filter.UserListFilter;
import org.example.account.user.filter.UserListPageAndSort;
import org.example.microservicecommon.exception.ConflictException;
import org.example.microservicecommon.exception.InvalidSortFieldException;
import org.example.microservicecommon.exception.MissingOrEmptyKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class JpaAccountServiceTest {

    private ApplicationContext appContext;

    private UserService userService;

    private JpaAccountService jpaAccountService;

    @BeforeEach
    public void setup(final Vertx vertx) {
        appContext = Mockito.mock(ApplicationContext.class);
        userService = Mockito.mock(UserService.class);
        Mockito.when(appContext.getBean("userService")).thenReturn(userService);

        jpaAccountService = new JpaAccountService(vertx, appContext);
    }

    public void tearDown(final Vertx vertx, final VertxTestContext testContext) {
        vertx.close(testContext.completing());
    }

    @Test
    void testListAllUsersForSuccess(final VertxTestContext testContext) {
        final JsonObject reqParams = new JsonObject();
        final User user = new User();
        user.setId(1L);
        user.setName("test");
        final Page<User> usersPage = new PageImpl<>(Arrays.asList(user));
        Mockito.when(userService.fetchAll(Mockito.any(Predicate.class), Mockito.any(Pageable.class)))
                .thenReturn(usersPage);

        jpaAccountService.listAllUsers(
                new UserListFilter(reqParams),
                new UserListPageAndSort(reqParams),
                testContext.succeeding(res -> testContext.verify(() -> {
                    assertEquals(1, res.getContent().size());
                    assertEquals(user, res.getContent().get(0));

                    testContext.completeNow();
                }))
        );
    }

    @Test
    void testListAllUsersForInvalidSort(final VertxTestContext testContext) {
        final JsonObject reqParams = new JsonObject();
        reqParams.put("sort", new JsonArray(Arrays.asList("invalid,desc")));

        jpaAccountService.listAllUsers(
                new UserListFilter(reqParams),
                new UserListPageAndSort(reqParams),
                testContext.failing(res -> testContext.verify(() -> {
                    assertTrue(res instanceof InvalidSortFieldException);

                    testContext.completeNow();
                }))
        );
    }

    @Test
    void testCreateUserForSuccess(final VertxTestContext testContext) {
        final JsonObject reqBody = new JsonObject();
        final JsonObject userData = new JsonObject();
        userData.put("username", "test");
        userData.put("name", "test");
        userData.put("password", "test");
        reqBody.put("user", userData);

        final CreateUserReqDto reqBodyDto = new CreateUserReqDto(reqBody);

        final User user = new User();
        user.setId(1L);
        user.setName(reqBodyDto.getUser().getName());
        user.setUsername(reqBodyDto.getUser().getUsername());
        user.setPassword("pswd");

        Mockito.when(userService.createUser(Mockito.any(User.class)))
                .thenReturn(user);

        jpaAccountService.createUser(
                reqBodyDto,
                testContext.succeeding(res -> testContext.verify(() -> {
                    assertEquals(user.getName(), res.getUser().getName());
                    assertEquals(user.getUsername(), res.getUser().getUsername());

                    testContext.completeNow();
                }))
        );
    }

    @Test
    void testCreateUserForValidationError(final VertxTestContext testContext) {
        final JsonObject reqBody = new JsonObject();
        final JsonObject userData = new JsonObject();
        userData.put("username", "");
        userData.put("name", "test");
        userData.put("password", "test");
        reqBody.put("user", userData);

        final CreateUserReqDto reqBodyDto = new CreateUserReqDto(reqBody);

        jpaAccountService.createUser(
                reqBodyDto,
                testContext.failing(ex -> testContext.verify(() -> {
                    assertTrue(ex instanceof MissingOrEmptyKeyException);
                    Mockito.verify(userService, Mockito.times(0)).createUser(Mockito.any(User.class));

                    testContext.completeNow();
                }))
        );
    }

    @Test
    void testCreateUserForConflictException(final VertxTestContext testContext) {
        final JsonObject reqBody = new JsonObject();
        final JsonObject userData = new JsonObject();
        userData.put("username", "test");
        userData.put("name", "test");
        userData.put("password", "test");
        reqBody.put("user", userData);

        final CreateUserReqDto reqBodyDto = new CreateUserReqDto(reqBody);

        Mockito.when(userService.createUser(Mockito.any(User.class))).thenThrow(ConflictException.class);

        jpaAccountService.createUser(
                reqBodyDto,
                testContext.failing(ex -> testContext.verify(() -> {
                    assertTrue(ex instanceof ConflictException);

                    testContext.completeNow();
                }))
        );
    }
}
