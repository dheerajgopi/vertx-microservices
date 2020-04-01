package org.example.gateway;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;
import org.example.microservicecommon.RestApiVerticle;

import java.util.List;
import java.util.Optional;

/**
 * API gateway verticle.
 */
public class ApiGatewayVerticle extends RestApiVerticle {

    private static final int DEFAULT_PORT = 8787;

    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayVerticle.class);

    /**
     * Starts an HTTP server.
     * Dispatches each request to its respective service.
     * @param startPromise a promise
     */
    @Override
    public void start(final Promise<Void> startPromise) {
        super.start();

        final String host = config().getString("api.gateway.http.address", "localhost");
        final int port = config().getInteger("api.gateway.http.port", DEFAULT_PORT);

        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/v").handler(this::apiVersion);
        router.route("/*").handler(this::dispatchRequests);
        this.addSubRouter(router);

        vertx
                .createHttpServer()
                .requestHandler(apiRouter)
                .listen(port, host, res -> {
                    if (res.succeeded()) {
                        this.publishApiGateway(host, port);
                        startPromise.complete();
                        logger.info("API Gateway is running on port " + port);
                    } else {
                        startPromise.fail(res.cause());
                    }
                });

    }

    /**
     * Identify the service from the URI, fetch the corresponding record from service
     * discovery, and dispatches the request to that service.
     * @param context request context
     */
    private void dispatchRequests(final RoutingContext context) {
        final String requestPath = context.request().uri();

        circuitBreaker.execute(promise -> {
            this.getAllHttpEndpoints().future().setHandler(res -> {
                if (res.succeeded()) {
                    final List<Record> records = res.result();

                    if (requestPath.length() <= 1) {
                        this.routeNotFoundHandler(context);
                        promise.complete();

                        return;
                    }

                    final String[] splitRequestPath = requestPath.substring(1).split("/");

                    if (splitRequestPath.length <= 1) {
                        this.routeNotFoundHandler(context);
                        promise.complete();

                        return;
                    }

                    final String apiName = splitRequestPath[0];
                    final String relativePath = requestPath.substring(apiName.length() + 1);

                    Optional<Record> serviceRecord = records.stream()
                            .filter(record -> record.getMetadata().getString("api.name") != null)
                            .filter(record -> record.getMetadata().getString("api.name").equals(apiName))
                            .findAny();

                    if (serviceRecord.isPresent()) {
                        doDispatch(context, relativePath, serviceDiscovery.getReference(serviceRecord.get()).get(), promise);
                    } else {
                        this.routeNotFoundHandler(context);
                        promise.complete();
                    }
                } else {
                    promise.fail(res.cause());
                }
            }).setHandler(res -> {
                if (res.failed()) {
                     this.badGatewayHandler(res.cause(), context);
                     promise.complete();
                }
            });
        });
    }

    /**
     * Calls the HTTP service, get the response and return it to the client.
     * @param context request context
     * @param path request path
     * @param httpClient HTTP client from service discovery
     * @param cbPromise circuit breaker promise
     */
    private void doDispatch(
            final RoutingContext context,
            final String path,
            final WebClient httpClient,
            final Promise<Object> cbPromise
    ) {
        httpClient.request(context.request().method(), path)
                .as(BodyCodec.jsonObject())
                .send(res -> {
                    if (res.succeeded()) {
                        if (res.result().statusCode() >= 500) {
                            cbPromise.fail(res.result().bodyAsString());
                        } else {
                            context
                                    .response()
                                    .setStatusCode(res.result().statusCode())
                                    .end(res.result().body().encode());
                            cbPromise.complete();
                        }
                    } else {
                        cbPromise.fail(res.cause());
                    }

                    ServiceDiscovery.releaseServiceObject(serviceDiscovery, httpClient);
                });
    }

    /**
     * Fetch all HTTP endpoints from service discovery.
     * @return a promise of Records from service discovery
     */
    private Promise<List<Record>> getAllHttpEndpoints() {
        final Promise<List<Record>> promise = Promise.promise();

        serviceDiscovery.getRecords(record -> record.getName().equals(HttpEndpoint.TYPE), res -> {
            if (res.succeeded()) {
                promise.complete(res.result());
            } else {
                promise.fail(res.cause());
            }
        });

        return promise;
    }

    private void apiVersion(RoutingContext context) {
        context.response()
                .end(new JsonObject().put("version", "v1").encodePrettily());
    }
}
