package org.example.account.service;

import com.querydsl.core.types.Predicate;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.example.account.entity.User;
import org.example.account.user.UserService;
import org.example.account.user.dataobject.page.UserPage;
import org.example.account.user.filter.UserListFilter;
import org.example.account.user.filter.UserListPageAndSort;
import org.example.microservicecommon.exception.InvalidSortFieldException;
import org.example.microservicecommon.util.RequestUtils;
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
}
