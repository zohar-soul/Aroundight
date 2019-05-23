package org.gosky.aroundight.verticle;

import java.util.HashSet;
import java.util.Set;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

/**
 * @Auther: guozhong
 * @Date: 2019-02-24 14:50
 * @Description:
 */

public abstract class RestVerticle extends AbstractVerticle {
    protected Router router;


    @Override
    public void start() throws Exception {
        super.start();
        router = Router.router(vertx);

        // CORS support
        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("x-requested-with");
//        allowedHeaders.add("Access-Control-Allow-Origin");
        allowedHeaders.add("Access-Control-Allow-Headers");
        allowedHeaders.add("origin");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("accept");
        allowedHeaders.add("authorization");
        allowedHeaders.add("X-PINGARUNER");

        Set<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.OPTIONS);
        /*
         * these methods aren't necessary for this sample,
         * but you may need them for your projects
         */
        allowedMethods.add(HttpMethod.DELETE);
        allowedMethods.add(HttpMethod.PATCH);
        allowedMethods.add(HttpMethod.PUT);

        router.route().handler(CorsHandler.create("*").allowedHeaders(allowedHeaders).allowedMethods(allowedMethods));
        router.route().handler(BodyHandler.create()); // <3>

        initRouter();

        vertx.createHttpServer().requestHandler(router).listen(8080);

    }

    protected abstract void initRouter();
}
