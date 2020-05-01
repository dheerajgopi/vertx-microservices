package org.example.account;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.serviceproxy.ServiceBinder;
import org.example.account.config.JpaConfig;
import org.example.account.service.AccountService;
import org.example.account.service.JpaAccountService;
import org.example.microservicecommon.BaseMicroserviceVerticle;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Account service verticle.
 */
public class AccountVerticle extends BaseMicroserviceVerticle {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AccountVerticle.class);

    /**
     * Spring application context.
     */
    private ApplicationContext applicationContext;

    /**
     * Event bus service for user account operations.
     */
    private AccountService accountService;

    /**
     * Start spring application context.
     */
    public AccountVerticle() {
        applicationContext = new AnnotationConfigApplicationContext(JpaConfig.class);
    }

    /**
     * Publish event bus service and deploy verticle for RESTful APIs.
     */
    @Override
    public void start(final Promise<Void> startPromise) {
        super.start();

        accountService = new JpaAccountService(vertx, applicationContext);

        new ServiceBinder(vertx)
                .setAddress(AccountService.SERVICE_ADDRESS)
                .register(AccountService.class, accountService);

        publishEventBusService(
                AccountService.SERVICE_NAME,
                AccountService.SERVICE_ADDRESS,
                AccountService.class
        ).future().compose(servicePublished -> deployRestVerticle().future()).setHandler(res -> {
            if (res.succeeded()) {
                logger.info("account verticle deployed");
            } else {
                logger.error("account verticle deployment failed", res.cause());
            }
        });
    }

    private Promise<Void> deployRestVerticle() {
        final Promise<Void> promise = Promise.promise();

        vertx.deployVerticle(new AccountRestVerticle(accountService),
                new DeploymentOptions().setConfig(config()),
                res -> {
                    if (res.succeeded()) {
                        promise.complete();
                    } else {
                        promise.fail(res.cause());
                    }
                });

        return promise;
    }
}
