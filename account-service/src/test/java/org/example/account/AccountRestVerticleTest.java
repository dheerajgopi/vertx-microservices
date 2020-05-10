package org.example.account;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.example.account.entity.User;
import org.example.account.service.AccountService;
import org.example.account.user.dataobject.page.UserPage;
import org.example.account.user.filter.UserListFilter;
import org.example.account.user.filter.UserListPageAndSort;
import org.example.microservicecommon.exception.InvalidSortFieldException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.PageImpl;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
public class AccountRestVerticleTest {

    private Integer port = 8081;

    private MockAccountService accountService;

    @BeforeEach
    public void setup(final Vertx vertx, final VertxTestContext testContext) {
        accountService = new MockAccountService(vertx);

        vertx.deployVerticle(new AccountRestVerticle(accountService), testContext.completing());
    }

    @AfterEach
    public void tearDown(final Vertx vertx, final VertxTestContext testContext) {
        vertx.close(testContext.completing());
    }

    @Test
    void testListUsersForSuccess(final Vertx vertx, final VertxTestContext testContext) {
        final WebClient client = WebClient.create(vertx);

        final User user = new User();
        user.setId(1L);
        user.setName("test");
        final UserPage users = new UserPage(new PageImpl<>(Arrays.asList(user)));
        accountService.setUserListResult(users);

        client.get(port, "localhost", "/users").send(testContext.succeeding(res -> testContext.verify(() -> {
            assertEquals(200, res.statusCode());

            final JsonObject actual = new JsonObject(res.body());
            final JsonArray usersArray = actual.getJsonObject("data").getJsonArray("users");

            assertEquals(200, actual.getInteger("status"));
            assertEquals(1, usersArray.size());
            assertEquals(user.getId(), usersArray.getJsonObject(0).getLong("id"));
            assertEquals(user.getName(), usersArray.getJsonObject(0).getString("name"));

            testContext.completeNow();
        })));
    }

    @Test
    void testListUsersForServerError(final Vertx vertx, final VertxTestContext testContext) {
        final WebClient client = WebClient.create(vertx);
        accountService.setException(new RuntimeException("error"));

        client.get(port, "localhost", "/users").send(testContext.succeeding(res -> testContext.verify(() -> {
            assertEquals(500, res.statusCode());

            final JsonObject actual = new JsonObject(res.body());

            assertEquals(500, actual.getInteger("status"));
            assertNull(actual.getJsonObject("data"));
            assertNotNull(actual.getJsonObject("error").getString("message"));

            testContext.completeNow();
        })));
    }

    @Test
    void testListUsersForServiceException(final Vertx vertx, final VertxTestContext testContext) {
        final WebClient client = WebClient.create(vertx);
        accountService.setException(new InvalidSortFieldException("error"));

        client.get(port, "localhost", "/users").send(testContext.succeeding(res -> testContext.verify(() -> {
            assertEquals(400, res.statusCode());

            final JsonObject actual = new JsonObject(res.body());

            assertEquals(400, actual.getInteger("status"));
            assertNull(actual.getJsonObject("data"));
            assertNotNull(actual.getJsonObject("error").getString("message"));

            testContext.completeNow();
        })));
    }

    /**
     * Mock service implementing {@link AccountService}.
     */
    public class MockAccountService implements AccountService {

        private Vertx vertx;

        private RuntimeException exception;

        private UserPage userListResult;

        public MockAccountService(final Vertx vertx) {
            this.vertx = vertx;
        }

        /**
         * Set the exception to be thrown.
         */
        public void setException(final RuntimeException ex) {
            this.exception = ex;
        }

        /**
         * Set value to be returned for user listing method.
         */
        public void setUserListResult(final UserPage userPage) {
            this.userListResult = userPage;
        }

        /**
         * Throw exception if <code>exception</code> field is not null,
         * else return <code>userListResult</code>.
         */
        @Override
        public void listAllUsers(
                final UserListFilter filter,
                final UserListPageAndSort pageAndSort,
                final Handler<AsyncResult<UserPage>> resultHandler
        ) {
            vertx.<UserPage>executeBlocking(promise -> {
                if (this.exception != null) {
                    throw exception;
                }

                promise.complete(userListResult);
            }, true, resultHandler);
        }
    }
}
