package org.example.microservicecommon;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Promise;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.servicediscovery.types.HttpEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Abstract verticle for microservice functionality like service discovery,
 * circuit breaker etc.
 */
public abstract class BaseMicroserviceVerticle extends AbstractVerticle {

    private static final String LOG_EVENT_ADDRESS = "event.log";

    private static final Logger logger = LoggerFactory.getLogger(BaseMicroserviceVerticle.class);

    /**
     * Service discovery.
     */
    protected ServiceDiscovery serviceDiscovery;

    /**
     * Circuit breaker.
     */
    protected CircuitBreaker circuitBreaker;

    /**
     * Contains service records published by service discovery.
     */
    protected Set<Record> registeredRecords = new ConcurrentHashSet<>();

    /**
     * Initialize service discovery and circuit breaker instances.
     */
    @Override
    public void start() {
        serviceDiscovery = ServiceDiscovery
                .create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()));

        final JsonObject cbConfig = config().getJsonObject("circuitBreaker") != null
                ? config().getJsonObject("circuitBreaker")
                : new JsonObject();

        final CircuitBreakerOptions cbOptions = new CircuitBreakerOptions();
        cbOptions
                .setMaxFailures(cbConfig.getInteger("maxFailures", 5))
                .setTimeout(cbConfig.getLong("timeout", 10000L))
                .setFallbackOnFailure(true)
                .setResetTimeout(cbConfig.getLong("resetTimeout", 30000L));
        circuitBreaker = CircuitBreaker.create(cbConfig.getString("name", "circuit-breaker"), vertx, cbOptions);
    }

    /**
     * Publish service record for API gateway.
     * @param host host
     * @param port port
     * @return a promise
     */
    protected Promise<Void> publishApiGateway(final String host, final int port) {
        final Record record = HttpEndpoint.createRecord("api-gateway", host, port, null)
                .setType("api-gateway");

        return publish(record);
    }

    /**
     * Publish service record for event bus service.
     * @param name service name
     * @param address service address
     * @param serviceClass service Class type
     * @return a promise
     */
    protected Promise<Void> publishEventBusService(final String name, final String address, final Class serviceClass) {
        final Record record = EventBusService.createRecord(name, address, serviceClass);

        return publish(record);
    }

    protected Promise<Void> publishHttpEndpoint(final String name, final String host, final int port) {
        final Record record = HttpEndpoint.createRecord(name, host, port, "/",
                new JsonObject().put("api.name", config().getString("api.name", "")));

        return publish(record);
    }

    /**
     * Create a record in service discovery.
     * @param record service record
     * @return a promise
     */
    private Promise<Void> publish(final Record record) {
        if (serviceDiscovery == null) {
            start();
        }

        final Promise<Void> promise = Promise.promise();

        serviceDiscovery.publish(record, res -> {
            if (res.succeeded()) {
                registeredRecords.add(record);
                logger.info("Service <" + res.result().getName() + "> published");
                promise.complete();
            } else {
                promise.fail(res.cause());
            }
        });

        return promise;
    }

    /**
     * Removes all records in service discovery and close.
     * @param promise
     */
    @Override
    public void stop(final Promise<Void> promise) {
        final List<Promise> promises = new ArrayList<>();

        registeredRecords.forEach(record -> {
            final Promise<Void> cleanupPromise = Promise.promise();
            promises.add(cleanupPromise);
            serviceDiscovery.unpublish(record.getRegistration(), res -> {
                if (res.succeeded()) {
                    cleanupPromise.complete();
                } else {
                    cleanupPromise.fail(res.cause());
                }
            });
        });

        if (promises.isEmpty()) {
            serviceDiscovery.close();
            promise.complete();
        } else {
            CompositeFuture
                    .all(promises.stream().map(Promise::future).collect(Collectors.toList()))
                    .setHandler(res -> {
                        serviceDiscovery.close();

                        if (res.succeeded()) {
                            promise.complete();
                        } else {
                            promise.fail(res.cause());
                        }
                    });
        }
    }
}
