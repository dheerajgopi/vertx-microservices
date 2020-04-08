package org.example.microservicecommon;

import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.example.microservicecommon.exception.ResourceNotFoundException;
import org.example.microservicecommon.exception.RestApiException;
import org.example.microservicecommon.http.ApiResponse;

/**
 * Abstract verticle containing helper methods for RESTful APIs.
 */
public abstract class RestApiVerticle extends BaseMicroserviceVerticle {

    protected Router apiRouter;

    @Override
    public void start() {
        super.start();
        apiRouter = Router.router(vertx);

        // catch-all mechanism for exceptions
        apiRouter.route("/*").failureHandler(ctx -> {
            ctx
                    .response()
                    .setStatusCode(ctx.statusCode())
                    .end(new ApiResponse(ctx.failure()).toJson().encodePrettily());
        });

        // put header values
        apiRouter
                .route("/*")
                .produces("application/json")
                .handler(ctx -> {
                    ctx
                            .response()
                            .setChunked(true)
                            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");

                    ctx.next();
                });

        // 404 handling
        apiRouter.route().last().handler(ctx -> {
            ctx
                    .response()
                    .setStatusCode(404)
                    .end(new ApiResponse(
                            new RestApiException(new ResourceNotFoundException("Route not found"))
                    ).toJson().encodePrettily());
        });
    }

    /**
     * Create an HTTP server.
     * @param router router
     * @param host host
     * @param port port
     * @return a promise
     */
    protected Promise<Void> createHttpServer(final Router router, final String host, final Integer port) {
        final Promise<Void> promise = Promise.promise();

        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(port, host, res -> {
                    if (res.succeeded()) {
                        promise.complete();
                    } else {
                        promise.fail(res.cause());
                    }
                });

        return promise;
    }

    /**
     * Handler for invalid route. Returns 404.
     * @param context request context
     */
    protected void routeNotFoundHandler(final RoutingContext context) {
        final ApiResponse.Error error = new ApiResponse.Error("route not found");
        final ApiResponse apiResponse = new ApiResponse(404, error);

        sendResponse(context, apiResponse);
    }

    /**
     * Handler bad gateway. Returns 502.
     * @param context request context
     */
    protected void badGatewayHandler(final Throwable ex, final RoutingContext context) {
        final ApiResponse.Error error = new ApiResponse.Error("bad gateway");
        final ApiResponse apiResponse = new ApiResponse(502, error);

        sendResponse(context, apiResponse);
    }

    /**
     * Mount sub-routers with <code>/api</code> prefix.
     * @param router subrouter
     */
    protected void addSubRouter(final Router router) {
        apiRouter.mountSubRouter("/", router);
    }

    /**
     * Write the JSON response to send.
     * @param context request context
     * @param apiResponse API response
     */
    private void sendResponse(final RoutingContext context, final ApiResponse apiResponse) {
        context
                .response()
                .setStatusCode(apiResponse.getStatus())
                .end(apiResponse.toJson().encode());
    }

}
