package org.example.microservicecommon;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.example.microservicecommon.exception.ResourceNotFoundException;
import org.example.microservicecommon.exception.RestApiException;
import org.example.microservicecommon.http.AbstractQueryPageAndSort;
import org.example.microservicecommon.http.ApiResponse;
import org.example.microservicecommon.http.Sort;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static org.apache.commons.lang3.StringUtils.upperCase;

/**
 * Abstract verticle containing helper methods for RESTful APIs.
 */
public abstract class RestApiVerticle extends BaseMicroserviceVerticle {

    protected Router apiRouter;

    @Override
    public void start() {
        super.start();
        apiRouter = Router.router(vertx);

        // validate page and sort params
        apiRouter
                .route("/*")
                .handler(ctx -> {
                    validatePageParams(ctx);
                    validateSortParams(ctx);

                    ctx.next();
                });

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

    /**
     * Validate sort parameters and return 400 response if invalid
     * @param context request context
     */
    private void validateSortParams(final RoutingContext context) {
        final List<String> sortFields = context.queryParam(AbstractQueryPageAndSort.sortKey);
        final Set<String> parsedSortFields = new HashSet<>();

        for (final String sortField : sortFields) {
            if (isBlank(sortField)) {
                continue;
            }

            final String[] fieldSplit = sortField.split(",");
            final List<String> sortDirections = Arrays.asList(
                    Sort.Direction.ASC.name(),
                    Sort.Direction.DESC.name()
            );

            if (fieldSplit.length > 2) {
                final ApiResponse.Error errorBody = new ApiResponse.Error("invalid sort param");
                sendResponse(context, new ApiResponse(HttpResponseStatus.BAD_REQUEST.code(), errorBody));
            }

            // validate sort order
            if (fieldSplit.length == 2
                    && !sortDirections.contains(upperCase(fieldSplit[1]))) {
                final ApiResponse.Error errorBody = new ApiResponse.Error("invalid sort order");
                sendResponse(context, new ApiResponse(HttpResponseStatus.BAD_REQUEST.code(), errorBody));
            }

            // handle duplicate sort field
            if (!parsedSortFields.add(fieldSplit[0])) {
                final ApiResponse.Error errorBody = new ApiResponse.Error("duplicate sort field");
                sendResponse(context, new ApiResponse(HttpResponseStatus.BAD_REQUEST.code(), errorBody));
            }
        }
    }

    /**
     * Validate pagination parameters and return 400 response if invalid
     * @param context request context
     */
    private void validatePageParams(final RoutingContext context) {
        final String pageField = context.queryParams().get(AbstractQueryPageAndSort.pageKey);
        final String sizeField = context.queryParams().get(AbstractQueryPageAndSort.sizeKey);

        if (isNotBlank(pageField) && !isNumeric(pageField)) {
            final ApiResponse.Error errorBody = new ApiResponse.Error("invalid page param");
            sendResponse(context, new ApiResponse(HttpResponseStatus.BAD_REQUEST.code(), errorBody));
        }

        if (isNotBlank(sizeField) && !isNumeric(sizeField)) {
            final ApiResponse.Error errorBody = new ApiResponse.Error("invalid size param");
            sendResponse(context, new ApiResponse(HttpResponseStatus.BAD_REQUEST.code(), errorBody));
        } else if (isNumeric(sizeField) && Integer.valueOf(sizeField) <= 0) {
            final ApiResponse.Error errorBody = new ApiResponse.Error("size param should be greater than 0");
            sendResponse(context, new ApiResponse(HttpResponseStatus.BAD_REQUEST.code(), errorBody));
        }
    }

}
